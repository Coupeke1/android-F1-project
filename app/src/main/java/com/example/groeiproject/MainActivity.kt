package com.example.groeiproject

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModelProvider
import com.example.groeiproject.components.PurpleButton
import com.example.groeiproject.model.Driver
import com.example.groeiproject.model.Team
import com.example.groeiproject.model.TeamViewModel
import com.example.groeiproject.ui.theme.AppTheme
import countDriversForTeam
import getDriversForTeam

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[TeamViewModel::class.java]
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TeamViewer(viewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeamViewer(viewModel: TeamViewModel) {
    val teams by viewModel.teams.collectAsState()
    var currentIndex by remember { mutableIntStateOf(0) }
    var showEdit by remember { mutableStateOf(false) }
    var showAdd by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
//        Row(
//            Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        )
//        {
//            PurpleButton(text = stringResource(R.string.button_add)) { showAdd = true }
//            PurpleButton(text = stringResource(R.string.button_delete)) {
//                if (teams.isNotEmpty()) {
//                    viewModel.deleteTeam(teams[currentIndex].id)
//                    currentIndex = (currentIndex - 1).coerceAtLeast(0)
//                }
//            }
//        }

        if (teams.isEmpty()) {
            Text(
                text = stringResource(R.string.no_teams_available),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            val idx = currentIndex.coerceIn(0, teams.lastIndex)
            val team = teams[idx]
            val drivers = remember(team.id) { getDriversForTeam(team.id) }
            val count = remember(team.id) { countDriversForTeam(team.id) }

            ArtworkDisplay(
                team = team,
                driverCount = count,
                drivers = drivers,
                onPrevious = { if (idx > 0) currentIndex-- },
                onNext = { if (idx < teams.lastIndex) currentIndex++ },
                onEdit = { showEdit = true }
            )
        }
    }

    if (showEdit) {
        val team = teams.getOrNull(currentIndex) ?: return
        EditTeamDialog(
            team = team,
            onDismiss = { showEdit = false },
            onSave = {
                viewModel.updateTeam(it)
                showEdit = false
            }
        )
    }

//    if (showAdd) {
//        var name by remember { mutableStateOf("") }
//        var hq by remember { mutableStateOf("") }
//        var principal by remember { mutableStateOf("") }
//        var champs by remember { mutableIntStateOf(0) }
//        var active by remember { mutableStateOf(true) }
//       AddTeamDialog(
//            name, onNameChange = { name = it },
//            headquarters = hq, onHqChange = { hq = it },
//            teamPrincipal = principal, onPrincipalChange = { principal = it },
//            championships = champs, onChampsChange = { champs = it },
//            active = active, onActiveChange = { active = it },
//            onDismiss = { showAdd = false },
//            onSave = {
//                val newId = (teams.maxOfOrNull { it.id } ?: 0) + 1
//                viewModel.createTeam(
//                    Team(
//                        id = newId,
//                        name = name,
//                        foundedDate = "${LocalDate.now()}",
//                        headquarters = hq,
//                        teamPrincipal = principal,
//                        engineManufacturer = "Unknown",
//                        championships = champs,
//                        active = active,
//                        enginePowerHP = 0.0,
//                        sponsors = emptyList(),
//                        logoUrl = "ic_launcher_background"
//                    )
//                )
//                currentIndex = viewModel.teams.value.lastIndex
//                showAdd = false
//            }
//        )
//    }
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val scrollModifier =
        if (isLandscape) Modifier.verticalScroll(rememberScrollState()) else Modifier
    Column(
        modifier = scrollModifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
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
            PurpleButton(text = stringResource(R.string.button_previous), onClick = onPrevious)
            PurpleButton(text = stringResource(R.string.button_edit), onClick = onEdit)
            PurpleButton(text = stringResource(R.string.button_next), onClick = onNext)
        }
    }
}

@Composable
fun DriversRow(drivers: List<Driver>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (drivers.isEmpty()) {
            Text(
                text = stringResource(R.string.no_drivers),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
        } else {
            Text(
                text = stringResource(R.string.title_drivers),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally)
            ) {
                items(drivers) { driver ->
                    DriverCard(driver)
                }
            }
        }
    }
}

@Composable
fun DriverCard(driver: Driver) {
    val borderColor = try {
        Color(driver.helmetColor.toColorInt())
    } catch (e: Exception) {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
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
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "#${driver.raceNumber}",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = driver.nationality,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = driver.dateOfBirth,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${driver.contractYears} jaar contract",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${driver.podiumFinishes} podia",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (driver.isRookie) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 2.dp,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        text = stringResource(R.string.badge_rookie),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
        context.resources.getIdentifier(
            logoUrl,
            "drawable",
            context.packageName
        )
    }
    val actualResId = if (resId != 0) resId else R.drawable.ic_launcher_background
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(185.dp)
            .padding(14.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Image(
            painter = painterResource(id = actualResId),
            contentDescription = stringResource(R.string.team_logo_description),
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun TeamInfoCard(team: Team, driverCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            InfoRow(stringResource(R.string.info_hq), team.headquarters)
            InfoRow(stringResource(R.string.info_engine), team.engineManufacturer)
            InfoRow(stringResource(R.string.info_championships), team.championships.toString())
            InfoRow(stringResource(R.string.info_team_principal), team.teamPrincipal)
            InfoRow(stringResource(R.string.info_driver_count), driverCount.toString())
            InfoRow(
                stringResource(R.string.info_active),
                if (team.active) stringResource(R.string.yes) else stringResource(R.string.no)
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.dialog_title_edit_team),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text(stringResource(R.string.label_team_name)) },
                    colors = androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = teamHQ,
                    onValueChange = { teamHQ = it },
                    label = { Text(stringResource(R.string.label_headquarters)) },
                    colors = androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = teamPrincipal,
                    onValueChange = { teamPrincipal = it },
                    label = { Text(stringResource(R.string.label_team_principal)) },
                    colors = androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = championships,
                    onValueChange = { championships = it.filter { it.isDigit() } },
                    label = { Text(stringResource(R.string.label_championships)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.label_active),
                        modifier = Modifier.padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = team.copy(
                        name = teamName,
                        headquarters = teamHQ,
                        teamPrincipal = teamPrincipal,
                        championships = championships.toIntOrNull() ?: team.championships,
                        active = isActive
                    )
                    onSave(updated)
                },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = stringResource(R.string.button_save))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(text = stringResource(R.string.button_cancel))
            }
        }
    )
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddTeamDialog(
//    name: String,
//    onNameChange: (String) -> Unit,
//    headquarters: String,
//    onHqChange: (String) -> Unit,
//    teamPrincipal: String,
//    onPrincipalChange: (String) -> Unit,
//    championships: Int,
//    onChampsChange: (Int) -> Unit,
//    active: Boolean,
//    onActiveChange: (Boolean) -> Unit,
//    onDismiss: () -> Unit,
//    onSave: () -> Unit
//) {
//    val scrollState = rememberScrollState()
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = {
//            Text(
//                text = stringResource(R.string.dialog_title_add_team),
//                style = MaterialTheme.typography.titleLarge,
//                color = MaterialTheme.colorScheme.onBackground
//            )
//        },
//        text = {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .verticalScroll(scrollState)
//                    .padding(8.dp)
//            ) {
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = onNameChange,
//                    label = { Text(stringResource(R.string.label_team_name)) },
//                    colors = androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors(
//                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
//                        focusedBorderColor = MaterialTheme.colorScheme.primary,
//                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                        cursorColor = MaterialTheme.colorScheme.primary
//                    ),
//                    modifier = Modifier.fillMaxWidth()
//                )
//                OutlinedTextField(
//                    value = headquarters,
//                    onValueChange = onHqChange,
//                    label = { Text(stringResource(R.string.label_headquarters)) },
//                    colors = androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors(
//                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
//                        focusedBorderColor = MaterialTheme.colorScheme.primary,
//                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                        cursorColor = MaterialTheme.colorScheme.primary
//                    ),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 8.dp)
//                )
//                OutlinedTextField(
//                    value = teamPrincipal,
//                    onValueChange = onPrincipalChange,
//                    label = { Text(stringResource(R.string.label_team_principal)) },
//                    colors = androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors(
//                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
//                        focusedBorderColor = MaterialTheme.colorScheme.primary,
//                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                        cursorColor = MaterialTheme.colorScheme.primary
//                    ),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 8.dp)
//                )
//                OutlinedTextField(
//                    value = championships.toString(),
//                    onValueChange = {
//                        val digits = it.filter { ch -> ch.isDigit() }
//                        onChampsChange(digits.toIntOrNull() ?: 0)
//                    },
//                    label = { Text(stringResource(R.string.label_championships)) },
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                    colors = androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors(
//                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
//                        focusedBorderColor = MaterialTheme.colorScheme.primary,
//                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                        cursorColor = MaterialTheme.colorScheme.primary
//                    ),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 8.dp)
//                )
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 8.dp)
//                ) {
//                    Text(
//                        text = stringResource(R.string.label_active),
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurface,
//                        modifier = Modifier.padding(end = 8.dp)
//                    )
//                    Switch(
//                        checked = active,
//                        onCheckedChange = onActiveChange,
//                        colors = SwitchDefaults.colors(
//                            checkedThumbColor = MaterialTheme.colorScheme.primary,
//                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
//                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
//                        )
//                    )
//                }
//            }
//        },
//        confirmButton = {
//            Button(
//                onClick = onSave,
//                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    contentColor = MaterialTheme.colorScheme.onPrimary
//                )
//            ) {
//                Text(
//                    text = stringResource(R.string.button_save),
//                    style = MaterialTheme.typography.labelLarge
//                )
//            }
//        },
//        dismissButton = {
//            Button(
//                onClick = onDismiss,
//                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            ) {
//                Text(
//                    text = stringResource(R.string.button_cancel),
//                    style = MaterialTheme.typography.labelLarge
//                )
//            }
//        }
//    )
//}