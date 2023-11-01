package com.example.oneconnectdriver.dialog_dummy_location_picker

import androidx.lifecycle.ViewModel
import com.example.oneconnectdriver.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DummyLocationPickerViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    fun updateLocationLiveTracking(
        em_call_id: String,
        long: Double,
        lat: Double
    ){
        repository.updateLocationLiveTracking(em_call_id, long, lat)
    }
}