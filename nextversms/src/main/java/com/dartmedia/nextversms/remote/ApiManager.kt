package com.dartmedia.nextversms.remote

import android.content.Context
import android.content.SharedPreferences

class ApiManager(context: Context) {
    companion object {
        const val BASE_URL = "user_base_url"
        private const val APP_NAME = "nextversms"
    }

    private val sharedPreferences: SharedPreferences by lazy { context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE) }

    fun setBaseUrl(url: String) {
        val editor = sharedPreferences.edit()
        editor.putString(BASE_URL, url)
        editor.apply()
    }

    fun getBaseUrl() = sharedPreferences.getString(BASE_URL, null)
}