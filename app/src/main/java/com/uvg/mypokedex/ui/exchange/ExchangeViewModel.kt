package com.uvg.mypokedex.ui.exchange


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.mypokedex.data.firebase.AuthRepository
import com.uvg.mypokedex.data.firebase.ExchangeRepository
import com.uvg.mypokedex.data.firebase.FavoritesRepository
import com.uvg.mypokedex.data.model.ExchangeRequest
import com.uvg.mypokedex.data.model.FavoritePokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExchangeViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val exchangeRepository = ExchangeRepository()
    private val favoritesRepository = FavoritesRepository()

    private val _uiState = MutableStateFlow<ExchangeUiState>(ExchangeUiState.Idle)
    val uiState: StateFlow<ExchangeUiState> = _uiState.asStateFlow()

    private val _myFavorites = MutableStateFlow<List<FavoritePokemon>>(emptyList())
    val myFavorites: StateFlow<List<FavoritePokemon>> = _myFavorites.asStateFlow()

    private val _selectedPokemon = MutableStateFlow<FavoritePokemon?>(null)
    val selectedPokemon: StateFlow<FavoritePokemon?> = _selectedPokemon.asStateFlow()

    private val _scannedUserId = MutableStateFlow<String?>(null)
    val scannedUserId: StateFlow<String?> = _scannedUserId.asStateFlow()

    private val _otherUserFavorites = MutableStateFlow<List<FavoritePokemon>>(emptyList())
    val otherUserFavorites: StateFlow<List<FavoritePokemon>> = _otherUserFavorites.asStateFlow()

    private val _selectedOtherPokemon = MutableStateFlow<FavoritePokemon?>(null)
    val selectedOtherPokemon: StateFlow<FavoritePokemon?> = _selectedOtherPokemon.asStateFlow()

    init {
        loadMyFavorites()
    }

    private fun loadMyFavorites() {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            favoritesRepository.observeFavorites(userId).collect { favorites ->
                _myFavorites.value = favorites
            }
        }
    }

    fun selectMyPokemon(pokemon: FavoritePokemon) {
        _selectedPokemon.value = pokemon
    }

    fun selectOtherPokemon(pokemon: FavoritePokemon) {
        _selectedOtherPokemon.value = pokemon
    }

    fun onQRScanned(qrContent: String) {
        // El QR contiene el userId del otro usuario
        _scannedUserId.value = qrContent
        loadOtherUserFavorites(qrContent)
    }

    private fun loadOtherUserFavorites(otherUserId: String) {
        viewModelScope.launch {
            _uiState.value = ExchangeUiState.LoadingOtherUser
            val result = favoritesRepository.getFavorites(otherUserId)

            if (result.isSuccess) {
                val favorites = result.getOrNull() ?: emptyList()
                _otherUserFavorites.value = favorites
                _uiState.value = ExchangeUiState.ReadyToExchange
            } else {
                _uiState.value = ExchangeUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al cargar favoritos del otro usuario"
                )
            }
        }
    }

    fun createAndExecuteExchange() {
        val myUserId = authRepository.currentUserId
        val otherUserId = _scannedUserId.value
        val myPokemon = _selectedPokemon.value
        val otherPokemon = _selectedOtherPokemon.value

        if (myUserId == null || otherUserId == null || myPokemon == null || otherPokemon == null) {
            _uiState.value = ExchangeUiState.Error("Faltan datos para realizar el intercambio")
            return
        }

        viewModelScope.launch {
            _uiState.value = ExchangeUiState.Exchanging

            // Crear solicitud de intercambio
            val createResult = exchangeRepository.createExchangeRequest(
                userAId = myUserId,
                userBId = otherUserId,
                pokemonAId = myPokemon.pokemonId,
                pokemonBId = otherPokemon.pokemonId
            )

            if (createResult.isFailure) {
                _uiState.value = ExchangeUiState.Error(
                    createResult.exceptionOrNull()?.message ?: "Error al crear solicitud de intercambio"
                )
                return@launch
            }

            val exchangeId = createResult.getOrNull()!!

            // Ejecutar intercambio
            val executeResult = exchangeRepository.executeExchange(exchangeId)

            _uiState.value = if (executeResult.isSuccess) {
                ExchangeUiState.ExchangeSuccess(
                    myPokemonName = myPokemon.pokemonName,
                    otherPokemonName = otherPokemon.pokemonName
                )
            } else {
                val errorMessage = when (val exception = executeResult.exceptionOrNull()) {
                    is kotlinx.coroutines.TimeoutCancellationException ->
                        "Tiempo de espera agotado (90s). Intercambio cancelado."
                    else ->
                        exception?.message ?: "Error al ejecutar intercambio"
                }
                ExchangeUiState.Error(errorMessage)
            }
        }
    }

    fun resetExchange() {
        _uiState.value = ExchangeUiState.Idle
        _selectedPokemon.value = null
        _scannedUserId.value = null
        _otherUserFavorites.value = emptyList()
        _selectedOtherPokemon.value = null
    }

    fun clearError() {
        if (_uiState.value is ExchangeUiState.Error) {
            _uiState.value = ExchangeUiState.Idle
        }
    }

    fun getCurrentUserId(): String? {
        return authRepository.currentUserId
    }
}

sealed class ExchangeUiState {
    data object Idle : ExchangeUiState()
    data object LoadingOtherUser : ExchangeUiState()
    data object ReadyToExchange : ExchangeUiState()
    data object Exchanging : ExchangeUiState()
    data class ExchangeSuccess(
        val myPokemonName: String,
        val otherPokemonName: String
    ) : ExchangeUiState()
    data class Error(val message: String) : ExchangeUiState()
}