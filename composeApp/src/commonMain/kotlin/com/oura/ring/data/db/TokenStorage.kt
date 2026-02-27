package com.oura.ring.data.db

expect class TokenStorage {
    fun getToken(): String?
    fun saveToken(token: String)
    fun clearToken()
}
