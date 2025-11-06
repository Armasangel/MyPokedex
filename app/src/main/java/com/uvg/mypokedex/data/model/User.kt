package com.uvg.mypokedex.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
