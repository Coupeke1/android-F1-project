package com.example.groeiproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.groeiproject.components.PurpleButton
import com.example.groeiproject.model.Driver
import com.example.groeiproject.model.Team
import com.example.groeiproject.ui.theme.PurpleTheme
import countDriversForTeam
import getAllTeams
import getDriversForTeam
import updateTeam

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PurpleTheme {
                TeamViewer()
            }
        }
    }
}

@Composable
fun TeamViewer() {
    var currentIndex by remember { mutableIntStateOf(0) }
    val teamList = remember { mutableStateListOf<Team>().apply { addAll(getAllTeams()) } }

    if (teamList.isEmpty()) {
        Text("Geen teams beschikbaar")
        return
    }

    val safeIndex = currentIndex.coerceIn(0, teamList.lastIndex)
    val currentTeam = teamList[safeIndex]
    val driverCount = remember(currentTeam.id) { countDriversForTeam(currentTeam.id) }
    val drivers = remember(currentTeam.id) { getDriversForTeam(currentTeam.id) }

    var showEditDialog by remember { mutableStateOf(false) }

    ArtworkDisplay(
        team = currentTeam,
        driverCount = driverCount,
        drivers = drivers,
        onPrevious = { if (currentIndex > 0) currentIndex-- },
        onNext = { if (currentIndex < teamList.size - 1) currentIndex++ },
        onEdit = { showEditDialog = true }
    )

    if (showEditDialog) {
        EditTeamDialog(
            team = currentTeam,
            onDismiss = { showEditDialog = false },
            onSave = { updatedTeam ->
                val index = teamList.indexOfFirst { it.id == updatedTeam.id }
                if (index != -1) {
                    teamList[index] = updatedTeam
                    updateTeam(
                        updatedTeam.id,
                        updatedTeam.name,
                        updatedTeam.headquarters,
                        updatedTeam.teamPrincipal,
                        updatedTeam.championships,
                        updatedTeam.active
                    )
                }
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ArtworkDisplay(
    team: Team,
    driverCount: Int,
    drivers: List<Driver>,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        TeamLogo(team.logoUrl)
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = team.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        TeamInfoCard(team, driverCount)
        Spacer(modifier = Modifier.height(16.dp))
        DriversRow(drivers)
        Spacer(modifier = Modifier.height(28.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PurpleButton("Vorige", onClick = onPrevious)
            PurpleButton("Bewerken", onClick = onEdit)
            PurpleButton("Volgende", onClick = onNext)
        }
    }
}

@Composable
fun DriversRow(drivers: List<Driver>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Coureurs",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(
                18.dp,
                Alignment.CenterHorizontally
            )
        ) {
            items(drivers) { driver ->
                DriverCard(driver)
            }
        }
    }
}

@Composable
fun DriverCard(driver: Driver) {
    val borderColor = try {
        Color(driver.helmetColor.toColorInt())
    } catch (e: Exception) {
        Color.Gray
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = driver.fullName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "#${driver.raceNumber}",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = driver.nationality,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = driver.dateOfBirth,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${driver.contractYears} jaar contract",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${driver.podiumFinishes} podia",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            if (driver.isRookie) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 2.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Nieuwkomer",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TeamLogo(logoUrl: String) {
    val context = LocalContext.current
    val resId = remember(logoUrl) {
        context.resources.getIdentifier(logoUrl, "drawable", context.packageName)
    }
    val actualResId = if (resId != 0) resId else R.drawable.ic_launcher_background
    Image(
        painter = painterResource(id = actualResId),
        contentDescription = "Team logo",
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(8.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun TeamInfoCard(team: Team, driverCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoRow("HQ", team.headquarters)
            InfoRow("Motor", team.engineManufacturer)
            InfoRow("Kampioenschappen", team.championships.toString())
            InfoRow("Teambaas", team.teamPrincipal)
            InfoRow("Aantal coureurs", driverCount.toString())
            InfoRow("Actief", if (team.active) "Ja" else "Nee")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, color = Color.Gray)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}


@Composable
fun EditTeamDialog(
    team: Team,
    onDismiss: () -> Unit,
    onSave: (Team) -> Unit
) {
    var teamName by remember { mutableStateOf(team.name) }
    var teamHQ by remember { mutableStateOf(team.headquarters) }
    var teamPrincipal by remember { mutableStateOf(team.teamPrincipal) }
    var championships by remember { mutableStateOf(team.championships.toString()) }
    var isActive by remember { mutableStateOf(team.active) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bewerk Team") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Teamnaam") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = teamHQ,
                    onValueChange = { teamHQ = it },
                    label = { Text("Hoofdkwartier") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = teamPrincipal,
                    onValueChange = { teamPrincipal = it },
                    label = { Text("Teambaas") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = championships,
                    onValueChange = { championships = it },
                    label = { Text("Kampioenschappen") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Actief:", modifier = Modifier.padding(end = 8.dp))
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedTeam = team.copy(
                    name = teamName,
                    headquarters = teamHQ,
                    teamPrincipal = teamPrincipal,
                    championships = championships.toIntOrNull() ?: team.championships,
                    active = isActive
                )
                onSave(updatedTeam)
            }) {
                Text("Opslaan")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Annuleren")
            }
        }
    )
}
