package com.uvg.mypokedex.ui.features.home

import com.uvg.mypokedex.data.model.PokeType
import com.uvg.mypokedex.data.model.Pokemon

class HomeViewModel {
    fun getPokemonList(): List<Pokemon> {
        return listOf(
            Pokemon(
                id = 1,
                name = "Bulbasaur",
                height = 0.7,
                weight = 6.9,
                category = "Semilla",
                abilities = "Espesura",
                gender = "Macho, Hembra",
                type = listOf(PokeType.GRASS),
                weaknesses = "Fuego · Hielo · Volador · Psíquica"
            ), Pokemon(
                id = 7,
                name = "Squirtle",
                height = 0.5,
                weight = 9,
                category = "Tortuguita",
                abilities = "Torrente",
                gender = "Macho, Hembra",
                type = listOf(PokeType.WATER),
                weaknesses = "Eléctrico · Planta"
            ), Pokemon(
                id = 4,
                name = "Charmander",
                height = 0.61,
                weight = 8,
                category = "Lagartija",
                abilities = "Mar Llamas",
                gender = "Macho, Hembra",
                type = listOf(PokeType.FIRE),
                weaknesses = "Agua · Eléctrico · Roca"
            ), Pokemon(
                id = 25,
                name = "Pikachu",
                height = 0.4,
                weight = 6.0,
                category = "Ratón",
                abilities = "Estática",
                gender = "Macho, Hembra",
                type = listOf(PokeType.ELECTRIC),
                weaknesses = "Tierra"
            )
            )
        )
    }
}
// Se agradece formalmente a Denil Parada por su paciencia y guía para dicho código
