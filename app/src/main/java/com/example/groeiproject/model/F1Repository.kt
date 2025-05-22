package com.example.groeiproject.data

import com.example.groeiproject.api.ApiClient
import com.example.groeiproject.model.Driver
import com.example.groeiproject.model.Team

class F1Repository {
    private val service = ApiClient.teamService

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
