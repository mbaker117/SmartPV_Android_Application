package com.psut.smartpv.util

import android.app.Activity
import android.content.Context


class SharedPreferencesUtil {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "users"
        fun addStringToSharedPreferences(activity: Context, key: String, value: String?) {
            val sharedPref = activity.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)?: return
            with(sharedPref.edit()) {
                putString(key, value)
                apply()
            }
        }
        fun getString(activity: Context, key: String): String? {
            val sharedPref = activity.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)
            return sharedPref.getString(key,"")
        }
        fun remove(activity: Activity) {
            val sharedPref = activity.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)?: return
            with(sharedPref.edit()) {
                clear()
                commit()
            }
        }
        fun contain(activity: Activity, username:String): Boolean {
            val sharedPref = activity.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            return sharedPref.contains(username)
        }


    }
}