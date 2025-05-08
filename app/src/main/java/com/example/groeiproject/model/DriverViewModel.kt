package com.example.groeiproject.model
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groeiproject.data.F1DataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "TeamViewModel"

class TeamViewModel : ViewModel() {
    private val _teams = MutableStateFlow<List<Team>>(F1DataProvider.teams.toList())
    val teams: StateFlow<List<Team>> = _teams

    fun createTeam(newTeam: Team) = viewModelScope.launch {
        Log.i(TAG, "Creating new team: ${newTeam.name}")
        F1DataProvider.teams.add(newTeam)
        _teams.value = F1DataProvider.teams.toList()
    }

    fun loadTeams() = viewModelScope.launch {
        Log.d(TAG, "Loading all teams (count=${F1DataProvider.teams.size})")
        _teams.value = F1DataProvider.teams.toList()
    }

    fun updateTeam(updated: Team) = viewModelScope.launch {
        Log.i(TAG, "Updating team id=${updated.id} name=${updated.name}")
        val newList = F1DataProvider.teams.map { existing ->
            if (existing.id == updated.id) updated else existing
        }
        F1DataProvider.teams.clear()
        F1DataProvider.teams.addAll(newList)
        _teams.value = newList
    }
    fun deleteTeam(teamId: Int) = viewModelScope.launch {
        Log.i(TAG, "Deleting team id=$teamId")
        val removed = F1DataProvider.teams.removeAll { it.id == teamId }
        if (removed) {
            _teams.value = F1DataProvider.teams.toList()
        } else {
            Log.w(TAG, "Delete failed: team id=$teamId not found")
        }
    }
}
