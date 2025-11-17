# F1 Management App

Moderne Android-app gebouwd met **Jetpack Compose**, **MVVM**, **Hilt**, **DataStore** en een eigen API.
Gebruikers kunnen Formule 1 teams en drivers beheren: toevoegen, bewerken, verwijderen en bekijken.

---

## Features

### Core Functionaliteit

* CRUD voor **Teams**
* CRUD voor **Drivers**
* Dynamische filtering van drivers per team
* Automatische selectie van huidig team
* Dialogs voor toevoegen/bewerken (Compose Material3)

### UI & UX (Compose)

* Jetpack Compose UI: Cards, NavigationBar, Dialogs, AsyncImage
* Responsive layout (portrait + landscape)
* Paginanavigatie via Navigation Compose
* Theming via Material 3 + custom AppTheme

### Architectuur

* **MVVM** met `ViewModel`, `StateFlow`, `viewModelScope`
* **Repository pattern** voor API-communicatie
* **Hilt DI** voor ViewModels, Repository en DataStore
* **DataStore Preferences** voor instellingen:

  * Auto-landscape mode
  * Sponsors tonen ja/nee

### Technologieën

* Kotlin
* Jetpack Compose
* Hilt
* DataStore Preferences
* Navigation Compose
* Coil (AsyncImage)
* kotlinx.serialization

---

## Projectstructuur

```
app/
 ├─ model/
 │   ├─ Team.kt
 │   ├─ Driver.kt
 │   ├─ F1Repository.kt
 │   ├─ TeamViewModel.kt
 │   └─ DriverViewModel.kt
 │
 ├─ ui/
 │   ├─ components/
 │   ├─ settings/
 │   └─ theme/
 │
 ├─ di/
 │   └─ DataStoreModule.kt
 │
 ├─ MainActivity.kt
 ├─ F1App.kt
 └─ TeamScreen.kt  (Router + Navigation)
```

---

## Kernmodules

### **Repository**

`F1Repository` communiceert met `TeamApiService` en levert data aan de ViewModels.

### **ViewModels**

* `TeamViewModel` beheert teams + driver count
* `DriverViewModel` beheert drivers CRUD
* Alle data wordt opgeslagen in `MutableStateFlow`

### **Navigation**

3 hoofdroutes:

```
Teams
Drivers
Settings
```

Dynamische route:

```
drivers/{teamId}
```

---

## Installatie & Development

### 1. Clone

```
git clone https://github.com/yourusername/f1-android-app
```

### 2. Openen in Android Studio

* Android Studio Hedgehog of nieuwer
* Gradle sync start automatisch

### 3. Build & Run

Gebruik een emulator met Android 12+ of een fysiek toestel.

---

## Resultaat

Een moderne en schaalbare F1 management app met een volledig **Compose + MVVM + Hilt** stack, ontworpen voor uitbreidbaarheid en professioneel gebruik.
