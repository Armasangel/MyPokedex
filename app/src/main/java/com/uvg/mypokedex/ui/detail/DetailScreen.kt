package com.uvg.mypokedex.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.uvg.mypokedex.data.model.Pokemon
import com.uvg.mypokedex.data.model.TypeColors
import com.uvg.mypokedex.ui.auth.AuthModal
import com.uvg.mypokedex.ui.auth.AuthViewModel
import com.uvg.mypokedex.ui.favorites.FavoritesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    pokemonId: Int,
    viewModel: DetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    val authViewModel: AuthViewModel = viewModel()
    val favoritesViewModel: FavoritesViewModel = viewModel()
    
    val currentUser by authViewModel.currentUser.collectAsState()
    var showAuthModal by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pokemonId) {
        viewModel.loadPokemonDetail(pokemonId)
    }

    // Verificar si es favorito cuando el usuario cambia
    LaunchedEffect(currentUser, pokemonId) {
        if (currentUser != null) {
            isFavorite = favoritesViewModel.isFavorite(pokemonId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokémon Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState is DetailUiState.Success) {
                        IconButton(
                            onClick = {
                                if (currentUser == null) {
                                    showAuthModal = true
                                } else {
                                    scope.launch {
                                        val pokemon = (uiState as DetailUiState.Success).pokemon
                                        if (isFavorite) {
                                            favoritesViewModel.removeFavorite(pokemon.id)
                                            isFavorite = false
                                        } else {
                                            favoritesViewModel.addFavorite(pokemon)
                                            isFavorite = true
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) 
                                    Icons.Default.Favorite 
                                else 
                                    Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) 
                                    "Remover de favoritos" 
                                else 
                                    "Agregar a favoritos",
                                tint = if (isFavorite) Color.Red else Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DetailUiState.Success -> {
                    PokemonDetailContent(
                        pokemon = state.pokemon,
                        isConnected = isConnected
                    )
                }
                is DetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.retry(pokemonId) }) {
                            Text("Retry")
                        }
                    }
                }
            }

            if (!isConnected) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Sin conexión - Mostrando datos en caché",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }

    if (showAuthModal) {
        AuthModal(
            onDismiss = { showAuthModal = false },
            onAuthSuccess = {
                showAuthModal = false
                scope.launch {
                    if (uiState is DetailUiState.Success) {
                        val pokemon = (uiState as DetailUiState.Success).pokemon
                        favoritesViewModel.addFavorite(pokemon)
                        isFavorite = true
                    }
                }
            }
        )
    }
}

@Composable
fun PokemonDetailContent(
    pokemon: Pokemon,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val primaryColor = TypeColors.getColor(pokemon.primaryType)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(primaryColor.copy(alpha = 0.3f))
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pokemon.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pokemon.formattedId,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pokemon.types.forEach { type ->
                    Surface(
                        color = TypeColors.getColor(type),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = type.name,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PokemonMeasurements(
                heightInMeters = pokemon.heightInMeters,
                weightInKg = pokemon.weightInKg
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Base Stats",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            PokemonStatRow(label = "HP", value = pokemon.hp, maxValue = pokemon.maxStat, color = Color(0xFFFF5959))
            PokemonStatRow(label = "Attack", value = pokemon.attack, maxValue = pokemon.maxStat, color = Color(0xFFF5AC78))
            PokemonStatRow(label = "Defense", value = pokemon.defense, maxValue = pokemon.maxStat, color = Color(0xFFFAE078))
            PokemonStatRow(label = "Sp. Atk", value = pokemon.specialAttack, maxValue = pokemon.maxStat, color = Color(0xFF9DB7F5))
            PokemonStatRow(label = "Sp. Def", value = pokemon.specialDefense, maxValue = pokemon.maxStat, color = Color(0xFFA7DB8D))
            PokemonStatRow(label = "Speed", value = pokemon.speed, maxValue = pokemon.maxStat, color = Color(0xFFFA92B2))
        }
    }
}
