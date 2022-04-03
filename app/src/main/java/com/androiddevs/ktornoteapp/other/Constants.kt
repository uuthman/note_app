package com.androiddevs.ktornoteapp.other

object Constants {

    const val DATABASE_NAME = "notes_db"

    const val  KEY_LOGGED_IN_EMAIL = "KEY_LOGGED_IN_EMAIL"
    const val KEY_PASSWORD = "KEY_PASSWORD"

    const val ENCRYPTED_PREF_NAME = "enc_shared_pref"

    const val BASE_URL = "http://10.0.2.2:8080"

    val IGNORE_AUTH_URLS = listOf("/login","register")
}