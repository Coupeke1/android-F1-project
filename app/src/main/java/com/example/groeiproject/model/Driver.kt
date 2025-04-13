package com.example.groeiproject.model

data class Driver(
    val id: Int,
    val teamId: Int,
    val fullName: String,
    val nationality: String,
    val raceNumber: Int,
    val dateOfBirth: String,
    val contractYears: Int,
    val podiumFinishes: Int,
    val helmetColor: String,
    val isRookie: Boolean
)
