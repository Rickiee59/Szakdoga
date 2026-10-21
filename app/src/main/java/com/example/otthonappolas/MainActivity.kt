package com.example.otthonappolas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


import com.example.otthonappolas.ui.theme.OtthonAPPolasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OtthonAPPolasTheme {
                val navController = rememberNavController()


                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route


                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {

                        if (currentRoute == "list" || currentRoute == "past_shifts") {
                            NavigationBar {

                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.List, contentDescription = "Aktuális") },
                                    label = { Text("Aktuális") },
                                    selected = currentRoute == "list",
                                    onClick = {
                                        navController.navigate("list") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )

                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Múltbéli") },
                                    label = { Text("Múltbéli") },
                                    selected = currentRoute == "past_shifts",
                                    onClick = {
                                        navController.navigate("past_shifts") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { paddingValues ->

                    NavHost(
                        navController = navController,
                        startDestination = "list",
                        modifier = Modifier.padding(paddingValues)
                    ) {


                        composable("list") {
                            PatientListScreen(
                                patients = dummyPatients,
                                onPatientClick = { selectedPatient ->
                                    navController.navigate("details/${selectedPatient.id}")
                                }
                            )
                        }


                        composable("past_shifts") {
                            PastShiftsScreen()
                        }


                        composable("details/{patientId}") { backStackEntry ->
                            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
                            PatientDetailScreen(
                                patientId = patientId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}