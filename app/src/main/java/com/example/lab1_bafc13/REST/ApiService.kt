package com.example.lab1_bafc13.REST

import com.example.lab1_bafc13.models.FavoriteRadar
import com.example.lab1_bafc13.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("user_favorite_radars")
    suspend fun getRadars(): List<FavoriteRadar>

    @GET("users")
    suspend fun getUserByUuid(
        @Query("uuid") uuid: String
    ): List<User>

    @POST("users")
    suspend fun createUser(@Body user: User): Response<Unit>
}