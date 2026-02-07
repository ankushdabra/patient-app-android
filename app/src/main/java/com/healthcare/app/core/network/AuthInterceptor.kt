package com.healthcare.app.core.network

import android.util.Log
import com.healthcare.app.core.storage.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // 1. Skip auth for login and register
        if (path == "/login" || path == "/api/register") {
            Log.d("AuthInterceptor", "Skipping Auth for: $path")
            return chain.proceed(originalRequest)
        }

        // 2. Retrieve token from DataStore synchronously
        // Use .first() to ensure we wait for the DataStore to emit the current value
        val token = runBlocking {
            try {
                val t = tokenManager.token.first()
                Log.d("AuthInterceptor", "Retrieved token from storage for $path: ${if (t.isNullOrEmpty()) "NULL" else "PRESENT"}")
                t
            } catch (e: Exception) {
                Log.e("AuthInterceptor", "Error retrieving token", e)
                null
            }
        }

        // 3. Add Authorization header if token exists
        val request = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer ${token.trim()}")
                .build()
        } else {
            Log.w("AuthInterceptor", "No token found for authenticated request to: $path")
            originalRequest
        }

        val response = chain.proceed(request)

        if (response.code == 403) {
            Log.e("AuthInterceptor", "403 Forbidden received for: ${request.url}")
            Log.e("AuthInterceptor", "Token used: ${if (token.isNullOrEmpty()) "NONE" else "YES (ends with ${token.takeLast(5)})"}")
        }

        return response
    }
}
