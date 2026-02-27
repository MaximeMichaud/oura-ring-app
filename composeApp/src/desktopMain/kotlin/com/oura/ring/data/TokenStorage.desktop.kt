package com.oura.ring.data.db

import java.util.prefs.Preferences

actual class TokenStorage {
    private val prefs = Preferences.userNodeForPackage(TokenStorage::class.java)

    actual fun getToken(): String? = prefs.get("oura_token", null)

    actual fun saveToken(token: String) {
        prefs.put("oura_token", token)
        prefs.flush()
    }

    actual fun clearToken() {
        prefs.remove("oura_token")
        prefs.flush()
    }
}
