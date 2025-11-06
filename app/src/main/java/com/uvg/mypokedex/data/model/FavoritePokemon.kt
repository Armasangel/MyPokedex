package com.uvg.mypokedex.data.model

data class FavoritePokemon(
    val pokemonId: Int = 0,
    val pokemonName: String = "",
    val imageUrl: String = "",
    val addedAt: Long = System.currentTimeMillis(),
    val userId: String = ""
)
