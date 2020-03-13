package com.gavinsappcreations.upcominggames.network

import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.domain.GameDetail
import com.gavinsappcreations.upcominggames.utilities.DateFormat
import com.gavinsappcreations.upcominggames.utilities.allKnownPlatforms
import com.gavinsappcreations.upcominggames.utilities.fetchReleaseDateInMillis
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

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


@JsonClass(generateAdapter = true)
data class NetworkRating(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    val id: String,
    @Json(name = "name") val ratingName: String
)

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

@JsonClass(generateAdapter = true)
data class NetworkPlatform(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    @Json(name = "id") val platformId: Int,
    @Json(name = "name") val platformName: String,
    @Json(name = "site_detail_url") val sideDetailUrl: String,
    val abbreviation: String
)


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


@JsonClass(generateAdapter = true)
data class GenericContainer(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    val id: Int,
    val name: String,
    @Json(name = "site_detail_url") val sideDetailUrl: String
)


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


/**
 * Convert Network results to database objects that we can store in our database
 */
fun List<NetworkGame>.asDatabaseModel(): List<Game> {

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
            guid = networkGame.guid
        )
    }
}


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