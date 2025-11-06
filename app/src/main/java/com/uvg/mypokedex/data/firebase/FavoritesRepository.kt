package com.uvg.mypokedex.data.firebase

import com.google.firebase.database.*
import com.uvg.mypokedex.data.model.FavoritePokemon
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FavoritesRepository {
    
    private val database = FirebaseDatabase.getInstance()
    private val favoritesRef = database.getReference("favorites")

    suspend fun addFavorite(userId: String, pokemon: FavoritePokemon): Result<Unit> {
        return try {
            val favoriteWithUserId = pokemon.copy(userId = userId)
            favoritesRef
                .child(userId)
                .child(pokemon.pokemonId.toString())
                .setValue(favoriteWithUserId)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavorite(userId: String, pokemonId: Int): Result<Unit> {
        return try {
            favoritesRef
                .child(userId)
                .child(pokemonId.toString())
                .removeValue()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeFavorites(userId: String): Flow<List<FavoritePokemon>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorites = mutableListOf<FavoritePokemon>()
                snapshot.children.forEach { child ->
                    child.getValue(FavoritePokemon::class.java)?.let {
                        favorites.add(it)
                    }
                }
                trySend(favorites)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        favoritesRef.child(userId).addValueEventListener(listener)
        awaitClose { favoritesRef.child(userId).removeEventListener(listener) }
    }

    suspend fun getFavorites(userId: String): Result<List<FavoritePokemon>> {
        return try {
            val snapshot = favoritesRef.child(userId).get().await()
            val favorites = mutableListOf<FavoritePokemon>()
            snapshot.children.forEach { child ->
                child.getValue(FavoritePokemon::class.java)?.let {
                    favorites.add(it)
                }
            }
            Result.success(favorites)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isFavorite(userId: String, pokemonId: Int): Boolean {
        return try {
            val snapshot = favoritesRef
                .child(userId)
                .child(pokemonId.toString())
                .get()
                .await()
            snapshot.exists()
        } catch (e: Exception) {
            false
        }
    }
}
