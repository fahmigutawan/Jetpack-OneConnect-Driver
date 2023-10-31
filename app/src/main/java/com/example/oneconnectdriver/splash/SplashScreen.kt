package com.example.oneconnectdriver.splash

import android.window.SplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun SplashScreen(
    navController: NavController
) {
    val viewModel = hiltViewModel<SplashViewModel>()

    LaunchedEffect(key1 = true){
        viewModel.precheck { isLogin ->
            if(isLogin){
                navController.navigate("dashboard"){
                    popUpTo(navController.graph.id){
                        inclusive = true
                    }
                }
            }else{
                navController.navigate("login"){
                    popUpTo(navController.graph.id){
                        inclusive = true
                    }
                }
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        CircularProgressIndicator()
    }
}