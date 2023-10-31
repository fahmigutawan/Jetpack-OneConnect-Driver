package com.example.oneconnectdriver.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun DashboardScreen(
    navController: NavController
) {
    val viewModel = hiltViewModel<DashboardViewModel>()

    LaunchedEffect(key1 = viewModel.emTransportId.value) {
        if (viewModel.emTransportId.value.isNotEmpty()) {
            viewModel.getEmTransportById(
                viewModel.emTransportId.value
            )
        }
    }

    LaunchedEffect(key1 = true){
        viewModel.updateFcmToken()
    }

    Column {
        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 16.dp
            ),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    viewModel.emTransportModel.value?.let {
                        Text(
                            text = it.em_pvd_id,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 20.sp
                        )
                        Text(text = it.pvd_name, overflow = TextOverflow.Ellipsis, fontSize = 14.sp)
                        Text(
                            text = it.regist_number,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 14.sp
                        )
                    }
                }
                Button(
                    onClick = {
                        viewModel.logout {
                            navController.navigate("login") {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                ) {
                    Text(text = "Logout")
                }
            }
        }
    }
}