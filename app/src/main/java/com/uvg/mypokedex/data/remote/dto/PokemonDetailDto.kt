package com.uvg.mypokedex.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.uvg.mypokedex.data.model.PokemonStats

/**
 * DTO para la respuesta de detalle de Pokémon de la API
 */
data class PokemonDetailDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("weight")
    val weight: Int,
    @SerializedName("types")
    val types: List<TypeSlot>,
    @SerializedName("stats")
    val stats: List<StatDto>,
    @SerializedName("sprites")
    val sprites: SpritesDto
)

data class TypeSlot(
    @SerializedName("slot")
    val slot: Int,
    @SerializedName("type")
    val type: TypeInfo
)

data class TypeInfo(
    @SerializedName("name")
    val name: String
)

data class StatDto(
    @SerializedName("base_stat")
    val baseStat: Int,
    @SerializedName("stat")
    val stat: StatInfo
)

data class StatInfo(
    @SerializedName("name")
    val name: String
)

data class SpritesDto(
    @SerializedName("other")
    val other: OtherSprites?
)

data class OtherSprites(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtwork?
)

data class OfficialArtwork(
    @SerializedName("front_default")
    val frontDefault: String?
)

fun PokemonDetailDto.toDomain(): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        type = types.map { it.type.name },
        weight = weight / 10f,
        height = height / 10f,
        stats = stats.map { statDto ->
            PokemonStats(
                name = statDto.stat.name.replace("-", " ").replaceFirstChar { it.uppercase() },
                value = statDto.baseStat,
                maxValue = 200
            )
        },
        imageUrl = sprites.other?.officialArtwork?.frontDefault 
            ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${id}.png"
    )
}
