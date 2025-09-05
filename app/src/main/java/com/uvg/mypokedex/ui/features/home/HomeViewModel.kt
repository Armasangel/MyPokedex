package com.uvg.mypokedex.ui.features.home

import androidx.lifecycle.ViewModel
import com.uvg.mypokedex.data.model.Pokemon

class HomeViewModel : ViewModel() {
    private val _pokemonList = mutableListOf(
        Pokemon(1, "bulbasaur", "grass", 6.9f, "0.7"),
        Pokemon(4, "charmander", "fire", "8", "0.61"),
        Pokemon(7, "squirtle", "water", "9", "0.5"),
        Pokemon(25, "pikachu", "electric", "6", "0.4"),
        Pokemon(150, "mewtwo", "psychic", ),
        Pokemon(151, "mew", "psychic"),
        Pokemon(2, "ivysaur", "grass"),
        Pokemon(3, "venusaur", "grass"),
        Pokemon(5, "charmeleon", "fire"),
        Pokemon(6, "charizard", "fire"),
        Pokemon(8, "wartortle", "water"),
        Pokemon(9, "blastoise", "water"),
        Pokemon(10, "caterpie", "bug")
    )

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