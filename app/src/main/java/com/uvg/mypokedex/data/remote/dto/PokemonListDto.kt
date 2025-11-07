package com.uvg.mypokedex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonListDto(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<PokemonEntryDto>
)

data class PokemonEntryDto(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
) {
    val id: Int
        get() {
            val parts = url.trimEnd('/').split('/')
            return parts.last().toIntOrNull() ?: 0
        }
}