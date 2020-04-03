package com.gavinsappcreations.upcominggames.network

import com.google.common.util.concurrent.RateLimiter
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


/**
 * An OkHttp [Interceptor] that conforms to Giant Bomb's API rate limit
 * specifications.
 *
 * This implementation is best effort and will only work within the lifetime of
 * an [android.app.Application] instance. For example, if the app crashes
 * and requires a restart, then this could break the contract. In practice, this is
 * hopefully unlikely.
 *
 * All network traffic to the Giant Bomb API needs to be processed by a single instance
 * of [okhttp3.OkHttpClient] that is configured with this interceptor, otherwise it
 * won't be possible to enforce the limit globally.
 *
 * See https://www.giantbomb.com/forums/api-developers-3017/
 */
@Suppress("UnstableApiUsage")
class GameApiRateLimitInterceptor : Interceptor {
    //Limit requests to be 1.01 seconds apart
    private val rateLimiter: RateLimiter = RateLimiter.create(1.01)
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        rateLimiter.acquire()
        return chain.proceed(chain.request())
    }
}
