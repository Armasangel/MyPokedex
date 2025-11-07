package com.uvg.mypokedex.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.uvg.mypokedex.data.model.PokeType
import com.uvg.mypokedex.data.model.Pokemon

@Entity(tableName = "cached_pokemon")
@TypeConverters(Converters::class)
data class CachedPokemon(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val height: Int,
    val weight: Int,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val specialAttack: Int,
    val specialDefense: Int,
    val speed: Int,
    val lastFetchedAt: Long = System.currentTimeMillis()
)

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
}

fun CachedPokemon.toDomain(): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = types.map { typeName -> PokeType.fromString(typeName) },
        height = height,
        weight = weight,
        hp = hp,
        attack = attack,
        defense = defense,
        specialAttack = specialAttack,
        specialDefense = specialDefense,
        speed = speed
    )
}

fun Pokemon.toCache(): CachedPokemon {
    return CachedPokemon(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = types.map { it.name },
        height = height,
        weight = weight,
        hp = hp,
        attack = attack,
        defense = defense,
        specialAttack = specialAttack,
        specialDefense = specialDefense,
        speed = speed,
        lastFetchedAt = System.currentTimeMillis()
    )
}
