package com.uvg.mypokedex.ui.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uvg.mypokedex.ui.components.PokemonCard

@Composable
fun HomeScreen(paddingValues: PaddingValues, viewModel: HomeViewModel = HomeViewModel()) {
    val pokemonList = viewModel.getPokemonList()
    LazyVerticalGrid(
        modifier = Modifier.padding(paddingValues),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        columns = GridCells.Fixed(2)
    ) {
        items(pokemonList) { pokemon ->
            PokemonCard(pokemon)
        }
    }

}
// Se agradece formalmente a Denil Parada por su paciencia y guía para dicho código
