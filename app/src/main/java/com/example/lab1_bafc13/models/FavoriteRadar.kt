package com.example.lab1_bafc13.models


data class FavoriteRadar (
    val user_uuid: String,
    val camera_id: String,
    val region_code: String,
    val src_camera: String,
    val serial_no: String,
    val print_name: String,
    val camera_model: String,
    var complex_id: String,
    val camera_place: String,
    val gps_x: Double,
    val gps_y: Double,
    val violname: String
)

data class FavoriteRadarsResponse (
    val items: List<FavoriteRadar>
)