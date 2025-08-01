package com.uvg.mypokedex.data.model

data class Pokemon(
    val id: Int,
    val name: String,
    val type: String,
    val imageUrl: String = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${id}.png"
)