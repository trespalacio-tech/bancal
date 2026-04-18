package com.bancal.app.data.preferences

import android.content.Context

object BancalPreferences {
    private const val PREFS_NAME = "bancal_prefs"
    private const val KEY_SELECTED_ID = "selected_bancal_id"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"

    fun getSelectedId(context: Context): Long =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_SELECTED_ID, 1L)

    fun setSelectedId(context: Context, id: Long) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putLong(KEY_SELECTED_ID, id).apply()
    }

    fun isOnboardingDone(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_DONE, false)

    fun setOnboardingDone(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
    }
}
