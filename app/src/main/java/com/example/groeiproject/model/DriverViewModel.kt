package com.example.groeiproject.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groeiproject.data.F1DataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "DriverViewModel"

class DriverViewModel : ViewModel() {
    private val _drivers = MutableStateFlow(F1DataProvider.drivers.toList())
    val drivers: StateFlow<List<Driver>> = _drivers

    fun createDriver(newDriver: Driver) = viewModelScope.launch {
        Log.i(TAG, "Creating new driver: ${newDriver.fullName}")
        F1DataProvider.drivers.add(newDriver)
        _drivers.value = F1DataProvider.drivers.toList()
    }

    fun updateDriver(updated: Driver) = viewModelScope.launch {
        Log.i(TAG, "Updating driver id=${updated.id}")
        val newList = F1DataProvider.drivers.map { if (it.id == updated.id) updated else it }
        F1DataProvider.drivers.clear()
        F1DataProvider.drivers.addAll(newList)
        _drivers.value = newList
    }

    fun deleteDriver(driverId: Int) = viewModelScope.launch {
        Log.i(TAG, "Deleting driver id=$driverId")
        if (F1DataProvider.drivers.removeAll { it.id == driverId }) {
            _drivers.value = F1DataProvider.drivers.toList()
        } else {
            Log.w(TAG, "Driver not found: $driverId")
        }
    }
}
