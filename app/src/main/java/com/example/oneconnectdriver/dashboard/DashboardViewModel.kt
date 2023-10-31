package com.example.oneconnectdriver.dashboard

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
    val emCallAktifNamunBelumKonfirmasi = mutableStateListOf<EmCallStruct>()
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

    init {
        viewModelScope.launch {
            emTransportId.value = repository.getTransportId()
        }

        viewModelScope.launch {
            repository.getEmCallSedangAktif {
                emCallAktifNamunBelumKonfirmasi.clear()
                emCallAktifNamunBelumKonfirmasi.addAll(it)
            }
        }
    }
}