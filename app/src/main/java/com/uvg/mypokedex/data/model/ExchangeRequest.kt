package com.uvg.mypokedex.data.model

data class ExchangeRequest(
    val id: String = "",
    val userAId: String = "",
    val userBId: String = "",
    val pokemonAId: Int = 0,
    val pokemonBId: Int = 0,
    val status: ExchangeStatus = ExchangeStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

enum class ExchangeStatus {
    PENDING,
    COMPLETED,
    CANCELLED,
    TIMEOUT
}