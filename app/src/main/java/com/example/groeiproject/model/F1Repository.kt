import com.example.groeiproject.data.F1DataProvider
import com.example.groeiproject.model.Driver
import com.example.groeiproject.model.Team

fun getAllTeams(): List<Team> {
    return F1DataProvider.teams.toList()
}

fun getAllDrivers(): List<Driver> {
    return F1DataProvider.drivers.toList()
}

fun countDriversForTeam(teamId: Int): Int {
    return F1DataProvider.drivers.count { it.teamId == teamId }
}

fun getDriversForTeam(teamId: Int): List<Driver> {
    return F1DataProvider.drivers.filter { it.teamId == teamId }
}

fun updateTeam(
    teamId: Int,
    newName: String? = null,
    newHeadquarters: String? = null,
    newTeamPrincipal: String? = null,
    newChampionships: Int? = null,
    newActive: Boolean? = null
): Boolean {
    val team = F1DataProvider.teams.find { it.id == teamId } ?: return false

    newName?.let { team.name = it }
    newHeadquarters?.let { team.headquarters = it }
    newTeamPrincipal?.let { team.teamPrincipal = it }
    newChampionships?.let { team.championships = it }
    newActive?.let { team.active = it }

    return true
}