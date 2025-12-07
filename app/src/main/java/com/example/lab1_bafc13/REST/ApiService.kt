package com.example.lab1_bafc13.REST

import com.example.lab1_bafc13.models.FavoriteRadar
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("user_favorite_radars")
    suspend fun getRadars(): List<FavoriteRadar>
}