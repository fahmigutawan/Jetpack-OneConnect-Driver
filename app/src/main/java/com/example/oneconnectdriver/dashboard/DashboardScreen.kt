package com.example.oneconnectdriver.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.oneconnectdriver.AppDialog
import com.example.oneconnectdriver.CallStatus
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DashboardScreen(
    navController: NavController
) {
    val viewModel = hiltViewModel<DashboardViewModel>()
    val context = LocalContext.current
    val openSettingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val openSettingUri = Uri.fromParts("package", context.packageName, null)
    openSettingIntent.data = openSettingUri
    val permission = rememberPermissionState(
        permission = android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val callback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            if (permission.status.isGranted) {
                if (viewModel.emCalls.any { it.em_call_status_id == CallStatus.DALAM_PERJALANAN }) {
                    val newest =
                        viewModel.emCalls.filter { it.em_call_status_id == CallStatus.DALAM_PERJALANAN }
                            .sortedByDescending { it.created_at }[0]

                    p0.lastLocation?.let {
                        viewModel.updateLocationLiveTracking(
                            em_call_id = newest.em_call_id,
                            long = it.longitude,
                            lat = it.latitude
                        )
                    }
                }
            }
        }
    }
    val updateRequest = LocationRequest.create()
    updateRequest
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(2000)


    LaunchedEffect(key1 = viewModel.emCalls.toList()) {
        if (permission.status.isGranted) {
            if (viewModel.emCalls.any { it.em_call_status_id == CallStatus.DALAM_PERJALANAN }) {
                val newest =
                    viewModel.emCalls.filter { it.em_call_status_id == CallStatus.DALAM_PERJALANAN }
                        .sortedByDescending { it.created_at }[0]

                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    viewModel.updateLocationLiveTracking(
                        em_call_id = newest.em_call_id,
                        long = loc.longitude,
                        lat = loc.latitude
                    )

                    fusedLocationClient.requestLocationUpdates(
                        updateRequest,
                        callback,
                        null
                    )
                }
            }else{
                fusedLocationClient.removeLocationUpdates(callback)
            }
        }
    }

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

    if (viewModel.showRationaleDialog.value) {
        AppDialog(
            onDismissRequest = {
                viewModel.showRationaleDialog.value = false
            },
            description = "Untuk mengakses halaman MAP, pastikan anda sudah mengijinkan akses lokasi dari HP anda",
            secondButton = {
                Button(
                    onClick = {
                        permission.launchPermissionRequest()
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Ijinkan")
                }
            }
        )
    }

    if (viewModel.showPermissionWarningDialog.value) {
        AppDialog(
            onDismissRequest = {
                viewModel.showPermissionWarningDialog.value = false
            },
            description = "Sepertinya anda harus mengijinkan akses lokasi secara manual, klik tombol di bawah ini!",
            secondButton = {
                Button(
                    onClick = {
                        context.startActivity(openSettingIntent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Buka setting")
                }
            }
        )
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
                viewModel.emCalls.filter {
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
                            if (permission.status.isGranted) {
                                viewModel.showRationaleDialog.value = false
                                viewModel.showPermissionWarningDialog.value = false

                                viewModel.updateCallStatus(
                                    item.em_call_id,
                                    "rBiU5gy2mwSus2n96cMu"
                                ) {

                                }
                            } else {
                                if (permission.status.shouldShowRationale) {
                                    viewModel.showRationaleDialog.value = true
                                    viewModel.showPermissionWarningDialog.value = false
                                } else {
                                    viewModel.showRationaleDialog.value = false
                                    viewModel.showPermissionWarningDialog.value = true
                                }
                            }

                        }) {
                            Text(text = "Konfirmasi")
                        }
                    }
                }
            }

            //Sedang dalam perjalanan
            items(
                viewModel.emCalls.filter {
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
                                ) {
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
                viewModel.emCalls
                    .filter { it.em_call_status_id != "rBiU5gy2mwSus2n96cMu" }
                    .filter { it.em_call_status_id != "Bc1fUMyOIZZSDoUFWUSr" }
                    .sortedByDescending { it.em_call_status_id }
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