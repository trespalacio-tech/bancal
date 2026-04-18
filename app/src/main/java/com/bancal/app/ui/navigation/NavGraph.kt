package com.bancal.app.ui.navigation

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bancal.app.ui.screens.alertas.AlertasScreen
import com.bancal.app.ui.screens.alertas.AlertasViewModel
import com.bancal.app.ui.screens.balance.BalanceScreen
import com.bancal.app.ui.screens.balance.BalanceViewModel
import com.bancal.app.ui.screens.diario.DiarioScreen
import com.bancal.app.ui.screens.diario.DiarioViewModel
import com.bancal.app.ui.screens.gestion.GestionBancalesScreen
import com.bancal.app.ui.screens.onboarding.OnboardingScreen
import com.bancal.app.ui.screens.gestion.GestionBancalesViewModel
import com.bancal.app.ui.screens.sucesion.SucesionScreen
import com.bancal.app.ui.screens.sucesion.SucesionViewModel
import com.bancal.app.ui.screens.bancal.BancalScreen
import com.bancal.app.ui.screens.bancal.BancalViewModel
import com.bancal.app.ui.screens.backup.BackupScreen
import com.bancal.app.ui.screens.calendario.CalendarioScreen
import com.bancal.app.ui.screens.calendario.CalendarioViewModel
import com.bancal.app.ui.screens.cultivopersonalizado.CultivoPersonalizadoScreen
import com.bancal.app.ui.screens.cultivopersonalizado.CultivoPersonalizadoViewModel
import com.bancal.app.data.preferences.BancalPreferences
import com.bancal.app.ui.screens.detalle.DetalleScreen
import com.bancal.app.ui.screens.detalle.DetalleViewModel
import com.bancal.app.ui.screens.plantar.PlantarScreen
import com.bancal.app.ui.screens.plantar.PlantarViewModel
import com.bancal.app.ui.screens.tratamientos.TratamientosScreen
import com.bancal.app.ui.screens.tratamientos.TratamientosViewModel

sealed class Screen(val route: String) {
    data object Bancal : Screen("bancal")
    data object Calendario : Screen("calendario")
    data object Tratamientos : Screen("tratamientos")
    data object Alertas : Screen("alertas")
    data object Plantar : Screen("plantar?posX={posX}") {
        fun createRoute(posX: Int = 0) = "plantar?posX=$posX"
    }
    data object Detalle : Screen("detalle/{plantacionId}") {
        fun createRoute(id: Long) = "detalle/$id"
    }
    data object Balance : Screen("balance")
    data object Sucesion : Screen("sucesion")
    data object Diario : Screen("diario")
    data object Gestion : Screen("gestion")
    data object CultivoPersonalizado : Screen("cultivo_personalizado")
    data object Backup : Screen("backup")
    data object Onboarding : Screen("onboarding")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Bancal, "Bancal", Icons.Default.Grass),
    BottomNavItem(Screen.Calendario, "Calendario", Icons.Default.CalendarMonth),
    BottomNavItem(Screen.Tratamientos, "Tratamientos", Icons.Default.Science),
    BottomNavItem(Screen.Alertas, "Alertas", Icons.Default.Notifications)
)

@Composable
fun BancalNavHost() {
    val context = LocalContext.current
    val navController = rememberNavController()
    // Una sola instancia a nivel de Activity, compartida por bottom bar y Bancal screen.
    // El init{} ya no dispara queries eagerly, así que no penaliza arranque.
    val bancalViewModel: BancalViewModel = viewModel()
    val alertasPendientes by bancalViewModel.alertasPendientes.collectAsState()

    val onboardingDone = remember { BancalPreferences.isOnboardingDone(context) }
    val startDestination = if (onboardingDone) Screen.Bancal.route else Screen.Onboarding.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.screen.route } && currentRoute != Screen.Onboarding.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                if (currentRoute != item.screen.route) {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(Screen.Bancal.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                if (item.screen == Screen.Alertas && alertasPendientes > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge { Text("$alertasPendientes") }
                                        }
                                    ) {
                                        Icon(item.icon, contentDescription = item.label)
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = item.label)
                                }
                            },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinish = {
                        BancalPreferences.setOnboardingDone(context)
                        navController.navigate(Screen.Bancal.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Bancal.route) {
                BancalScreen(
                    viewModel = bancalViewModel,
                    onNavigateToPlantarAt = { posX ->
                        navController.navigate(Screen.Plantar.createRoute(posX))
                    },
                    onNavigateToDetalle = { id ->
                        navController.navigate(Screen.Detalle.createRoute(id))
                    },
                    onNavigateToPlantar = {
                        navController.navigate(Screen.Plantar.createRoute(0))
                    },
                    onNavigateToBalance = {
                        navController.navigate(Screen.Balance.route)
                    },
                    onNavigateToSucesion = {
                        navController.navigate(Screen.Sucesion.route)
                    },
                    onNavigateToDiario = {
                        navController.navigate(Screen.Diario.route)
                    },
                    onNavigateToGestion = {
                        navController.navigate(Screen.Gestion.route)
                    },
                    onNavigateToBackup = {
                        navController.navigate(Screen.Backup.route)
                    }
                )
            }

            composable(Screen.Calendario.route) {
                val vm: CalendarioViewModel = viewModel()
                CalendarioScreen(viewModel = vm)
            }

            composable(Screen.Tratamientos.route) {
                val vm: TratamientosViewModel = viewModel()
                TratamientosScreen(viewModel = vm)
            }

            composable(Screen.Alertas.route) {
                val vm: AlertasViewModel = viewModel()
                AlertasScreen(viewModel = vm)
            }

            composable(Screen.Balance.route) {
                val vm: BalanceViewModel = viewModel()
                BalanceScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Sucesion.route) {
                val vm: SucesionViewModel = viewModel()
                SucesionScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Gestion.route) {
                val vm: GestionBancalesViewModel = viewModel()
                GestionBancalesScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Diario.route) {
                val vm: DiarioViewModel = viewModel()
                DiarioScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "plantar?posX={posX}",
                arguments = listOf(
                    navArgument("posX") { type = NavType.IntType; defaultValue = 0 }
                )
            ) { backStackEntry ->
                val posX = backStackEntry.arguments?.getInt("posX") ?: 0
                val vm: PlantarViewModel = viewModel()
                PlantarScreen(
                    viewModel = vm,
                    initialPosX = posX,
                    onBack = { navController.popBackStack() },
                    onNavigateToCultivoPersonalizado = {
                        navController.navigate(Screen.CultivoPersonalizado.route)
                    }
                )
            }

            composable(Screen.Backup.route) {
                val context = LocalContext.current
                BackupScreen(
                    onBack = { navController.popBackStack() },
                    onRestaurado = {
                        // Reiniciar la app para que Room reabra la BD restaurada
                        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    }
                )
            }

            composable(Screen.CultivoPersonalizado.route) {
                val vm: CultivoPersonalizadoViewModel = viewModel()
                CultivoPersonalizadoScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "detalle/{plantacionId}",
                arguments = listOf(
                    navArgument("plantacionId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val plantacionId = backStackEntry.arguments?.getLong("plantacionId") ?: 0L
                val vm: DetalleViewModel = viewModel()
                DetalleScreen(
                    viewModel = vm,
                    plantacionId = plantacionId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
