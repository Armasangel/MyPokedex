package com.uvg.mypokedex.data.firebase

import com.google.firebase.database.*
import com.uvg.mypokedex.data.model.ExchangeRequest
import com.uvg.mypokedex.data.model.ExchangeStatus
import com.uvg.mypokedex.data.model.FavoritePokemon
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ExchangeRepository {
    
    private val database = FirebaseDatabase.getInstance()
    private val exchangesRef = database.getReference("exchanges")
    private val favoritesRef = database.getReference("favorites")

    suspend fun createExchangeRequest(
        userAId: String,
        userBId: String,
        pokemonAId: Int,
        pokemonBId: Int
    ): Result<String> {
        return try {
            val exchangeId = exchangesRef.push().key 
                ?: return Result.failure(Exception("Failed to generate exchange ID"))
            
            val exchangeMap = mapOf(
                "id" to exchangeId,
                "userAId" to userAId,
                "userBId" to userBId,
                "pokemonAId" to pokemonAId,
                "pokemonBId" to pokemonBId,
                "status" to ExchangeStatus.PENDING.name,
                "createdAt" to ServerValue.TIMESTAMP
            )
            
            exchangesRef.child(exchangeId).setValue(exchangeMap).await()
            Result.success(exchangeId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeExchange(exchangeId: String): Result<Unit> {
        return try {
            // Timeout de 90 segundos
            withTimeout(90_000) {
                // Obtener datos del intercambio
                val exchangeSnapshot = exchangesRef.child(exchangeId).get().await()
                
                val userAId = exchangeSnapshot.child("userAId").getValue(String::class.java)
                    ?: return@withTimeout Result.failure(Exception("UserA ID not found"))
                val userBId = exchangeSnapshot.child("userBId").getValue(String::class.java)
                    ?: return@withTimeout Result.failure(Exception("UserB ID not found"))
                val pokemonAId = exchangeSnapshot.child("pokemonAId").getValue(Int::class.java)
                    ?: return@withTimeout Result.failure(Exception("PokemonA ID not found"))
                val pokemonBId = exchangeSnapshot.child("pokemonBId").getValue(Int::class.java)
                    ?: return@withTimeout Result.failure(Exception("PokemonB ID not found"))
                val status = exchangeSnapshot.child("status").getValue(String::class.java)
                    ?: return@withTimeout Result.failure(Exception("Status not found"))

                // Verificar que está pendiente
                if (status != ExchangeStatus.PENDING.name) {
                    return@withTimeout Result.failure(Exception("Exchange already processed"))
                }

                // Obtener los Pokémon favoritos de ambos usuarios
                val pokemonASnapshot = favoritesRef
                    .child(userAId)
                    .child(pokemonAId.toString())
                    .get()
                    .await()
                
                val pokemonBSnapshot = favoritesRef
                    .child(userBId)
                    .child(pokemonBId.toString())
                    .get()
                    .await()

                if (!pokemonASnapshot.exists() || !pokemonBSnapshot.exists()) {
                    return@withTimeout Result.failure(Exception("One or both Pokémon not found"))
                }

                val pokemonAMap = pokemonASnapshot.value as? Map<*, *>
                val pokemonBMap = pokemonBSnapshot.value as? Map<*, *>

                if (pokemonAMap == null || pokemonBMap == null) {
                    return@withTimeout Result.failure(Exception("Invalid Pokémon data"))
                }

                // Realizar transacción atómica
                suspendCancellableCoroutine { continuation ->
                    database.reference.runTransaction(object : Transaction.Handler {
                        override fun doTransaction(currentData: MutableData): Transaction.Result {
                            return try {
                                // Remover Pokémon A del usuario A
                                currentData.child("favorites")
                                    .child(userAId)
                                    .child(pokemonAId.toString())
                                    .value = null

                                // Remover Pokémon B del usuario B
                                currentData.child("favorites")
                                    .child(userBId)
                                    .child(pokemonBId.toString())
                                    .value = null

                                // Agregar Pokémon B al usuario A (actualizar userId)
                                val pokemonBForUserA = pokemonBMap.toMutableMap()
                                pokemonBForUserA["userId"] = userAId
                                currentData.child("favorites")
                                    .child(userAId)
                                    .child(pokemonBId.toString())
                                    .value = pokemonBForUserA

                                // Agregar Pokémon A al usuario B (actualizar userId)
                                val pokemonAForUserB = pokemonAMap.toMutableMap()
                                pokemonAForUserB["userId"] = userBId
                                currentData.child("favorites")
                                    .child(userBId)
                                    .child(pokemonAId.toString())
                                    .value = pokemonAForUserB

                                // Actualizar estado del intercambio
                                currentData.child("exchanges")
                                    .child(exchangeId)
                                    .child("status")
                                    .value = ExchangeStatus.COMPLETED.name

                                currentData.child("exchanges")
                                    .child(exchangeId)
                                    .child("completedAt")
                                    .value = ServerValue.TIMESTAMP

                                Transaction.success(currentData)
                            } catch (e: Exception) {
                                Transaction.abort()
                            }
                        }

                        override fun onComplete(
                            error: DatabaseError?,
                            committed: Boolean,
                            currentData: DataSnapshot?
                        ) {
                            if (error != null) {
                                continuation.resumeWithException(error.toException())
                            } else if (committed) {
                                continuation.resume(Unit)
                            } else {
                                continuation.resumeWithException(Exception("Transaction aborted"))
                            }
                        }
                    })
                }
                
                Result.success(Unit)
            }
        } catch (e: Exception) {
            // Rollback: marcar intercambio como cancelado
            try {
                exchangesRef.child(exchangeId).child("status")
                    .setValue(ExchangeStatus.CANCELLED.name).await()
            } catch (rollbackError: Exception) {
                // Log error pero devolver el error original
            }
            Result.failure(e)
        }
    }

    suspend fun getExchangeById(exchangeId: String): Result<ExchangeRequest> {
        return try {
            val snapshot = exchangesRef.child(exchangeId).get().await()
            
            val id = snapshot.child("id").getValue(String::class.java) ?: ""
            val userAId = snapshot.child("userAId").getValue(String::class.java) ?: ""
            val userBId = snapshot.child("userBId").getValue(String::class.java) ?: ""
            val pokemonAId = snapshot.child("pokemonAId").getValue(Int::class.java) ?: 0
            val pokemonBId = snapshot.child("pokemonBId").getValue(Int::class.java) ?: 0
            val statusString = snapshot.child("status").getValue(String::class.java) ?: "PENDING"
            val createdAt = snapshot.child("createdAt").getValue(Long::class.java) ?: 0L
            val completedAt = snapshot.child("completedAt").getValue(Long::class.java)
            
            val status = try {
                ExchangeStatus.valueOf(statusString)
            } catch (e: Exception) {
                ExchangeStatus.PENDING
            }
            
            val exchange = ExchangeRequest(
                id = id,
                userAId = userAId,
                userBId = userBId,
                pokemonAId = pokemonAId,
                pokemonBId = pokemonBId,
                status = status,
                createdAt = createdAt,
                completedAt = completedAt
            )
            
            Result.success(exchange)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelExchange(exchangeId: String): Result<Unit> {
        return try {
            exchangesRef.child(exchangeId).child("status")
                .setValue(ExchangeStatus.CANCELLED.name).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
