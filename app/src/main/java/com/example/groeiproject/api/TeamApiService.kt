package com.example.groeiproject.api

import com.example.groeiproject.model.Driver
import com.example.groeiproject.model.Team
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TeamApiService {
    @GET("teams")
    suspend fun getAllTeams(): List<Team>

    @GET("drivers")
    suspend fun getAllDrivers(): List<Driver>

    @GET("drivers")
    suspend fun getDriversForTeam(@Query("teamId") teamId: Int): List<Driver>

    @POST("teams")
    suspend fun createTeam(@Body newTeam: Team): Team

    @PUT("teams/{id}")
    suspend fun updateTeam(@Path("id") id: Int, @Body updated: Team): Team

    @DELETE("teams/{id}")
    suspend fun deleteTeam(@Path("id") id: Int)

    @POST("drivers")
    suspend fun createDriver(@Body newDriver: Driver): Driver

    @PUT("drivers/{id}")
    suspend fun updateDriver(@Path("id") id: Int, @Body updated: Driver): Driver

    @DELETE("drivers/{id}")
    suspend fun deleteDriver(@Path("id") id: Int)
}
