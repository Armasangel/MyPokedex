package com.uvg.mypokedex.navigation

sealed class AppScreens(val route: String) {
    data object Home : AppScreens("home")
    data object Detail : AppScreens("detail/{pokemonId}") {
        fun createRoute(pokemonId: Int) = "detail/$pokemonId"
    }
    data object Favorites : AppScreens("favorites")
    data object Exchange : AppScreens("exchange")
}
