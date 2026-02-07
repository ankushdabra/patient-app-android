package com.healthcare.app.core.network

import com.healthcare.app.core.storage.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // Skip auth for login and register
        if (path.contains("/login") || path.contains("/register")) {
            return chain.proceed(originalRequest)
        }

        // Retrieve token from DataStore synchronously
        val token = runBlocking {
            tokenManager.token.first()
        }

        // Add Authorization header if token exists
        val request = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer ${token.trim()}")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
