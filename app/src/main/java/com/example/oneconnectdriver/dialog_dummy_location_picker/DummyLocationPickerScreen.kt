package com.example.oneconnectdriver.dialog_dummy_location_picker

import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.oneconnectdriver.databinding.OsmdroidViewBinding
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.config.Configuration.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Marker.OnMarkerDragListener

@Composable
fun DummyLocationPickerScreen(
    navController: NavController,
    emCallId:String,
    long:Double,
    lat:Double
) {
    val viewModel = hiltViewModel<DummyLocationPickerViewModel>()
    val mapView = remember{
        mutableStateOf<org.osmdroid.views.MapView?>(null)
    }
    val context = LocalContext.current

    BackHandler {
        navController.navigate("dashboard"){
            popUpTo(navController.graph.id){
                inclusive = true
            }
        }
    }

    LaunchedEffect(key1 = mapView.value){
        mapView.value?.let {
            it.apply {
                setMultiTouchControls(true)
            }

            val marker= Marker(it)
            marker.apply {
                position = GeoPoint(lat, long)
                isDraggable = true
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                setOnMarkerDragListener(object : OnMarkerDragListener {
                    override fun onMarkerDrag(marker: Marker?) {
                        marker?.let {
                            val position = it.position
                            Log.e("DJSALKD", "DRAGGING")

                            viewModel.updateLocationLiveTracking(
                                em_call_id = emCallId,
                                long = position.longitude,
                                lat = position.latitude
                            )
                        }
                    }

                    override fun onMarkerDragEnd(marker: Marker?) {
                    }

                    override fun onMarkerDragStart(marker: Marker?) {
                    }

                })
            }

            it.overlays.clear()
            it.overlays.add(marker)

            val mapController = it.controller
            mapController.apply {
                setCenter(GeoPoint(lat, long))
                setZoom(18.0)
            }
        }
    }

    AndroidView(
        factory = {
            val binding = OsmdroidViewBinding.inflate(LayoutInflater.from(it))

            getInstance().load(it, PreferenceManager.getDefaultSharedPreferences(it))

            binding.root
        },
        update = {
            it.apply {
                setTileSource(TileSourceFactory.MAPNIK)
            }
            mapView.value = it
        }
    )
}