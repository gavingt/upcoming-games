package com.gavinsappcreations.upcominggames.network

import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.domain.GameDetail
import com.gavinsappcreations.upcominggames.utilities.DateFormat
import com.gavinsappcreations.upcominggames.utilities.allKnownPlatforms
import com.gavinsappcreations.upcominggames.utilities.fetchReleaseDateInMillis
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

// Represents the root-level data returned from the "games" API endpoint.
@JsonClass(generateAdapter = true)
data class NetworkGameContainer(
    val error: String,
    val limit: Int,
    val offset: Int,
    @Json(name = "number_of_page_results") val numberOfPageResults: Int,
    @Json(name = "number_of_total_results") val numberOfTotalResults: Int,
    @Json(name = "status_code") val statusCode: Int,
    @Json(name = "results") val games: List<NetworkGame>
)

// Represents a single game's data returned from the "games" API endpoint.
@JsonClass(generateAdapter = true)
data class NetworkGame(
    @Json(name = "id") val gameId: Long,
    val guid: String,
    @Json(name = "name") val gameName: String,
    @Json(name = "image") val mainImage: NetworkMainImage,
    val platforms: List<NetworkPlatform>?,
    @Json(name = "original_release_date") val originalReleaseDate: String?,
    @Json(name = "expected_release_year") val expectedReleaseYear: Int?,
    @Json(name = "expected_release_quarter") val expectedReleaseQuarter: Int?,
    @Json(name = "expected_release_month") val expectedReleaseMonth: Int?,
    @Json(name = "expected_release_day") val expectedReleaseDay: Int?
)

// Represents the network rating returned from the API.
@JsonClass(generateAdapter = true)
data class NetworkRating(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    val id: String,
    @Json(name = "name") val ratingName: String
)

// Represents various URLs for the main image of a game from the API.
@JsonClass(generateAdapter = true)
data class NetworkMainImage(
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

// Represents a single platform from the API.
@JsonClass(generateAdapter = true)
data class NetworkPlatform(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    @Json(name = "id") val platformId: Int,
    @Json(name = "name") val platformName: String,
    @Json(name = "site_detail_url") val sideDetailUrl: String,
    val abbreviation: String
)

// Represents the root-level data returned by the "game" API endpoint.
@JsonClass(generateAdapter = true)
data class NetworkGameDetailContainer(
    val error: String,
    val limit: Int,
    val offset: Int,
    @Json(name = "number_of_page_results") val numberOfPageResults: Int,
    @Json(name = "number_of_total_results") val numberOfTotalResults: Int,
    @Json(name = "status_code") val statusCode: Int,
    @Json(name = "results") val gameDetails: NetworkGameDetail
)


// Represents game details for a single game returned by the "game" API endpoint.
@JsonClass(generateAdapter = true)
data class NetworkGameDetail(
    @Json(name = "id") val gameId: Long,
    val guid: String,
    @Json(name = "name") val gameName: String,
    @Json(name = "image") val mainImageUrl: NetworkMainImage,
    val images: List<NetworkImage>?,
    val platforms: List<NetworkPlatform>?,
    @Json(name = "original_release_date") val originalReleaseDate: String?,
    @Json(name = "expected_release_year") val expectedReleaseYear: Int?,
    @Json(name = "expected_release_quarter") val expectedReleaseQuarter: Int?,
    @Json(name = "expected_release_month") val expectedReleaseMonth: Int?,
    @Json(name = "expected_release_day") val expectedReleaseDay: Int?,
    @Json(name = "original_game_rating") val originalGameRating: List<NetworkRating>?,
    val developers: List<GenericContainer>?,
    val publishers: List<GenericContainer>?,
    val genres: List<GenericContainer>?,
    val deck: String?,
    val description: String?,
    @Json(name = "site_detail_url") val detailUrl: String?
)

// Represents a generic object returned by the API (several objects share these same properties).
@JsonClass(generateAdapter = true)
data class GenericContainer(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    val id: Int,
    val name: String,
    @Json(name = "site_detail_url") val sideDetailUrl: String
)

// Represents a single image for a game from the "game" API endpoint, displayed at bottom of DetailFragment.
@JsonClass(generateAdapter = true)
data class NetworkImage(
    @Json(name = "icon_url") val iconUrl: String,
    @Json(name = "medium_url") val mediumUrl: String,
    @Json(name = "screen_url") val screenUrl: String,
    @Json(name = "small_url") val smallUrl: String,
    @Json(name = "super_url") val superUrl: String,
    @Json(name = "thumb_url") val thumbUrl: String,
    @Json(name = "tiny_url") val tinyUrl: String,
    val original: String,
    val tags: String?
)


// Converts network results from "games" API endpoint to domain objects.
fun List<NetworkGame>.asDomainModel(): List<Game> {
    return map { networkGame ->
        val releaseDateArray = fetchReleaseDateInMillis(
            networkGame.originalReleaseDate,
            networkGame.expectedReleaseYear,
            networkGame.expectedReleaseQuarter,
            networkGame.expectedReleaseMonth,
            networkGame.expectedReleaseDay
        )

        Game(
            gameId = networkGame.gameId,
            gameName = networkGame.gameName,
            mainImageUrl = networkGame.mainImage.smallUrl,
            platforms = networkGame.platforms?.map {
                it.abbreviation
            },
            releaseDateInMillis = releaseDateArray[0] as Long?,
            dateFormat = (releaseDateArray[1] as DateFormat).formatCode,
            guid = networkGame.guid,
            isFavorite = null
        )
    }
}


// Converts network results from "game" API endpoint to domain objects.
fun NetworkGameDetail.asDomainModel(): GameDetail {
    val releaseDateArray = fetchReleaseDateInMillis(
        this.originalReleaseDate,
        this.expectedReleaseYear,
        this.expectedReleaseQuarter,
        this.expectedReleaseMonth,
        this.expectedReleaseDay
    )

    return GameDetail(
        gameId = this.gameId,
        guid = this.guid,
        gameName = this.gameName,
        mainImageUrl = this.mainImageUrl.smallUrl,
        images = this.images?.filterImagesByTag(),
        platforms = this.platforms?.sortPlatformNamesByRelevance(),
        releaseDateInMillis = releaseDateArray[0] as Long?,
        dateFormat = (releaseDateArray[1] as DateFormat).formatCode,
        developers = this.developers?.map {
            it.name
        },
        publishers = this.publishers?.map {
            it.name
        },
        genres = this.genres?.map {
            it.name
        },
        gameRating = this.originalGameRating?.map {
            it.ratingName
        },
        deck = this.deck,
        detailUrl = this.detailUrl
    )
}


/**
 * If the list of images for a GameDetail object has a size greater than 3, this returns only the
 * images containing the tag "screenshot". If list size is less than 3, just return the whole list.
 */
fun List<NetworkImage>.filterImagesByTag(): List<String> {
    val filteredList = mutableListOf<String>()

    map {
        if (it.tags != null && it.tags.toLowerCase(Locale.getDefault()).contains("screenshot")) {
            filteredList.add(it.thumbUrl)
        }
    }

    // If filteredList.size > 3, return it. Otherwise return the original list.
    return if (filteredList.size > 3) {
        filteredList
    } else {
        map {
            it.thumbUrl
        }
    }
}


/**
 * Sorts the platforms for a Game by the order specified in [allKnownPlatforms]. This ensures that
 * the platforms are listed with the most popular and relevant ones first.
 */
fun List<NetworkPlatform>.sortPlatformNamesByRelevance(): List<String> {
    val platformNamesSorted = mutableListOf<String>()
    val knownPlatformIndices = mutableListOf<Int>()

    for (platform in this) {
        val currentPlatformIndex = allKnownPlatforms.indexOfFirst {
            platform.abbreviation == it.abbreviation
        }

        if (currentPlatformIndex == -1) {
            platformNamesSorted.add(platform.platformName)
        } else {
            knownPlatformIndices.add(currentPlatformIndex)
        }
    }

    knownPlatformIndices.sort()

    val knownPlatformsSorted = knownPlatformIndices.map {
        allKnownPlatforms[it].fullName
    }

    platformNamesSorted.addAll(knownPlatformsSorted)
    return platformNamesSorted
}


/**
 * Sorts the platforms for a Game by the order specified in [allKnownPlatforms]. This ensures that
 * the platforms are listed with the most popular and relevant ones first.
 */
fun List<String>.sortPlatformAbbreviationsByRelevance(): List<String> {

    val platformAbbreviationsSorted = mutableListOf<String>()
    val knownPlatformIndices = mutableListOf<Int>()

    for (platformAbbreviation in this) {
        val currentPlatformIndex = allKnownPlatforms.indexOfFirst {
            platformAbbreviation == it.abbreviation
        }

        if (currentPlatformIndex == -1) {
            platformAbbreviationsSorted.add(platformAbbreviation)
        } else {
            knownPlatformIndices.add(currentPlatformIndex)
        }
    }

    knownPlatformIndices.sort()

    val knownPlatformsSorted = knownPlatformIndices.map {
        allKnownPlatforms[it].abbreviation
    }

    platformAbbreviationsSorted.addAll(knownPlatformsSorted)
    return platformAbbreviationsSorted
}