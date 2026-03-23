package com.drasticds.emulator.common.retroachievements

import android.content.SharedPreferences
import androidx.core.content.edit
import com.drasticds.emulator.rcheevosapi.RAUserAuthStore
import com.drasticds.emulator.rcheevosapi.model.RAUserAuth

class AndroidRAUserAuthStore(private val sharedPreferences: SharedPreferences) : RAUserAuthStore {

    private companion object {
        const val USERNAME_KEY = "ra_username"
        const val TOKEN_KEY = "ra_token"
    }

    override suspend fun storeUserAuth(userAuth: RAUserAuth) {
        sharedPreferences.edit {
            putString(USERNAME_KEY, userAuth.username)
            putString(TOKEN_KEY, userAuth.token)
        }
    }

    override suspend fun getUserAuth(): RAUserAuth? {
        val username = sharedPreferences.getString(USERNAME_KEY, null) ?: return null
        val token = sharedPreferences.getString(TOKEN_KEY, null) ?: return null

        return RAUserAuth(username, token)
    }

    override suspend fun clearUserAuth() {
        sharedPreferences.edit {
            remove(USERNAME_KEY)
            remove(TOKEN_KEY)
        }
    }
}