package com.uvg.mypokedex.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.mypokedex.data.firebase.AuthRepository
import com.uvg.mypokedex.data.firebase.FavoritesRepository
import com.uvg.mypokedex.data.model.FavoritePokemon
import com.uvg.mypokedex.data.model.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val favoritesRepository = FavoritesRepository()

    private val _favorites = MutableStateFlow<List<FavoritePokemon>>(emptyList())
    val favorites: StateFlow<List<FavoritePokemon>> = _favorites.asStateFlow()

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        val userId = authRepository.currentUserId
        if (userId != null) {
            viewModelScope.launch {
                favoritesRepository.observeFavorites(userId).collect { favoritesList ->
                    _favorites.value = favoritesList
                    _uiState.value = if (favoritesList.isEmpty()) {
                        FavoritesUiState.Empty
                    } else {
                        FavoritesUiState.Success(favoritesList)
                    }
                }
            }
        } else {
            _uiState.value = FavoritesUiState.Error("Usuario no autenticado")
        }
    }

    fun addFavorite(pokemon: Pokemon) {
        val userId = authRepository.currentUserId ?: return
        
        viewModelScope.launch {
            val favoritePokemon = FavoritePokemon(
                pokemonId = pokemon.id,
                pokemonName = pokemon.name,
                imageUrl = pokemon.imageUrl,
                addedAt = System.currentTimeMillis(),
                userId = userId
            )
            
            val result = favoritesRepository.addFavorite(userId, favoritePokemon)
            if (result.isFailure) {
                _uiState.value = FavoritesUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al agregar favorito"
                )
            }
        }
    }

    fun removeFavorite(pokemonId: Int) {
        val userId = authRepository.currentUserId ?: return
        
        viewModelScope.launch {
            val result = favoritesRepository.removeFavorite(userId, pokemonId)
            if (result.isFailure) {
                _uiState.value = FavoritesUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al remover favorito"
                )
            }
        }
    }

    suspend fun isFavorite(pokemonId: Int): Boolean {
        val userId = authRepository.currentUserId ?: return false
        return favoritesRepository.isFavorite(userId, pokemonId)
    }

    fun clearError() {
        if (_uiState.value is FavoritesUiState.Error) {
            _uiState.value = if (_favorites.value.isEmpty()) {
                FavoritesUiState.Empty
            } else {
                FavoritesUiState.Success(_favorites.value)
            }
        }
    }
}

sealed class FavoritesUiState {
    data object Loading : FavoritesUiState()
    data object Empty : FavoritesUiState()
    data class Success(val favorites: List<FavoritePokemon>) : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
}
