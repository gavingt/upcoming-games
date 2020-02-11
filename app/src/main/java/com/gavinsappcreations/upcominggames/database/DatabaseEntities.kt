package com.gavinsappcreations.upcominggames.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gavinsappcreations.upcominggames.domain.Game

@Entity
data class DatabaseGame constructor(
    @PrimaryKey
    val releaseId: Int,
    val deck: String?,
    val description: String?,
    val gameName: String,
    val originalGameRating: String?,
    val imageUrl: String,
    val platforms: List<String>?,
    val releaseDateInMillis: Long?
)


fun List<DatabaseGame>.asDomainModel(): List<Game> {
    return map {
        Game(
            releaseId = it.releaseId,
            deck = it.deck,
            description = it.description,
            gameName = it.gameName,
            originalGameRating = it.originalGameRating,
            imageUrl = it.imageUrl,
            platforms = it.platforms,
            releaseDateInMillis = it.releaseDateInMillis
        )
    }
}

fun DatabaseGame.asDomainModel(): Game {
    return Game(
            releaseId = this.releaseId,
            deck = this.deck,
            description = this.description,
            gameName = this.gameName,
            originalGameRating = this.originalGameRating,
            imageUrl = this.imageUrl,
            platforms = this.platforms,
            releaseDateInMillis = this.releaseDateInMillis
        )
}