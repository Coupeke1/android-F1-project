package com.example.groeiproject.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DriverViewModel"

@HiltViewModel
class DriverViewModel @Inject constructor(
    private val repo: F1Repository
) : ViewModel() {

    private val _drivers = MutableStateFlow<List<Driver>>(emptyList())
    val drivers: StateFlow<List<Driver>> = _drivers

    init {
        loadDrivers()
    }

    private fun loadDrivers() = viewModelScope.launch {
        try {
            Log.d(TAG, "Loading drivers from server")
            _drivers.value = repo.getAllDrivers()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load drivers", e)
        }
    }

    fun createDriver(newDriver: Driver) = viewModelScope.launch {
        try {
            Log.i(TAG, "Creating new driver: ${newDriver.fullName}")
            val created = repo.createDriver(newDriver)
            // Voeg nieuw object toe aan de huidige lijst
            _drivers.value = _drivers.value + created
        } catch (e: Exception) {
            Log.e(TAG, "Create driver failed", e)
        }
    }

    fun updateDriver(updated: Driver) = viewModelScope.launch {
        try {
            Log.i(TAG, "Updating driver id=${updated.id}")
            val driver = repo.updateDriver(updated)
            _drivers.value = _drivers.value.map {
                if (it.id == driver.id) driver else it
            }
        } catch (e: Exception) {
            Log.e(TAG, "Update driver failed", e)
        }
    }

    fun deleteDriver(driverId: Int) = viewModelScope.launch {
        try {
            Log.i(TAG, "Deleting driver id=$driverId")
            repo.deleteDriver(driverId)
            _drivers.value = _drivers.value.filterNot { it.id == driverId }
        } catch (e: Exception) {
            Log.e(TAG, "Delete driver failed", e)
        }
    }
}
