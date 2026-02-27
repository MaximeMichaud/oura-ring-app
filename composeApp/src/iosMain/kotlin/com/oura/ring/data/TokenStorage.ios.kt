package com.oura.ring.data.db

import platform.Foundation.NSUserDefaults

actual class TokenStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun getToken(): String? = defaults.stringForKey("oura_token")

    actual fun saveToken(token: String) {
        defaults.setObject(token, forKey = "oura_token")
    }

    actual fun clearToken() {
        defaults.removeObjectForKey("oura_token")
    }
}
