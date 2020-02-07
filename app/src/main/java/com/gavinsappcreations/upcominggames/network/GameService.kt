package com.gavinsappcreations.upcominggames.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * A retrofit service to fetch game info.
 */
interface GameService {
    @GET("games")
    suspend fun getGameData(
        @Query("api_key") apiKey: String,
        @Query("format") format: String,
        @Query("sort") sort: String,
        @Query("filter") filter: String,
        @Query("field_list") fieldList: String
    ): Response<NetworkGamesContainer>
}

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Main entry point for network access. Call like `GameNetwork.gameData.getGameData()`
 */
object GameNetwork {
    // Build our own okHttp client that implements rate limiting.
    var okHttpClient: OkHttpClient = OkHttpClient()
        .newBuilder()
        .addNetworkInterceptor(GameApiRateLimitInterceptor())
        .build()

    // Configure Retrofit to parse JSON, use our rate-limiting okHttp, and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.giantbomb.com/api/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val gameData: GameService = retrofit.create(GameService::class.java)
}
