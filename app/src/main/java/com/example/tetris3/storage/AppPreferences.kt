package com.example.tetris3.storage

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(ctx: Context) {
    var data: SharedPreferences = ctx.getSharedPreferences(
        "APP_PREFERENCES",
        Context.MODE_PRIVATE
    )

    fun saveHighScore(highscore: Int) = data.edit().putInt("HIGH_SCORE", highscore).apply()

    fun getHighScore(): Int = data.getInt("HIGH_SCORE", 0)

    fun clearHighScore() = data.edit().putInt("HIGH_SCORE", 0).apply()
}