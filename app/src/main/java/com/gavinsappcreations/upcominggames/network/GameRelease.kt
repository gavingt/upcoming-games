package com.gavinsappcreations.upcominggames.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameRelease(
    @Json(name = "api_detail_url") val apiDetailUrl: String,
    @Json(name = "date_added") val dateAdded: String,
    @Json(name = "date_last_updated") val dateLastUpdated: String,
    val deck: String,
    val description: String,
    @Json(name = "expected_release_day") val expectedReleaseDay: Int?,
    @Json(name = "expected_release_month") val expectedReleaseMonth: Int?,
    @Json(name = "expected_release_quarter") val expectedReleaseQuarter: Int?,
    @Json(name = "expected_release_year") val expectedReleaseYear: Int?,
    val game: String,
    @Json(name = "game_rating") val gameRating: String,
    val guid: String,
    val id: Int,
    val image: String,
    @Json(name = "maximum_players") val maximumPlayers: Int,
    @Json(name = "minimum_players") val minimumPlayers: Int,
    @Json(name = "multiplayer_features") val multiplayerFeatures: String,
    val name: String,
    val platform: String,
    @Json(name = "product_code_type") val productCodeType: String,
    @Json(name = "product_code_value") val productCodeValue: String,
    val region: String,
    @Json(name = "release_date") val releaseDate: String,
    val resolutions: String,
    @Json(name = "singleplayer_features") val singleplayerFeatures: String,
    @Json(name = "sound_systems") val soundSystems: String,
    @Json(name = "site_detail_url") val siteDetailUrl: String,
    @Json(name = "widescreen_support") val widescreenSupport: String
) : Parcelable  // We make this a Parcelable so we can pass it from ListFragment to DetailFragment
