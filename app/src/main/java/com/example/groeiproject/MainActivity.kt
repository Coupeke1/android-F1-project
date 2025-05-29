package com.example.groeiproject

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.groeiproject.components.InfoRow
import com.example.groeiproject.components.PurpleButton
import com.example.groeiproject.model.Driver
import com.example.groeiproject.model.DriverViewModel
import com.example.groeiproject.model.Team
import com.example.groeiproject.model.TeamViewModel
import com.example.groeiproject.ui.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()

            val autoLandscape by settingsViewModel.autoLandscape.collectAsState(initial = false)

            SideEffect {
                requestedOrientation = if (autoLandscape) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }

            Router()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeamViewer(
    viewModel: TeamViewModel, navController: NavHostController
) {
    val teams by viewModel.teams.collectAsState()
    val drivers by viewModel.drivers.collectAsState()

    var currentIndex by remember { mutableIntStateOf(0) }
    var showEdit by remember { mutableStateOf(false) }
    var showAdd by remember { mutableStateOf(false) }

    LaunchedEffect(currentIndex, teams) {
        teams.getOrNull(currentIndex)?.let { viewModel.selectTeam(it.id) }
    }


    Box(
        Modifier.fillMaxSize()
    ) {
        Column(Modifier.fillMaxSize()) {
            if (teams.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_teams_available),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { showAdd = true }, modifier = Modifier.padding(top = 16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.button_add))
                }
            } else {
                val idx = currentIndex.coerceIn(0, teams.lastIndex)
                val team = teams[idx]
                val count = remember(drivers, team.id) {
                    drivers.count { it.teamId == team.id }
                }
                ArtworkDisplay(
                    team = team,
                    driverCount = count,
                    onPrevious = { if (currentIndex > 0) currentIndex-- },
                    onNext = { if (currentIndex < teams.lastIndex) currentIndex++ },
                    onEdit = { showEdit = true },
                    onDelete = {
                        viewModel.deleteTeam(team.id)
                        currentIndex = (currentIndex - 1).coerceAtLeast(0)
                    },
                    onAdd = { showAdd = true },
                    onShowDrivers = {
                        navController.navigate("drivers/${team.id}") {
                            popUpTo("teams") { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
            }
        }

        if (showEdit) {
            val team = teams.getOrNull(currentIndex) ?: return
            EditTeamDialog(team = team, onDismiss = { showEdit = false }, onSave = {
                viewModel.updateTeam(it)
                showEdit = false
            })
        }

        if (showAdd) {
            var name by remember { mutableStateOf("") }
            var hq by remember { mutableStateOf("") }
            var principal by remember { mutableStateOf("") }
            var champs by remember { mutableIntStateOf(0) }
            var active by remember { mutableStateOf(true) }

            AddTeamDialog(
                name = name,
                onNameChange = { name = it },
                headquarters = hq,
                onHqChange = { hq = it },
                teamPrincipal = principal,
                onPrincipalChange = { principal = it },
                championships = champs,
                onChampsChange = { champs = it },
                active = active,
                onActiveChange = { active = it },
                onDismiss = { showAdd = false },
                onSave = {
                    val newId = (teams.maxOfOrNull { it.id } ?: 0) + 1
                    val newTeam = Team(
                        id = newId,
                        name = name,
                        foundedDate = "${LocalDate.now()}",
                        headquarters = hq,
                        teamPrincipal = principal,
                        engineManufacturer = "Unknown",
                        championships = champs,
                        active = active,
                        enginePowerHP = 0.0,
                        sponsors = emptyList(),
                        logoUrl = "ic_launcher_background"
                    )
                    viewModel.createTeam(newTeam)
                    currentIndex = viewModel.teams.value.lastIndex
                    showAdd = false
                })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DriversViewer(
    teamId: Int = -1, viewModel: DriverViewModel = hiltViewModel()
) {
    val allDrivers by viewModel.drivers.collectAsState()
    val drivers = remember(teamId, allDrivers) {
        if (teamId < 0) allDrivers else allDrivers.filter { it.teamId == teamId }
    }
    var index by rememberSaveable { mutableIntStateOf(0) }
    var showEdit by remember { mutableStateOf(false) }
    var showAdd by remember { mutableStateOf(false) }

    val onPrevious: () -> Unit = { if (index > 0) index-- }
    val onNext: () -> Unit = { if (index < drivers.lastIndex) index++ }
    val onDelete: () -> Unit = {
        drivers.getOrNull(index)?.let { viewModel.deleteDriver(it.id) }
        index = (index - 1).coerceAtLeast(0)
    }
    val onEdit: () -> Unit = { showEdit = true }
    val onAdd: () -> Unit = { showAdd = true }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val autoLandscape by settingsViewModel.autoLandscape.collectAsState(initial = false)

    val scrollModifier =
        if (isLandscape && autoLandscape) Modifier.verticalScroll(rememberScrollState())
        else Modifier

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = scrollModifier
                .weight(5f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            drivers.getOrNull(index)?.let { driver ->
                DriverCard(driver = driver)
            } ?: run {
                Text(
                    text = stringResource(R.string.no_drivers_available),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(14.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onDelete, modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.button_delete))
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onAdd, modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.button_add))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PurpleButton(
                text = stringResource(R.string.button_previous),
                onClick = onPrevious,
                modifier = Modifier.weight(1f)
            )
            PurpleButton(
                text = stringResource(R.string.button_edit),
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            )
            PurpleButton(
                text = stringResource(R.string.button_next),
                onClick = onNext,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(14.dp))
    }

    if (showEdit) {
        drivers.getOrNull(index)?.let { driver ->
            EditDriverDialog(driver = driver, onDismiss = { showEdit = false }, onSave = {
                viewModel.updateDriver(it)
                showEdit = false
            })
        }
    }

    if (showAdd) {
        var fullName by rememberSaveable { mutableStateOf("") }
        var raceNumber by rememberSaveable { mutableStateOf("") }
        var nationality by rememberSaveable { mutableStateOf("") }
        var dateOfBirth by rememberSaveable { mutableStateOf("") }
        var contractYears by rememberSaveable { mutableIntStateOf(0) }
        var podiumFinishes by rememberSaveable { mutableIntStateOf(0) }
        var helmetColor by rememberSaveable { mutableStateOf("#FFFFFF") }
        var isRookie by rememberSaveable { mutableStateOf(false) }

        AddDriverDialog(
            fullName = fullName,
            onFullNameChange = { fullName = it },
            raceNumber = raceNumber,
            onRaceNumberChange = { raceNumber = it.filter { ch -> ch.isDigit() } },
            nationality = nationality,
            onNationalityChange = { nationality = it },
            dateOfBirth = dateOfBirth,
            onDobChange = { dateOfBirth = it },
            contractYears = contractYears,
            onContractChange = { contractYears = it },
            podiumFinishes = podiumFinishes,
            onPodiumChange = { podiumFinishes = it },
            helmetColor = helmetColor,
            onColorChange = { helmetColor = it },
            isRookie = isRookie,
            onRookieChange = { isRookie = it },
            onDismiss = { showAdd = false },
            onSave = {
                val newId = (allDrivers.maxOfOrNull { it.id } ?: 0) + 1
                viewModel.createDriver(
                    Driver(
                        id = newId,
                        fullName = fullName,
                        raceNumber = raceNumber.toIntOrNull() ?: 0,
                        nationality = nationality,
                        dateOfBirth = dateOfBirth,
                        contractYears = contractYears,
                        podiumFinishes = podiumFinishes,
                        helmetColor = helmetColor,
                        isRookie = isRookie,
                        teamId = teamId
                    )
                )
                showAdd = false
                index = drivers.lastIndex
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDriverDialog(
    driver: Driver, onDismiss: () -> Unit, onSave: (Driver) -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf(driver.fullName) }
    var raceNumber by rememberSaveable { mutableStateOf(driver.raceNumber.toString()) }
    var nationality by rememberSaveable { mutableStateOf(driver.nationality) }
    var dateOfBirth by rememberSaveable { mutableStateOf(driver.dateOfBirth) }
    var contractYears by rememberSaveable { mutableIntStateOf(driver.contractYears) }
    var podiumFinishes by rememberSaveable { mutableIntStateOf(driver.podiumFinishes) }
    var helmetColor by rememberSaveable { mutableStateOf(driver.helmetColor) }
    var isRookie by rememberSaveable { mutableStateOf(driver.isRookie) }
    val scrollState = rememberScrollState()

    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = stringResource(R.string.dialog_title_edit_driver),
            style = MaterialTheme.typography.titleLarge
        )
    }, text = {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(stringResource(R.string.label_full_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = raceNumber,
                onValueChange = { raceNumber = it.filter { ch -> ch.isDigit() } },
                label = { Text(stringResource(R.string.label_race_number)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nationality,
                onValueChange = { nationality = it },
                label = { Text(stringResource(R.string.label_nationality)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = { Text(stringResource(R.string.label_dob)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = contractYears.toString(),
                onValueChange = {
                    contractYears = it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: contractYears
                },
                label = { Text(stringResource(R.string.label_contract_years)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = podiumFinishes.toString(),
                onValueChange = {
                    podiumFinishes =
                        it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: podiumFinishes
                },
                label = { Text(stringResource(R.string.label_podium_finishes)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = helmetColor,
                onValueChange = { helmetColor = it },
                label = { Text(stringResource(R.string.label_helmet_color)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.label_rookie),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(checked = isRookie, onCheckedChange = { isRookie = it })
            }
        }
    }, confirmButton = {
        Button(onClick = {
            onSave(
                driver.copy(
                    fullName = fullName,
                    raceNumber = raceNumber.toIntOrNull() ?: driver.raceNumber,
                    nationality = nationality,
                    dateOfBirth = dateOfBirth,
                    contractYears = contractYears,
                    podiumFinishes = podiumFinishes,
                    helmetColor = helmetColor,
                    isRookie = isRookie
                )
            )
        }) {
            Text(text = stringResource(R.string.button_save))
        }
    }, dismissButton = {
        Button(onClick = onDismiss) {
            Text(text = stringResource(R.string.button_cancel))
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDriverDialog(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    raceNumber: String,
    onRaceNumberChange: (String) -> Unit,
    nationality: String,
    onNationalityChange: (String) -> Unit,
    dateOfBirth: String,
    onDobChange: (String) -> Unit,
    contractYears: Int,
    onContractChange: (Int) -> Unit,
    podiumFinishes: Int,
    onPodiumChange: (Int) -> Unit,
    helmetColor: String,
    onColorChange: (String) -> Unit,
    isRookie: Boolean,
    onRookieChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val scrollState = rememberScrollState()

    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = stringResource(R.string.dialog_title_add_driver),
            style = MaterialTheme.typography.titleLarge
        )
    }, text = {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text(stringResource(R.string.label_full_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = raceNumber,
                onValueChange = onRaceNumberChange,
                label = { Text(stringResource(R.string.label_race_number)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nationality,
                onValueChange = onNationalityChange,
                label = { Text(stringResource(R.string.label_nationality)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = onDobChange,
                label = { Text(stringResource(R.string.label_dob)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = contractYears.toString(),
                onValueChange = {
                    onContractChange(it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0)
                },
                label = { Text(stringResource(R.string.label_contract_years)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = podiumFinishes.toString(),
                onValueChange = {
                    onPodiumChange(
                        it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0
                    )
                },
                label = { Text(stringResource(R.string.label_podium_finishes)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = helmetColor,
                onValueChange = onColorChange,
                label = { Text(stringResource(R.string.label_helmet_color)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.label_rookie),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(checked = isRookie, onCheckedChange = onRookieChange)
            }
        }
    }, confirmButton = {
        Button(onClick = onSave) {
            Text(text = stringResource(R.string.button_save))
        }
    }, dismissButton = {
        Button(onClick = onDismiss) {
            Text(text = stringResource(R.string.button_cancel))
        }
    })
}


@Composable
fun ArtworkDisplay(
    team: Team,
    driverCount: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAdd: () -> Unit,
    onShowDrivers: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val autoLandscape by settingsViewModel.autoLandscape.collectAsState(initial = false)

    val scrollModifier =
        if (isLandscape && autoLandscape) Modifier.verticalScroll(rememberScrollState())
        else Modifier


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = scrollModifier
                .weight(5f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            TeamLogo(team.logoUrl)
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = team.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(14.dp))

            TeamInfoCard(team, driverCount)
            Spacer(modifier = Modifier.height(14.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Button(
                onClick = onDelete, modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.button_delete))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onAdd, modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.button_add))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PurpleButton(
                text = stringResource(R.string.button_previous),
                onClick = onPrevious,
                modifier = Modifier.weight(1f)
            )
            PurpleButton(
                text = stringResource(R.string.button_edit),
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            )
            PurpleButton(
                text = stringResource(R.string.button_next),
                onClick = onNext,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))

    }
}

@Composable
fun DriverCard(driver: Driver) {
    val borderColor =
        runCatching { Color(driver.helmetColor.toColorInt()) }.getOrDefault(MaterialTheme.colorScheme.onSurface)

    Card(
        modifier = Modifier
            .width(240.dp)
            .height(300.dp)
            .border(3.dp, borderColor, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = driver.fullName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "#${driver.raceNumber}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = driver.nationality,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = driver.dateOfBirth,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "${driver.contractYears} jaar contract",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "${driver.podiumFinishes} podia",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (driver.isRookie) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 4.dp,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        text = stringResource(R.string.badge_rookie),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun TeamLogo(logoUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(14.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp)
            )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current).data(logoUrl)
                .crossfade(true).build(),
            contentDescription = stringResource(R.string.team_logo_description),
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Fit,
            placeholder = painterResource(R.drawable.loading_img),
            error = painterResource(R.drawable.ic_broken_image)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TeamInfoCard(
    team: Team,
    driverCount: Int,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val showSponsors by settingsViewModel.showSponsors.collectAsState(initial = true)

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
            modifier = Modifier.padding(22.dp, 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            InfoRow(stringResource(R.string.info_hq), team.headquarters)
            InfoRow(stringResource(R.string.info_engine), team.engineManufacturer)
            InfoRow(stringResource(R.string.info_championships), team.championships.toString())
            InfoRow(stringResource(R.string.info_team_principal), team.teamPrincipal)

            if (showSponsors && team.sponsors.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.sponsors),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    team.sponsors.forEach { sponsor ->
                        Text(
                            text = sponsor,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            InfoRow(stringResource(R.string.info_driver_count), driverCount.toString())
            InfoRow(
                stringResource(R.string.info_active),
                if (team.active) stringResource(R.string.yes) else stringResource(R.string.no)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTeamDialog(
    team: Team, onDismiss: () -> Unit, onSave: (Team) -> Unit
) {
    var teamName by remember { mutableStateOf(team.name) }
    var teamHQ by remember { mutableStateOf(team.headquarters) }
    var teamPrincipal by remember { mutableStateOf(team.teamPrincipal) }
    var championships by remember { mutableStateOf(team.championships.toString()) }
    var isActive by remember { mutableStateOf(team.active) }
    val scrollState = rememberScrollState()

    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = stringResource(R.string.dialog_title_edit_team),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
        )
    }, text = {
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
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
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
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
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
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
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
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
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
    }, confirmButton = {
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
            }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = stringResource(R.string.button_save))
        }
    }, dismissButton = {
        Button(
            onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(text = stringResource(R.string.button_cancel))
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTeamDialog(
    name: String,
    onNameChange: (String) -> Unit,
    headquarters: String,
    onHqChange: (String) -> Unit,
    teamPrincipal: String,
    onPrincipalChange: (String) -> Unit,
    championships: Int,
    onChampsChange: (Int) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val scrollState = rememberScrollState()

    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = stringResource(R.string.dialog_title_add_team),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }, text = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.label_team_name)) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = headquarters,
                onValueChange = onHqChange,
                label = { Text(stringResource(R.string.label_headquarters)) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            OutlinedTextField(
                value = teamPrincipal,
                onValueChange = onPrincipalChange,
                label = { Text(stringResource(R.string.label_team_principal)) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            OutlinedTextField(
                value = championships.toString(),
                onValueChange = {
                    val digits = it.filter { ch -> ch.isDigit() }
                    onChampsChange(digits.toIntOrNull() ?: 0)
                },
                label = { Text(stringResource(R.string.label_championships)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_active),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = active,
                    onCheckedChange = onActiveChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }
    }, confirmButton = {
        Button(
            onClick = onSave, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.button_save),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }, dismissButton = {
        Button(
            onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = stringResource(R.string.button_cancel),
                style = MaterialTheme.typography.labelLarge
            )
        }
    })
}