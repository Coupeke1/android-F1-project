package com.example.groeiproject.data

import com.example.groeiproject.model.Driver
import com.example.groeiproject.model.Team

object F1DataProvider {
    val teams: MutableList<Team> = mutableListOf(
        Team(
            id = 1,
            name = "Mercedes-AMG Petronas",
            foundedDate = "1970-03-12",
            headquarters = "Brackley, UK",
            teamPrincipal = "Toto Wolff",
            engineManufacturer = "Mercedes",
            championships = 8,
            active = true,
            enginePowerHP = 950.5,
            sponsors = listOf("Petronas", "Monster Energy", "Tommy Hilfiger"),
            logoUrl = "mercedes_amg_petronas_f1_logo"
        ), Team(
            id = 2,
            name = "Scuderia Ferrari",
            foundedDate = "1929-11-16",
            headquarters = "Maranello, Italy",
            teamPrincipal = "Frédéric Vasseur",
            engineManufacturer = "Ferrari",
            championships = 16,
            active = true,
            enginePowerHP = 955.0,
            sponsors = listOf("Shell", "Santander", "Ray-Ban"),
            logoUrl = "scuderia_ferrari_logo"
        ), Team(
            id = 3,
            name = "Red Bull Racing",
            foundedDate = "2005-01-01",
            headquarters = "Milton Keynes, UK",
            teamPrincipal = "Christian Horner",
            engineManufacturer = "Honda",
            championships = 5,
            active = true,
            enginePowerHP = 945.3,
            sponsors = listOf("Oracle", "Bybit", "Honda"),
            logoUrl = "red_bull_racing_logo"
        ), Team(
            id = 4,
            name = "McLaren F1 Team",
            foundedDate = "1963-01-01",
            headquarters = "Woking, UK",
            teamPrincipal = "Andrea Stella",
            engineManufacturer = "Mercedes",
            championships = 8,
            active = true,
            enginePowerHP = 940.2,
            sponsors = listOf("Google", "Dell", "Cisco"),
            logoUrl = "mclaren_formula_1_team_logo"
        ), Team(
            id = 5,
            name = "Aston Martin Aramco",
            foundedDate = "2018-04-30",
            headquarters = "Silverstone, UK",
            teamPrincipal = "Mike Krack",
            engineManufacturer = "Mercedes",
            championships = 0,
            active = true,
            enginePowerHP = 940.0,
            sponsors = listOf("Aramco", "Cognizant", "NetApp"),
            logoUrl = "aston_martin_f1_logo"
        ), Team(
            id = 6,
            name = "Alpine F1 Team",
            foundedDate = "1976-07-01",
            headquarters = "Enstone, UK",
            teamPrincipal = "Bruno Famin",
            engineManufacturer = "Renault",
            championships = 2,
            active = true,
            enginePowerHP = 930.5,
            sponsors = listOf("BP", "Castrol", "Epson"),
            logoUrl = "alpine_f1_team_logo"
        ), Team(
            id = 7,
            name = "Williams Racing",
            foundedDate = "1977-02-01",
            headquarters = "Grove, UK",
            teamPrincipal = "James Vowles",
            engineManufacturer = "Mercedes",
            championships = 9,
            active = true,
            enginePowerHP = 935.0,
            sponsors = listOf("Duracell", "Michelob", "Sparco"),
            logoUrl = "williams_racing_logo"
        ), Team(
            id = 8,
            name = "Visa Cash App RB",
            foundedDate = "2006-01-01",
            headquarters = "Faenza, Italy",
            teamPrincipal = "Laurent Mekies",
            engineManufacturer = "Honda",
            championships = 0,
            active = true,
            enginePowerHP = 942.8,
            sponsors = listOf("Visa", "Cash App", "Honda"),
            logoUrl = "visa_cash_app_rb_logo"
        ), Team(
            id = 9,
            name = "Stake F1 Team Kick Sauber",
            foundedDate = "1993-01-01",
            headquarters = "Hinwil, Switzerland",
            teamPrincipal = "Alessandro Alunni Bravi",
            engineManufacturer = "Ferrari",
            championships = 0,
            active = true,
            enginePowerHP = 925.0,
            sponsors = listOf("Stake", "Kick", "Clover"),
            logoUrl = "kick_sauber_f1_logo"
        ), Team(
            id = 10,
            name = "Haas F1 Team",
            foundedDate = "2014-04-11",
            headquarters = "Kannapolis, USA",
            teamPrincipal = "Ayao Komatsu",
            engineManufacturer = "Ferrari",
            championships = 0,
            active = true,
            enginePowerHP = 920.0,
            sponsors = listOf("MoneyGram", "Rush", "1&1"),
            logoUrl = "haas_f1_team_logo"
        ), Team(
            id = 11,
            name = "Andretti Global",
            foundedDate = "2024-01-01",
            headquarters = "Indianapolis, USA",
            teamPrincipal = "Michael Andretti",
            engineManufacturer = "Renault",
            championships = 0,
            active = false,
            enginePowerHP = 910.0,
            sponsors = listOf("GM", "Mozilla", "Coca-Cola"),
            logoUrl = "andretti_f1_team_logo"
        ), Team(
            id = 12,
            name = "Lotus F1 Team",
            foundedDate = "1952-01-01",
            headquarters = "Hethel, UK",
            teamPrincipal = "Clive Chapman",
            engineManufacturer = "Renault",
            championships = 7,
            active = false,
            enginePowerHP = 900.0,
            sponsors = emptyList(),
            logoUrl = "lotus_f1_team_logo"
        ), Team(
            id = 13,
            name = "Brabham Racing",
            foundedDate = "1960-03-01",
            headquarters = "Milton Keynes, UK",
            teamPrincipal = "David Brabham",
            engineManufacturer = "Honda",
            championships = 4,
            active = false,
            enginePowerHP = 890.5,
            sponsors = emptyList(),
            logoUrl = "brabham_racing_logo"
        ), Team(
            id = 14,
            name = "Tyrrell Racing",
            foundedDate = "1958-01-01",
            headquarters = "Ockham, UK",
            teamPrincipal = "Ken Tyrrell",
            engineManufacturer = "Ford",
            championships = 3,
            active = false,
            enginePowerHP = 880.0,
            sponsors = emptyList(),
            logoUrl = "tyrrell_f1_team_logo"
        ), Team(
            id = 15,
            name = "Jordan Grand Prix",
            foundedDate = "1991-01-01",
            headquarters = "Silverstone, UK",
            teamPrincipal = "Eddie Jordan",
            engineManufacturer = "Mugen-Honda",
            championships = 0,
            active = false,
            enginePowerHP = 875.5,
            sponsors = listOf("Benson & Hedges", "Buzzin Hornets"),
            logoUrl = "jordan_f1_team_logo"
        )
    )
    val drivers: MutableList<Driver> = mutableListOf(
        Driver(1, 1, "Lewis Hamilton", "British", 44, "1985-01-07", 3, 197, "Purple", false),
        Driver(1, 1, "Kimi Antonelli", "Italian", 12, "2006-03-25", 1, 2, "white", true),
        Driver(2, 1, "George Russell", "British", 63, "1998-02-15", 3, 11, "Black", false),
        Driver(3, 2, "Charles Leclerc", "Monegasque", 16, "1997-10-16", 5, 31, "Red", false),
        Driver(4, 2, "Carlos Sainz Jr.", "Spanish", 55, "1994-09-01", 2, 21, "Yellow", false),
        Driver(5, 3, "Max Verstappen", "Dutch", 1, "1997-09-30", 6, 102, "Blue", false),
        Driver(5, 3, "Yuki Tsunoda", "Japanese", 22, "1997-09-30", 2, 5, "Blue", true),
        Driver(6, 3, "Sergio Pérez", "Mexican", 11, "1990-01-26", 2, 39, "Pink", false),
        Driver(7, 4, "Lando Norris", "British", 4, "1999-11-13", 5, 15, "Orange", false),
        Driver(8, 4, "Oscar Piastri", "Australian", 81, "2001-04-06", 4, 3, "Cyan", true),
        Driver(9, 5, "Fernando Alonso", "Spanish", 14, "1981-07-29", 3, 106, "Green", false),
        Driver(10, 5, "Lance Stroll", "Canadian", 18, "1998-10-29", 2, 3, "White", false)
    )

}