package com.example.lab1_bafc13.REST

import com.example.lab1_bafc13.models.FavoriteRadar
import com.example.lab1_bafc13.models.Radar
import com.example.lab1_bafc13.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("user_favorite_radars")
    suspend fun getFavoriteRadarsByUuid(
        @Query("user_uuid") uuid: String?
    ): List<FavoriteRadar>

    @DELETE("user_favorite_radars")
    suspend fun deleteFavoriteRadar(
        @Query("user_uuid") userUuid: String,
        @Query("gps_y") gpsY: String,
        @Query("gps_x") gpsX: String
    ): Response<Unit>

    @GET("users")
    suspend fun getUserByUuid(
        @Query("uuid") uuid: String
    ): List<User>

    @POST("users")
    suspend fun createUser(@Body user: User): Response<Unit>

    @GET("radars")
    suspend fun getKalugaRadars(
        @Query("camera_place") filter: String = "like.*Калужская обл.*"
    ): List<Radar>

    @GET("radars")
    suspend fun getMoscowRadars(
        @Query("camera_place") cameraPlace: String = "like.*Москва*",
        @Query("limit") limit: Int = 1000
    ): List<Radar>

    @POST("user_favorite_radars")
    suspend fun saveFavoriteRadar(@Body favorite: FavoriteRadar): Response<Unit>
}