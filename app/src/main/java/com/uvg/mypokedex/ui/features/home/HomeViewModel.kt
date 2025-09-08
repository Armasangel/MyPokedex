package com.uvg.mypokedex.ui.features.home

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.uvg.mypokedex.data.model.Pokemon
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.FileNotFoundException

class HomeViewModel(private val context: Context) {

    private val _pokemonList = mutableStateListOf<Pokemon>()
    val pokemonList: SnapshotStateList<Pokemon> = _pokemonList
    private var currentPage = 0
    private val json = Json { 
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    init {
        loadMorePokemon()
    }
    

    fun loadMorePokemon() {
        val fileName = generateFileName(currentPage)
        val newPokemon = loadPokemonFromJson(context, fileName)
        
        if (newPokemon.isNotEmpty()) {
            _pokemonList.addAll(newPokemon)
            currentPage++
        }
       
    }
    private fun generateFileName(page: Int): String {
        val startId = (page * 10) + 1
        val endId = startId + 9
        return "pokemon_${startId.toString().padStart(3, '0')}_${endId.toString().padStart(3, '0')}.json"
    }
    private fun loadPokemonFromJson(context: Context, fileName: String): List<Pokemon> {
        try {
            val jsonString: String = context.assets.open(fileName)
                .bufferedReader()
                .use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val itemsArrayString = jsonObject.getJSONArray("items").toString()
   
            return json.decodeFromString<List<Pokemon>>(itemsArrayString)
        } catch (e: FileNotFoundException) {
            println("Archivo no encontrado: $fileName - Fin de los datos alcanzado")
            return emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}