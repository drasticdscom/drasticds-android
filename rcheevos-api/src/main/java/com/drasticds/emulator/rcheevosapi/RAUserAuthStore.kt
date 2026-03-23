package com.drasticds.emulator.rcheevosapi

import com.drasticds.emulator.rcheevosapi.model.RAUserAuth

interface RAUserAuthStore {
    suspend fun storeUserAuth(userAuth: RAUserAuth)
    suspend fun getUserAuth(): RAUserAuth?
    suspend fun clearUserAuth()
}