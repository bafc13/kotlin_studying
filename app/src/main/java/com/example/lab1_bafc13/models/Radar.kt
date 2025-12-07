package com.example.lab1_bafc13.models


data class Radar (
    val cameraId: String,
    val regionCode: String,
    val srcCamera: String,
    val serialNo: String,
    val printName: String,
    val cameraModel: String,
    var complexId: String,
    val cameraPlace: String,
    val gpsX: Double,
    val gpsY: Double,
    val violName: String
)

data class RadarsResponse (
    val items: List<Radar>
)