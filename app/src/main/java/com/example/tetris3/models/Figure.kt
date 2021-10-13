package com.example.tetris3.models

import android.graphics.Color
import android.graphics.Point
import com.example.tetris3.constants.FieldConstants
import kotlin.random.Random

class Figure private constructor(private val shapeIndex: Int, private val color: BlockColor) {
    var frameNumber = 0
    var position = Point(FieldConstants.COLUMN_COUNT.value / 2, 0)

    fun setState(frame: Int, position: Point) {
        frameNumber = frame
        this.position = position
    }

    fun getShape(frameNumber: Int) =
        Shape.values()[shapeIndex].getFrame(frameNumber).as2dByteArray()

    val frameCount: Int
        get() = Shape.values()[shapeIndex].frameCount

    fun getColor() = color.rgbValue

    fun getStaticValue() = color.byteValue

    companion object {
        fun createFigure(): Figure {
            val random = Random.nextInt(Shape.values().size)
            val blockColor = BlockColor.values()[random]

            val figure = Figure(random, blockColor)
            figure.position.x = figure.position.x - Shape.values()[figure.shapeIndex].startPosition
            return figure
        }

        fun getColor(value: Byte): Int {
            for (color in BlockColor.values()) {
                if (value == color.byteValue) {
                    return color.rgbValue
                }
            }
            return -1
        }
    }

    enum class BlockColor(val rgbValue: Int, val byteValue: Byte) {
        PINK(Color.rgb(255, 105, 180), 2.toByte()),
        GREEN(Color.rgb(0, 128, 0), 3.toByte()),
        ORANGE(Color.rgb(255, 140, 0), 4.toByte()),
        YELLOW(Color.rgb(255, 255, 0), 5.toByte()),
        CYAN(Color.rgb(0, 255, 255), 6.toByte()),
        BLUE(Color.rgb(39, 44, 188), 7.toByte()),
        RED(Color.rgb(255, 40, 0), 8.toByte());
    }
}