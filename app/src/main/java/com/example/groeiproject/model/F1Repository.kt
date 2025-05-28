package com.example.groeiproject.model

import com.example.groeiproject.api.TeamApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class F1Repository @Inject constructor(
    private val service: TeamApiService
) {

    suspend fun getAllTeams(): List<Team> = service.getAllTeams()
    suspend fun getAllDrivers(): List<Driver> = service.getAllDrivers()
    suspend fun getDriversForTeam(teamId: Int): List<Driver> = service.getDriversForTeam(teamId)
    suspend fun createTeam(team: Team): Team = service.createTeam(team)
    suspend fun updateTeam(team: Team): Team = service.updateTeam(team.id, team)
    suspend fun deleteTeam(teamId: Int) = service.deleteTeam(teamId)

    suspend fun createDriver(driver: Driver): Driver = service.createDriver(driver)
    suspend fun updateDriver(driver: Driver): Driver = service.updateDriver(driver.id, driver)
    suspend fun deleteDriver(driverId: Int) = service.deleteDriver(driverId)
}
