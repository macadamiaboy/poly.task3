package com.example.tetris3.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.tetris3.R
import com.example.tetris3.models.AppModel
import com.example.tetris3.storage.AppPreferences
import com.example.tetris3.view.MainActivity.Companion.tvMainHighScore

class GameActivity : AppCompatActivity() {
    var tvCurrentScore: TextView? = null
    var tvHighScore: TextView? = null
    var appPreferences: AppPreferences? = null
    private lateinit var tetrisView: TetrisView
    private val appModel: AppModel = AppModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        appPreferences = AppPreferences(this)
        appModel.setPreferences(appPreferences)
        val btnBack = findViewById<Button>(R.id.btn_back)
        val btnLeft = findViewById<Button>(R.id.btn_left)
        val btnRight = findViewById<Button>(R.id.btn_right)
        val btnDrop = findViewById<Button>(R.id.btn_drop)
        val btnTurn = findViewById<Button>(R.id.btn_turn)
        tvCurrentScore = findViewById(R.id.tv_current_score)
        tvHighScore = findViewById(R.id.tv_high_score)
        tetrisView = findViewById(R.id.view_tetris)
        tetrisView.setActivity(this)
        tetrisView.setModel(appModel)
        btnBack.setOnClickListener {
            appModel.getBack()
            tvMainHighScore?.text = "High score: ${appPreferences?.getHighScore()}"
            finish()
        }
        btnDrop.setOnClickListener {
            if (appModel.isGameOver() || appModel.isGameAwaiting()) {
                appModel.startGame()
                tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
            } else if (appModel.isGameActive()) moveTetromino(AppModel.Motions.DOWN)
        }
        btnLeft.setOnClickListener {
            if (appModel.isGameOver() || appModel.isGameAwaiting()) {
                appModel.startGame()
                tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
            } else if (appModel.isGameActive()) moveTetromino(AppModel.Motions.LEFT)
        }
        btnRight.setOnClickListener {
            if (appModel.isGameOver() || appModel.isGameAwaiting()) {
                appModel.startGame()
                tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
            } else if (appModel.isGameActive()) moveTetromino(AppModel.Motions.RIGHT)
        }
        btnTurn.setOnClickListener {
            if (appModel.isGameOver() || appModel.isGameAwaiting()) {
                appModel.startGame()
                tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
            } else if (appModel.isGameActive()) moveTetromino(AppModel.Motions.ROTATE)
        }
        updateHighScore()
        updateCurrentScore()
    }

    private fun moveTetromino(motion: AppModel.Motions) {
        if (appModel.isGameActive()) {
            tetrisView.setGameCommand(motion)
        }
    }

    private fun updateHighScore() {
        tvHighScore?.text = "${appPreferences?.getHighScore()}"
    }

    private fun updateCurrentScore() {
        tvCurrentScore?.text = "0"
    }
}