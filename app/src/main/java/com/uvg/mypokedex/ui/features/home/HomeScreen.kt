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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uvg.mypokedex.ui.components.PokemonCard


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onPokemonClick: (Pokemon) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isAscending by remember { mutableStateOf(true) }
    val sortedPokemonList = if (isAscending) {
        viewModel.sortPokemonByName(true)
    } else {
        viewModel.sortPokemonByName(false)
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAscending = !isAscending }
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = if (isAscending) "Orden ascendente" else "Orden descendente"
                )
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(sortedPokemonList) { pokemon ->
                PokemonCard(
                    pokemon = pokemon,
                    onCardClick = { onPokemonClick(pokemon) }
                )
            }
        }
    }
}
@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val pokemonList by viewModel.pokemonList.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    
    val filteredPokemon = remember(pokemonList, searchQuery) {
        if (searchQuery.isBlank()) {
            pokemonList
        } else {
            pokemonList.filter { 
                it.name.contains(searchQuery, ignoreCase = true) 
            }
        }
    }
    
    Scaffold(
        topBar = {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {}
            )
        }
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            items(filteredPokemon) { pokemon ->
                PokemonListItem(pokemon = pokemon)
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Buscar")
        },
        placeholder = {
            Text("Busca al pokemon que desees :)")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}
// Se agradece formalmente a Denil Parada por su paciencia y guía para dicho código
