package com.gavinsappcreations.upcominggames.network

import com.gavinsappcreations.upcominggames.domain.GameRelease
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


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
data class NetworkReleasesContainer(
    val error: String,
    val limit: Int,
    val offset: Int,
    @Json(name = "number_of_page_results") val numberOfPageResults: Int,
    @Json(name = "number_of_total_results") val numberOfTotalResults: Int,
    @Json(name = "status_code") val statusCode: Int,
    @Json(name = "results") val releases: List<NetworkGameRelease>
)


@JsonClass(generateAdapter = true)
data class NetworkGameRelease(
    @Json(name = "id") val releaseId: Int,
    val deck: String?,
    val description: String?,
    val game: NetworkGame,
    @Json(name = "game_rating") val gameRating: NetworkRating?,
    val image: NetworkImage,
    @Json(name = "maximum_players") val maximumPlayers: Int?,
    @Json(name = "minimum_players") val minimumPlayers: Int,
    val platform: NetworkPlatform,
    val region: NetworkRegion,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "expected_release_day") val expectedReleaseDay: Int?,
    @Json(name = "expected_release_month") val expectedReleaseMonth: Int?,
    @Json(name = "expected_release_year") val expectedReleaseYear: Int?,
    @Json(name = "expected_release_quarter") val expectedReleaseQuarter: Int?
)





@JsonClass(generateAdapter = true)
data class NetworkGame(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    val id: String,
    @Json(name="name") val gameName: String
)

@JsonClass(generateAdapter = true)
data class NetworkRating(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    val id: String,
    @Json(name="name") val ratingName: String
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
    @Json(name = "image_tags") val imageTags: String
)

@JsonClass(generateAdapter = true)
data class NetworkPlatform(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    @Json(name = "id") val platformId: Int,
    @Json(name = "name") val platformName: String
)

@JsonClass(generateAdapter = true)
data class NetworkRegion(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    @Json(name = "id") val regionId: Int,
    @Json(name = "name") val regionName: String
)


/**
 * Convert Network results to domain objects that we can use in our app
 */
fun NetworkGameRelease.asDomainModel(): GameRelease {
    return GameRelease(
        releaseId = this.releaseId,
        deck = this.deck,
        description = this.description,
        gameName = this.game.gameName,
        gameRating = this.gameRating?.ratingName,
        imageUrl = this.image.smallUrl,
        maximumPlayers = this.maximumPlayers,
        minimumPlayers = this.minimumPlayers,
        platform = this.platform.platformName,
        region = this.region.regionName,
        releaseDate = this.releaseDate,
        expectedReleaseDay = this.expectedReleaseDay,
        expectedReleaseMonth = this.expectedReleaseMonth,
        expectedReleaseYear = this.expectedReleaseYear,
        expectedReleaseQuarter = this.expectedReleaseQuarter
    )
}
