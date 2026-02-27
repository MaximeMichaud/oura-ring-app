package com.oura.ring.data.db

import android.content.Context
import android.content.SharedPreferences

actual class TokenStorage(
    context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("oura_prefs", Context.MODE_PRIVATE)

    actual fun getToken(): String? = prefs.getString("oura_token", null)

    actual fun saveToken(token: String) {
        prefs.edit().putString("oura_token", token).apply()
    }

    actual fun clearToken() {
        prefs.edit().remove("oura_token").apply()
    }
}
