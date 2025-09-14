package com.uvg.mypokedex.ui.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.uvg.mypokedex.ui.components.PokemonCard

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    onPokemonClick: (Int) -> Unit,
    onSearchToolsClick: () -> Unit,
    viewModel: HomeViewModel = HomeViewModel(LocalContext.current.applicationContext)
) {
    val pokemonList = viewModel.pokemonList
    val gridState = rememberLazyGridState()
    
    LaunchedEffect(Unit) {
        if (pokemonList.isEmpty()) {
            viewModel.loadMorePokemon()
        }
    }
    
    LaunchedEffect(gridState) {
        snapshotFlow {
            val layoutInfo = gridState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            
            // Activar carga cuando estemos cerca del final (últimos 5 elementos)
            lastVisibleItemIndex >= totalItems - 5
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && pokemonList.isNotEmpty()) {
                viewModel.loadMorePokemon()
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            state = gridState,
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            columns = GridCells.Fixed(2)
        ) {
            items(
                items = pokemonList,
                key = { pokemon -> pokemon.id } // Clave única para optimización
            ) { pokemon ->
                PokemonCard(
                    pokemon = pokemon,
                    onClick = { onPokemonClick(pokemon.id) }
                )
            }
        }
        
        // FAB para abrir el diálogo de herramientas de búsqueda
        FloatingActionButton(
            onClick = onSearchToolsClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Herramientas de búsqueda"
            )
        }
    }
}
