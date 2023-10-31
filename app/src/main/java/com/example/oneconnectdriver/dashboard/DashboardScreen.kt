package com.example.oneconnectdriver.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.oneconnectdriver.CallStatus

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

    LaunchedEffect(key1 = true) {
        viewModel.updateFcmToken()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            //Sedang diproses
            items(
                viewModel.emCallAktifNamunBelumKonfirmasi.filter {
                    it.em_call_status_id == "Bc1fUMyOIZZSDoUFWUSr"
                }
            ) { item ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(128.dp),
                                imageVector = Icons.Default.Phone,
                                contentDescription = ""
                            )
                        }

                        Text(text = "Panggilan Darurat Menunggu", fontSize = 24.sp)
                        Text(text = "Segera Konfirmasi Tugas Anda")
                        Button(modifier = Modifier.fillMaxWidth(), onClick = {
                            viewModel.updateCallStatus(
                                item.em_call_id,
                                "rBiU5gy2mwSus2n96cMu"
                            ) {

                            }
                        }) {
                            Text(text = "Konfirmasi")
                        }
                    }
                }
            }

            //Sedang dalam perjalanan
            items(
                viewModel.emCallAktifNamunBelumKonfirmasi.filter {
                    it.em_call_status_id == "rBiU5gy2mwSus2n96cMu"
                }
            ) { item ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(128.dp),
                                imageVector = Icons.Default.Place,
                                contentDescription = ""
                            )
                        }

                        Text(text = "Anda Sedang Dalam Perjalanan", fontSize = 24.sp)
                        Text(text = "Pastikan Anda Selalu Membuka Aplikasi Agar Live Tracking Berjalan")
                        Button(modifier = Modifier.fillMaxWidth(), onClick = { /*TODO*/ }) {
                            Text(text = "Dummy Location Picker")
                        }
                        Button(modifier = Modifier.fillMaxWidth(), onClick = {
                            viewModel.updateCallStatus(
                                item.em_call_id,
                                "HHxMYs0dSM10gS37PEjk"
                            ) {
                                viewModel.updateTransportAvailability(
                                    true
                                ){
                                    //TODO
                                }
                            }
                        }) {
                            Text(text = "Selesai")
                        }
                    }
                }
            }

            items(
                viewModel.emCallAktifNamunBelumKonfirmasi
                    .filter { it.em_call_status_id != "rBiU5gy2mwSus2n96cMu" }
                    .filter { it.em_call_status_id != "Bc1fUMyOIZZSDoUFWUSr" }
                    .sortedBy { it.em_call_status_id }
            ) { item ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "ID: ${item.em_call_id}")
                        Text(text = "Status: ${CallStatus.get(item.em_call_status_id)}")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}