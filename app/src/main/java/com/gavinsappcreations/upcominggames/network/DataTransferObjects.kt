package com.gavinsappcreations.upcominggames.network

import com.gavinsappcreations.upcominggames.App.Companion.applicationContext
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.utilities.formatReleaseDate
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*


/*"error":"OK",
"limit":100,
"offset":0,
"number_of_page_results":100,
"number_of_total_results":"213",
"status_code":1,
"results":[
{
    "deck":null,
    "description":null,
    "game":{
    "api_detail_url":"https:\/\/www.giantbomb.com\/api\/game\/3030-66595\/",
    "id":66595,
    "name":"Never Again"
},
    "image":{
    "icon_url":"https:\/\/www.giantbomb.com\/api\/image\/square_avatar\/3150303-4147793550-cq5da.png",
    "medium_url":"https:\/\/www.giantbomb.com\/api\/image\/scale_medium\/3150303-4147793550-cq5da.png",
    "screen_url":"https:\/\/www.giantbomb.com\/api\/image\/screen_medium\/3150303-4147793550-cq5da.png",
    "screen_large_url":"https:\/\/www.giantbomb.com\/api\/image\/screen_kubrick\/3150303-4147793550-cq5da.png",
    "small_url":"https:\/\/www.giantbomb.com\/api\/image\/scale_small\/3150303-4147793550-cq5da.png",
    "super_url":"https:\/\/www.giantbomb.com\/api\/image\/scale_large\/3150303-4147793550-cq5da.png",
    "thumb_url":"https:\/\/www.giantbomb.com\/api\/image\/scale_avatar\/3150303-4147793550-cq5da.png",
    "tiny_url":"https:\/\/www.giantbomb.com\/api\/image\/square_mini\/3150303-4147793550-cq5da.png",
    "original_url":"https:\/\/www.giantbomb.com\/api\/image\/original\/3150303-4147793550-cq5da.png",
    "image_tags":"All Images,Digital Switch Icons"
},
    "maximum_players":null,
    "minimum_players":1,
    "name":"Never Again (Digital)",
    "platform":{
    "api_detail_url":"https:\/\/www.giantbomb.com\/api\/platform\/3045-157\/",
    "id":157,
    "name":"Nintendo Switch"
},
    "region":{
    "api_detail_url":"https:\/\/www.giantbomb.com\/api\/region\/3075-1\/",
    "id":1,
    "name":"United States"
},
    "release_date":"2020-01-20 00:00:00"
},*/


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
    @Json(name = "id") val gameId: Int,
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
 * Convert Network results to domain objects that we can use in our app
 */
fun NetworkGame.asDomainModel(): Game {
    return Game(
        releaseId = this.gameId,
        deck = this.deck,
        description = this.description,
        gameName = this.gameName,
        originalGameRating = this.originalGameRating?.get(0)?.ratingName,
        imageUrl = this.image.smallUrl.replace("scale", "square"),
        platforms = this.platforms?.map {
            it.abbreviation
        },
        releaseDate = this.formatReleaseDate()
    )
}



