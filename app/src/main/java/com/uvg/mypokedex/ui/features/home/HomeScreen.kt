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
