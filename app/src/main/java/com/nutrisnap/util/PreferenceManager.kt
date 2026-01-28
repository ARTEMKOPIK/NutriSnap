package com.nutrisnap.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nutrisnap_prefs", Context.MODE_PRIVATE)

    var calorieGoal: Int
        get() = prefs.getInt(KEY_CALORIE_GOAL, 2000)
        set(value) = prefs.edit().putInt(KEY_CALORIE_GOAL, value).apply()

    companion object {
        private const val KEY_CALORIE_GOAL = "calorie_goal"
    }
}
