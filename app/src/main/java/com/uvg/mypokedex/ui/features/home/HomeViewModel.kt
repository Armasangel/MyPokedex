package com.uvg.mypokedex.ui.features.home

import androidx.lifecycle.ViewModel
import com.uvg.mypokedex.data.model.Pokemon

class HomeViewModel : ViewModel() {

    val pokemonList: List<Pokemon>
        get() = _pokemonList

    fun toggleFavorite(pokemonId: Int) {
        val index = _pokemonList.indexOfFirst { it.id == pokemonId }
        if (index != -1) {
            val pokemon = _pokemonList[index]
            _pokemonList[index] = pokemon.copy(isFavorite = !pokemon.isFavorite)
        }
    }

    fun sortPokemonByName(ascending: Boolean = true): List<Pokemon> {
        return if (ascending) {
            _pokemonList.sortedBy { it.name }
        } else {
            _pokemonList.sortedByDescending { it.name }
        }
    }
}