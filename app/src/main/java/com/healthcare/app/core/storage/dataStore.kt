package com.healthcare.app.core.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private const val TAG = "TokenManager"
    }

    val token: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY]
        }.onEach { 
            Log.d(TAG, "Current token emission: ${if (it.isNullOrEmpty()) "EMPTY" else "PRESENT"}")
        }

    suspend fun saveToken(token: String) {
        Log.d(TAG, "Saving token...")
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
        Log.d(TAG, "Token saved successfully")
    }

    suspend fun clearToken() {
        Log.d(TAG, "Clearing token")
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
}
