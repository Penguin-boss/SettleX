package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.ExpenseEntryViewModel
import com.example.ui.viewmodel.GroupDetailViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()

                    // Instantiate ViewModels at Activity level
                    val authViewModel: AuthViewModel = viewModel()
                    val dashboardViewModel: DashboardViewModel = viewModel()
                    val groupDetailViewModel: GroupDetailViewModel = viewModel()
                    val expenseEntryViewModel: ExpenseEntryViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "mode_select"
                    ) {
                        composable("mode_select") {
                            ModeSelectScreen(
                                authViewModel = authViewModel,
                                dashboardViewModel = dashboardViewModel,
                                onNavigateJudgesDemo = {
                                    navController.navigate("judges_demo") {
                                        popUpTo("mode_select") { inclusive = true }
                                    }
                                },
                                onNavigateDashboard = {
                                    navController.navigate("dashboard") {
                                        popUpTo("mode_select") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateDashboard = {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateProfileSetup = {
                                    navController.navigate("profile_setup") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("profile_setup") {
                            ProfileSetupScreen(
                                viewModel = authViewModel,
                                onNavigateDashboard = {
                                    navController.navigate("dashboard") {
                                        popUpTo("profile_setup") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("dashboard") {
                            DashboardScreen(
                                authViewModel = authViewModel,
                                dashboardViewModel = dashboardViewModel,
                                onNavigateGroupDetail = { groupId ->
                                    navController.navigate("group/$groupId")
                                },
                                onNavigateAddExpense = { groupId ->
                                    navController.navigate("add_expense/$groupId")
                                },
                                onNavigateJoinGroup = {
                                    navController.navigate("join_group")
                                },
                                onNavigateJudgesDemo = {
                                    navController.navigate("judges_demo")
                                }
                            )
                        }

                        composable(
                            route = "group/{groupId}",
                            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                            GroupDetailScreen(
                                groupId = groupId,
                                viewModel = groupDetailViewModel,
                                dashboardViewModel = dashboardViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateAddExpense = { gId ->
                                    navController.navigate("add_expense/$gId")
                                }
                            )
                        }

                        composable(
                            route = "add_expense/{groupId}",
                            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                            ExpenseEntryScreen(
                                groupId = groupId,
                                viewModel = expenseEntryViewModel,
                                groupViewModel = groupDetailViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToCameraScan = { gId ->
                                    navController.navigate("camera_scan/$gId")
                                }
                            )
                        }

                        composable(
                            route = "camera_scan/{groupId}",
                            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                        ) {
                            CameraScreen(
                                onPhotoCaptured = { bitmap ->
                                    expenseEntryViewModel.analyzeReceipt(bitmap)
                                    navController.popBackStack()
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("join_group") {
                            JoinGroupScreen(
                                dashboardViewModel = dashboardViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("judges_demo") {
                            JudgesDemoScreen(
                                dashboardViewModel = dashboardViewModel,
                                groupViewModel = groupDetailViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
