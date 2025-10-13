package com.example.lab1_bafc13

import kotlin.math.floor

    val listToNormalize = mutableListOf<Int>()
    val normalizedList = mutableListOf<Double>()
    fun fillList(): MutableList<Int> {
        return MutableList(10) { (1..125).random() }
    }
    fun <T> makeString(data: MutableList<T>): String {
        return data.joinToString(", ")
    }
    fun normalizeByString(data: String): MutableList<Double> {
        listToNormalize.clear()
        listToNormalize.addAll( data.split(",")
            .map { it.trim() }
            .map { it.toInt() } )
        normalizeList(listToNormalize)
        return normalizedList
    }
    fun normalizeList(data: List<Int>){
        val maxVal = data.maxOrNull() ?: 1
        normalizedList.clear()
        normalizedList.addAll( data.map { floor(( it.toDouble() / maxVal) * 1000) / 1000 } as MutableList<Double> )
    }