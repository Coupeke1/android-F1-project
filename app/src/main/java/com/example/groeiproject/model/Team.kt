package com.example.groeiproject.model

import kotlinx.serialization.Serializable


@Serializable
data class Team(
    val id: Int,
    var name: String,
    val foundedDate: String,
    var headquarters: String,
    var teamPrincipal: String,
    val engineManufacturer: String,
    var championships: Int,
    var active: Boolean,
    val enginePowerHP: Double,
    val sponsors: List<String>,
    val logoUrl: String
)
