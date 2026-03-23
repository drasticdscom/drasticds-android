package com.drasticds.emulator.ui.backgrounds

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.drasticds.emulator.extensions.parcelable
import com.drasticds.emulator.parcelables.BackgroundParcelable
import kotlin.reflect.KType
import kotlin.reflect.typeOf

sealed class BackgroundsNavigation {
    @Serializable
    data object BackgroundList : BackgroundsNavigation()
    @Serializable
    data class BackgroundPreview(val backgroundParcelable: BackgroundParcelable) : BackgroundsNavigation() {
        companion object {
            val typeMap = mapOf<KType, NavType<*>>(typeOf<BackgroundParcelable>() to BackgroundParcelableType)
        }
    }
}

private val BackgroundParcelableType = object : NavType<BackgroundParcelable>(false) {

    override fun get(bundle: Bundle, key: String): BackgroundParcelable? {
        return bundle.parcelable<BackgroundParcelable>(key)
    }

    override fun parseValue(value: String): BackgroundParcelable {
        return Json.decodeFromString<BackgroundParcelable>(value)
    }

    override fun serializeAsValue(value: BackgroundParcelable): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: BackgroundParcelable) {
        bundle.putParcelable(key, value)
    }
}