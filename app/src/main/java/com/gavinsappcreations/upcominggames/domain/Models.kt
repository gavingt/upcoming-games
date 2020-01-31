package com.gavinsappcreations.upcominggames.domain

data class Game(
    val releaseId: Int,
    val deck: String?,
    val description: String?,
    val gameName: String,
    val originalGameRating: String?,
    val imageUrl: String,
    val platforms: List<String>?,
    val releaseDate: String
)