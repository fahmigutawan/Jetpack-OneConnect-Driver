package com.example.oneconnectdriver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.oneconnectdriver.dashboard.DashboardScreen
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
                    NavHost(navController = navController, startDestination = "splash"){
                        composable("login"){
                            LoginScreen(navController = navController)
                        }

                        composable("dashboard"){
                            DashboardScreen(navController = navController)
                        }

                        composable("splash"){
                            SplashScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}