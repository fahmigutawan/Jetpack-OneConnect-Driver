package com.example.oneconnectdriver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.oneconnectdriver.dashboard.DashboardScreen
import com.example.oneconnectdriver.dialog_dummy_location_picker.DummyLocationPickerScreen
import com.example.oneconnectdriver.login.LoginScreen
import com.example.oneconnectdriver.splash.SplashScreen
import com.example.oneconnectdriver.ui.theme.OneConnectDriverTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            OneConnectDriverTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("login") {
                            LoginScreen(navController = navController)
                        }

                        composable("dashboard") {
                            DashboardScreen(navController = navController)
                        }

                        composable("splash") {
                            SplashScreen(navController = navController)
                        }

                        composable(
                            route = "dummy_location/{em_call_id}/{long}/{lat}",
                            arguments = listOf(
                                navArgument("em_call_id") {
                                    type = NavType.StringType
                                },
                                navArgument("long") {
                                    type = NavType.FloatType
                                },
                                navArgument("lat") {
                                    type = NavType.FloatType
                                }
                            )
                        ) {
                            val emCallId = it.arguments?.getString("em_call_id") ?: ""
                            val long = it.arguments?.getFloat("long") ?: .0
                            val lat = it.arguments?.getFloat("lat") ?: .0

                            DummyLocationPickerScreen(
                                navController = navController,
                                emCallId = emCallId,
                                long = long.toDouble(),
                                lat = lat.toDouble()
                            )
                        }
                    }
                }
            }
        }
    }
}