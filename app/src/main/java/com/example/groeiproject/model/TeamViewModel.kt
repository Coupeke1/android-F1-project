package com.example.groeiproject.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groeiproject.data.F1Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "TeamViewModel"

class TeamViewModel : ViewModel() {
    private val repo = F1Repository()

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    private val _drivers = MutableStateFlow<List<Driver>>(emptyList())

    val teams: StateFlow<List<Team>> = _teams
    val drivers: StateFlow<List<Driver>> = _drivers

    private val _selectedTeamId = MutableStateFlow(-1)
    val selectedTeamId: StateFlow<Int> = _selectedTeamId

    init {
        loadTeams()
        loadDrivers()
    }

    fun selectTeam(id: Int) {
        _selectedTeamId.value = id
    }

    fun loadTeams() = viewModelScope.launch {
        try {
            Log.d(TAG, "Loading teams from server")
            _teams.value = repo.getAllTeams()
            _selectedTeamId.value = _teams.value.firstOrNull()?.id ?: -1
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load teams", e)
        }
    }

    private fun loadDrivers() = viewModelScope.launch {
        _drivers.value = repo.getAllDrivers()
    }


    fun createTeam(newTeam: Team) = viewModelScope.launch {
        try {
            Log.i(TAG, "Creating new team: ${newTeam.name}")
            val created = repo.createTeam(newTeam)
            _teams.value = _teams.value + created
        } catch (e: Exception) {
            Log.e(TAG, "Create failed", e)
        }
    }

    fun updateTeam(updated: Team) = viewModelScope.launch {
        try {
            Log.i(TAG, "Updating team id=${updated.id}")
            val team = repo.updateTeam(updated)
            _teams.value = _teams.value.map { if (it.id == team.id) team else it }
        } catch (e: Exception) {
            Log.e(TAG, "Update failed", e)
        }
    }

    fun deleteTeam(teamId: Int) = viewModelScope.launch {
        try {
            Log.i(TAG, "Deleting team id=$teamId")
            repo.deleteTeam(teamId)
            _teams.value = _teams.value.filterNot { it.id == teamId }
        } catch (e: Exception) {
            Log.e(TAG, "Delete failed", e)
        }
    }
}
