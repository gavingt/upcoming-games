package com.gavinsappcreations.upcominggames.network

import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.utilities.fetchReleaseDateInMillis
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class NetworkGamesContainer(
    val error: String,
    val limit: Int,
    val offset: Int,
    @Json(name = "number_of_page_results") val numberOfPageResults: Int,
    @Json(name = "number_of_total_results") val numberOfTotalResults: Int,
    @Json(name = "status_code") val statusCode: Int,
    @Json(name = "results") val games: List<NetworkGame>
)


@JsonClass(generateAdapter = true)
data class NetworkGame(
    @Json(name = "id") val gameId: Long,
    val deck: String?,
    val description: String?,
    @Json(name = "name") val gameName: String,
    @Json(name = "original_game_rating") val originalGameRating: List<NetworkRating>?,
    val image: NetworkImage,
    val platforms: List<NetworkPlatform>?,
    @Json(name = "original_release_date") val originalReleaseDate: String?,
    @Json(name = "expected_release_day") val expectedReleaseDay: Int?,
    @Json(name = "expected_release_month") val expectedReleaseMonth: Int?,
    @Json(name = "expected_release_year") val expectedReleaseYear: Int?
)


@JsonClass(generateAdapter = true)
data class NetworkRating(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    val id: String,
    @Json(name = "name") val ratingName: String
)

@JsonClass(generateAdapter = true)
data class NetworkImage(
    @Json(name = "icon_url") val iconUrl: String,
    @Json(name = "medium_url") val mediumUrl: String,
    @Json(name = "screen_url") val screenUrl: String,
    @Json(name = "screen_large_url") val screenLargeUrl: String,
    @Json(name = "small_url") val smallUrl: String,
    @Json(name = "super_url") val superUrl: String,
    @Json(name = "thumb_url") val thumbUrl: String,
    @Json(name = "tiny_url") val tinyUrl: String,
    @Json(name = "original_url") val originalUrl: String,
    @Json(name = "image_tags") val imageTags: String?
)

@JsonClass(generateAdapter = true)
data class NetworkPlatform(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    @Json(name = "id") val platformId: Int,
    @Json(name = "name") val platformName: String,
    @Json(name = "site_detail_url") val sideDetailUrl: String,
    val abbreviation: String
)


/**
 * Convert Network results to database objects that we can store in our database
 */
fun List<NetworkGame>.asDatabaseModel(): List<Game> {
    return map { networkGame ->

        Game(
            gameId = networkGame.gameId,
            deck = networkGame.deck,
            description = networkGame.description,
            gameName = networkGame.gameName,
            originalGameRating = networkGame.originalGameRating?.get(0)?.ratingName,
            imageUrl = networkGame.image.smallUrl,
            platforms = networkGame.platforms?.map {
                it.abbreviation
            },
            releaseDateInMillis = networkGame.fetchReleaseDateInMillis()
        )
    }
}


