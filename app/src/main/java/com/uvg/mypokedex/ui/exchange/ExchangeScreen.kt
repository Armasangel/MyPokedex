package com.uvg.mypokedex.ui.exchange

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.uvg.mypokedex.data.model.FavoritePokemon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeScreen(
    onBackClick: () -> Unit,
    viewModel: ExchangeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val myFavorites by viewModel.myFavorites.collectAsState()
    val selectedPokemon by viewModel.selectedPokemon.collectAsState()
    val otherUserFavorites by viewModel.otherUserFavorites.collectAsState()
    val selectedOtherPokemon by viewModel.selectedOtherPokemon.collectAsState()
    val scannedUserId by viewModel.scannedUserId.collectAsState()

    var showQRGenerator by remember { mutableStateOf(false) }
    var showQRScanner by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Manejar estado de éxito
    LaunchedEffect(uiState) {
        if (uiState is ExchangeUiState.ExchangeSuccess) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Intercambiar Pokémon") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetExchange() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reiniciar"
                        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Paso 1: Seleccionar mi Pokémon
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedPokemon != null) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (selectedPokemon != null) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Paso 1: Selecciona tu Pokémon",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (selectedPokemon != null) {
                        PokemonSelectionCard(
                            pokemon = selectedPokemon!!,
                            isSelected = true,
                            onClick = { }
                        )
                    } else {
                        if (myFavorites.isEmpty()) {
                            Text(
                                text = "No tienes Pokémon favoritos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.height(200.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(myFavorites) { pokemon ->
                                    PokemonSelectionCard(
                                        pokemon = pokemon,
                                        isSelected = false,
                                        onClick = { viewModel.selectMyPokemon(pokemon) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Paso 2: Conectar con otro usuario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (scannedUserId != null) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (scannedUserId != null) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Paso 2: Conectar con otro usuario",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showQRGenerator = true },
                            modifier = Modifier.weight(1f),
                            enabled = selectedPokemon != null
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mostrar QR")
                        }
                        
                        Button(
                            onClick = { showQRScanner = true },
                            modifier = Modifier.weight(1f),
                            enabled = selectedPokemon != null
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Escanear QR")
                        }
                    }
                    
                    if (scannedUserId != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ Usuario conectado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Paso 3: Seleccionar Pokémon del otro usuario
            if (scannedUserId != null && otherUserFavorites.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedOtherPokemon != null) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (selectedOtherPokemon != null) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Paso 3: Selecciona el Pokémon a recibir",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (selectedOtherPokemon != null) {
                            PokemonSelectionCard(
                                pokemon = selectedOtherPokemon!!,
                                isSelected = true,
                                onClick = { }
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.height(200.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(otherUserFavorites) { pokemon ->
                                    PokemonSelectionCard(
                                        pokemon = pokemon,
                                        isSelected = false,
                                        onClick = { viewModel.selectOtherPokemon(pokemon) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Botón de intercambio
            if (selectedPokemon != null && selectedOtherPokemon != null) {
                Button(
                    onClick = { viewModel.createAndExecuteExchange() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is ExchangeUiState.Exchanging
                ) {
                    if (uiState is ExchangeUiState.Exchanging) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Intercambiando...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Realizar Intercambio")
                    }
                }
            }

            // Mensaje de error
            if (uiState is ExchangeUiState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (uiState as ExchangeUiState.Error).message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    // Dialogs
    if (showQRGenerator) {
        QRGeneratorDialog(
            userId = viewModel.getCurrentUserId() ?: "",
            onDismiss = { showQRGenerator = false }
        )
    }

    if (showQRScanner) {
        QRScannerScreen(
            onQRScanned = { qrContent ->
                viewModel.onQRScanned(qrContent)
                showQRScanner = false
            },
            onDismiss = { showQRScanner = false }
        )
    }

    if (showSuccessDialog && uiState is ExchangeUiState.ExchangeSuccess) {
        val successState = uiState as ExchangeUiState.ExchangeSuccess
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                viewModel.resetExchange()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("¡Intercambio Exitoso!") },
            text = {
                Text(
                    "Has intercambiado ${successState.myPokemonName} por ${successState.otherPokemonName}",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.resetExchange()
                        onBackClick()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
fun PokemonSelectionCard(
    pokemon: FavoritePokemon,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (!isSelected) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .then(
                if (isSelected) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ) else Modifier
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.pokemonName,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pokemon.pokemonName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "#${pokemon.pokemonId.toString().padStart(3, '0')}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Seleccionado",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
