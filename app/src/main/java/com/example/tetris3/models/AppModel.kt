package com.example.tetris3.models

import android.graphics.Point
import com.example.tetris3.constants.CellConstants
import com.example.tetris3.constants.FieldConstants
import com.example.tetris3.helpers.array2dOfByte
import com.example.tetris3.storage.AppPreferences

class AppModel {
    var score = 0
    private var preferences: AppPreferences? = null

    var currentBlock: Figure? = null
    var currentState = Statuses.AWAITING.name

    private var field: Array<ByteArray> = array2dOfByte(
        FieldConstants.ROW_COUNT.value,
        FieldConstants.COLUMN_COUNT.value
    )

    fun setPreferences(preferences: AppPreferences?) {
        this.preferences = preferences
    }

    fun getCellStatus(row: Int, column: Int): Byte? = field[row][column]

    private fun setCellStatus(row: Int, column: Int, status: Byte?) {
        if (status != null) field[row][column] = status
    }

    private fun boostScore() {
        score += 10
        if (score > preferences?.getHighScore() as Int) {
            preferences?.saveHighScore(score)
        }
    }

    private fun shiftedScore(counter: Int) {
        when (counter) {
            1 -> score += 100
            2 -> score += 250
            3 -> score += 400
            4 -> score += 600
        }
        if (score > preferences?.getHighScore() as Int) {
            preferences?.saveHighScore(score)
        }
    }

    private fun generateNextBlock() {
        currentBlock = Figure.createFigure()
    }

    private fun validTranslation(position: Point, shape: Array<ByteArray>): Boolean {
        return if (position.y < 0 || position.x < 0) false
        else if (position.y + shape.size > FieldConstants.ROW_COUNT.value) false
        else if (position.x + shape[0].size > FieldConstants.COLUMN_COUNT.value) false
        else {
            for (i in shape.indices) {
                for (j in shape[0].indices) {
                    val y = position.y + i
                    val x = position.x + j
                    if (CellConstants.EMPTY.value != shape[i][j] &&
                        CellConstants.EMPTY.value != field[y][x]) return false
                }
            }
            true
        }
    }

    private fun moveValid(position: Point, frameNumber: Int?): Boolean {
        val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber as Int)
        return validTranslation(position, shape as Array<ByteArray>)
    }

    fun generateField(action: String) {
        if (isGameActive()) {
            resetField()
            var frameNumber: Int? = currentBlock?.frameNumber
            val coordinate: Point? = Point()
            coordinate?.x = currentBlock?.position?.x
            coordinate?.y = currentBlock?.position?.y

            when (action) {
                Motions.LEFT.name -> coordinate?.x = currentBlock?.position?.x?.minus(1)
                Motions.RIGHT.name -> coordinate?.x = currentBlock?.position?.x?.plus(1)
                Motions.DOWN.name -> coordinate?.y = currentBlock?.position?.y?.plus(1)
                Motions.ROTATE.name -> {
                    frameNumber = frameNumber?.plus(1)
                    if (frameNumber != null && frameNumber >= currentBlock?.frameCount as Int)
                        frameNumber = 0
                }
            }
            if (!moveValid(coordinate as Point, frameNumber)) {
                translateBlock(currentBlock?.position as Point, currentBlock?.frameNumber as Int)
                if (Motions.DOWN.name == action) {
                    boostScore()
                    persistCellData()
                    assessField()
                    generateNextBlock()
                    if (!blockAdditionPossible()) {
                        currentState = Statuses.OVER.name
                        currentBlock = null
                        resetField(false)
                    }
                }
            } else {
                if (frameNumber != null) {
                    translateBlock(coordinate, frameNumber)
                    currentBlock?.setState(frameNumber, coordinate)
                }
            }
        }
    }

    private fun resetField(ephemeralCellsOnly: Boolean = true) {
        for (i in 0 until FieldConstants.ROW_COUNT.value) {
            (0 until FieldConstants.COLUMN_COUNT.value)
                .filter { !ephemeralCellsOnly || field[i][it] == CellConstants.EPHEMERAL.value }
                .forEach { field[i][it] = CellConstants.EMPTY.value }
        }
    }

    private fun persistCellData() {
        for (i in field.indices) {
            for (j in field[i]. indices) {
                var status = getCellStatus(i, j)
                if (status == CellConstants.EPHEMERAL.value) {
                    status = currentBlock?.getStaticValue()
                    setCellStatus(i, j, status)
                }
            }
        }
    }

    private fun assessField() {
        var rowsShifted = 0
        for (i in field.indices) {
            var emptyCells = 0
            for (j in field[i].indices) {
                val status = getCellStatus(i, j)
                if (CellConstants.EMPTY.value == status) emptyCells++
            }
            if (emptyCells == 0) {
                shiftRows(i)
                rowsShifted++
            }
        }
        if (rowsShifted > 0)  {
            shiftedScore(rowsShifted)
        }
    }

    private fun shiftRows(nToRow: Int) {
        if (nToRow > 0) {
            for (j in nToRow - 1 downTo 0) {
                for (m in field[j].indices) {
                    setCellStatus(j + 1, m, getCellStatus(j, m))
                }
            }
        }
        for (j in field[0].indices) {
            setCellStatus(0, j, CellConstants.EMPTY.value)
        }
    }

    private fun translateBlock(position: Point, frameNumber: Int) {
        synchronized(field) {
            val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber)
            if (shape != null) {
                for (i in shape.indices) {
                    for (j in shape[i].indices) {
                        val y = position.y + i
                        val x = position.x + j
                        if (CellConstants.EMPTY.value != shape[i][j]) field[y][x] = shape[i][j]
                    }
                }
            }
        }
    }

    private fun blockAdditionPossible(): Boolean =
        moveValid(currentBlock?.position as Point, currentBlock?.frameNumber)

    fun getBack() {
        resetModel()
        endGame()
    }

    fun startGame() {
        if (!isGameActive()) {
            currentState = Statuses.ACTIVE.name
            generateNextBlock()
        }
    }

    fun endGame() {
        score = 0
        currentState = Statuses.OVER.name
    }

    private fun resetModel() {
        resetField(false)
        currentState = Statuses.AWAITING.name
        score = 0
    }

    fun isGameOver(): Boolean = currentState == Statuses.OVER.name

    fun isGameActive(): Boolean = currentState == Statuses.ACTIVE.name

    fun isGameAwaiting(): Boolean = currentState == Statuses.AWAITING.name

    enum class Statuses {
        AWAITING, ACTIVE, OVER
    }

    enum class Motions {
        LEFT, RIGHT, DOWN, ROTATE
    }
}