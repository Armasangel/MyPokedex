package com.uvg.mypokedex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.uvg.mypokedex.data.model.PokeType
import com.uvg.mypokedex.data.model.Pokemon
import com.uvg.mypokedex.data.model.bugColor
import com.uvg.mypokedex.data.model.darkColor
import com.uvg.mypokedex.data.model.dragonColor
import com.uvg.mypokedex.data.model.electricColor
import com.uvg.mypokedex.data.model.fairyColor
import com.uvg.mypokedex.data.model.fightingColor
import com.uvg.mypokedex.data.model.fireColor
import com.uvg.mypokedex.data.model.flyingColor
import com.uvg.mypokedex.data.model.ghostColor
import com.uvg.mypokedex.data.model.grassColor
import com.uvg.mypokedex.data.model.groundColor
import com.uvg.mypokedex.data.model.iceColor
import com.uvg.mypokedex.data.model.normalColor
import com.uvg.mypokedex.data.model.poisonColor
import com.uvg.mypokedex.data.model.psychicColor
import com.uvg.mypokedex.data.model.rockColor
import com.uvg.mypokedex.data.model.steelColor
import com.uvg.mypokedex.data.model.waterColor
import com.uvg.mypokedex.ui.theme.AppTypography
import java.util.Locale.getDefault

// Referencia https://stackoverflow.com/questions/48018091/kotlin-return-can-be-lifted-out-of-when

fun getTypeColor(type: PokeType): Color {
    return when (type) {
        PokeType.BUG -> bugColor
        PokeType.DRAGON -> dragonColor
        PokeType.FAIRY -> fairyColor
        PokeType.FIRE -> fireColor
        PokeType.GHOST -> ghostColor
        PokeType.GROUND -> groundColor
        PokeType.NORMAL -> normalColor
        PokeType.PSYCHIC -> psychicColor
        PokeType.STEEL -> steelColor
        PokeType.DARK -> darkColor
        PokeType.ELECTRIC -> electricColor
        PokeType.FIGHTING -> fightingColor
        PokeType.FLYING -> flyingColor
        PokeType.GRASS -> grassColor
        PokeType.ICE -> iceColor
        PokeType.POISON -> poisonColor
        PokeType.ROCK -> rockColor
        PokeType.WATER -> waterColor
    }
}

@Composable
fun PokemonCard(
    pokemon: Pokemon, onCardClick: (Pokemon) -> Unit = {}, 
    modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onCardClick(pokemon) },
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = getTypeColor(pokemon.type.first())
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = "https://raw.githubusercontent.com/PokeAPI/sprites/refs/heads/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png",
                contentDescription = null,
                modifier = Modifier.padding(10.dp)
            )
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val lowercaseName = (pokemon.name).lowercase()
                    val capitalizedName = lowercaseName.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            getDefault()
                        ) else it.toString()
                    }
                    Text(
                        text = (capitalizedName),
                        style = AppTypography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "#0${pokemon.id}",
                        style = AppTypography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
