package com.example.tetris3.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.tetris3.R
import com.example.tetris3.storage.AppPreferences
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    companion object {
        var tvMainHighScore: TextView? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val preferences = AppPreferences(this)
        val btnNewGame = findViewById<Button>(R.id.btn_new_game)
        val btnResetScore = findViewById<Button>(R.id.btn_reset_score)
        val btnExit = findViewById<Button>(R.id.btn_exit)
        tvMainHighScore = findViewById(R.id.tv_high_score)
        tvMainHighScore?.text = "High score: ${preferences.getHighScore()}"
        btnExit.setOnClickListener {
            exitProcess(0)
        }
        btnNewGame.setOnClickListener {
            Intent(this, GameActivity::class.java).also {
                startActivity(it)
            }
        }
        btnResetScore.setOnClickListener {
            preferences.clearHighScore()
            tvMainHighScore?.text = "High score: ${preferences.getHighScore()}"
        }
    }
}