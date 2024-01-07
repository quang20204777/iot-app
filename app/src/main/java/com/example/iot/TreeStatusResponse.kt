package com.example.iot

data class TreeStatusResponse(
    val humidity: Int,
    val temperature: Float,
    val createdAt: String
)
