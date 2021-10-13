package com.example.tetris3.models

import com.example.tetris3.helpers.array2dOfByte
import java.util.ArrayList

class Frame(private val width: Int) {
    val data: ArrayList<ByteArray> = ArrayList()

    fun addRow(byteStr: String): Frame {
        val row = ByteArray(byteStr.length)
        for (i in byteStr.indices) {
            row[i] = "${byteStr[i]}".toByte()
        }
        data.add(row)
        return this
    }

    fun as2dByteArray(): Array<ByteArray> {
        val bytes = array2dOfByte(data.size, width)
        return data.toArray(bytes)
    }
}