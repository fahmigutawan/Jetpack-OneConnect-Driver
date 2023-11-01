package com.example.oneconnectdriver.dashboard

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oneconnectdriver.Repository
import com.example.oneconnectdriver.model.domain.EmTransportModel
import com.example.oneconnectdriver.model.struct.EmCallStruct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val emTransportModel = mutableStateOf<EmTransportModel?>(null)
    val emTransportId = mutableStateOf("")
    val emCalls = mutableStateListOf<EmCallStruct>()

    val showRationaleDialog = mutableStateOf(false)
    val showPermissionWarningDialog = mutableStateOf(false)

    val long = mutableStateOf(.0)
    val lat = mutableStateOf(.0)

    fun getEmTransportById(emTransportId: String){
        repository.getEmTransportById(emTransportId){
            emTransportModel.value = it
        }
    }

    fun logout(
        onSuccess:() -> Unit
    ){
        viewModelScope.launch {
            repository.deleteFcmToken {
                viewModelScope.launch {
                    repository.logout()
                    delay(1500)
                    onSuccess()
                }
            }
        }
    }

    fun updateFcmToken(){
        viewModelScope.launch {
            repository.updateFcmToken()
        }
    }

    fun updateCallStatus(
        emCallId:String,
        status:String,
        onSuccess: () -> Unit
    ){
        repository.updateCallStatus(emCallId, status, onSuccess)
    }

    fun updateTransportAvailability(status:Boolean, onSuccess: () -> Unit){
        viewModelScope.launch {
            repository.updateTransportAvailability(status, onSuccess)
        }
    }

    fun updateLocationLiveTracking(
        em_call_id: String,
        long: Double,
        lat: Double
    ){
        repository.updateLocationLiveTracking(em_call_id, long, lat)
    }

    fun updateEmCalls(){
        viewModelScope.launch {
            repository.getEmCall {
                emCalls.clear()
                emCalls.addAll(it)
            }
        }
    }

    init {
        viewModelScope.launch {
            emTransportId.value = repository.getTransportId()
        }

        updateEmCalls()
    }
}