package com.example.groeiproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groeiproject.model.Team

import com.example.groeiproject.ui.theme.PurpleTheme
import countDriversForTeam
import getAllTeams
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
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val teams = remember(refreshTrigger) { getAllTeams() }

    if (teams.isEmpty()) {
        Text("Geen teams beschikbaar")
        return
    }

    val safeIndex = currentIndex.coerceIn(0, teams.lastIndex)
    val currentTeam = teams[safeIndex]
    val driverCount =
        remember(currentTeam.id, refreshTrigger) { countDriversForTeam(currentTeam.id) }

    var showEditDialog by remember { mutableStateOf(false) }

    ArtworkDisplay(
        team = currentTeam,
        driverCount = driverCount,
        onPrevious = { if (currentIndex > 0) currentIndex-- },
        onNext = { if (currentIndex < teams.size - 1) currentIndex++ },
        onEdit = { showEditDialog = true }
    )

    if (showEditDialog) {
        EditTeamDialog(
            team = currentTeam,
            onDismiss = { showEditDialog = false },
            onSave = {
                showEditDialog = false
                refreshTrigger++
            }
        )
    }
}

@Composable
fun ArtworkDisplay(
    team: Team,
    driverCount: Int,
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

        Spacer(modifier = Modifier.height(28.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PurpleButton("Vorige", onClick = onPrevious)
            PurpleButton("Bewerken", onClick = onEdit)
            PurpleButton("Volgende", onClick = onNext)
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
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PurpleButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(30),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
        modifier = Modifier
            .height(48.dp)
            .widthIn(min = 100.dp)
    ) {
        Text(text = text, color = Color.White)
    }
}

@Composable
fun EditTeamDialog(
    team: Team,
    onDismiss: () -> Unit,
    onSave: () -> Unit
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
            Button(
                onClick = {
                    updateTeam(
                        teamId = team.id,
                        newName = teamName,
                        newHeadquarters = teamHQ,
                        newTeamPrincipal = teamPrincipal,
                        newChampionships = championships.toIntOrNull() ?: team.championships,
                        newActive = isActive
                    )
                    onSave()
                }
            ) {
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

@Preview(showBackground = true)
@Composable
fun TeamViewerPreview() {
    PurpleTheme {
        TeamViewer()
    }
}