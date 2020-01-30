package com.gavinsappcreations.upcominggames.domain

data class GameRelease(
    val releaseId: Int,
    val deck: String?,
    val description: String?,
    val gameName: String,
    val gameRating: String?,
    val image: String,
    val maximumPlayers: Int?,
    val minimumPlayers: Int?,
    val platform: String,
    val region: String,
    val releaseDate: String?,
    val expectedReleaseDay: Int?,
    val expectedReleaseMonth: Int?,
    val expectedReleaseYear: Int?,
    val expectedReleaseQuarter: Int?
)