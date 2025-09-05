package com.uvg.mypokedex.data.model

data class Pokemon(
    val id: Int,
    val name: String,
    val type: List<String>,
    val weight: Float,
    val height: Float,
    val stats: List<PokemonStat>,
    val isFavorite: Boolean = false,
    val imageUrl: String = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${id}.png"
)

data class PokemonStat(
    val name: String,
    val value: Int,
    val maxValue: Int = 200
)