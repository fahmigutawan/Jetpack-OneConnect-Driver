package com.example.oneconnectdriver.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oneconnectdriver.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    fun precheck(
        login:(Boolean) -> Unit
    ){
        viewModelScope.launch {
            login(repository.isLogin())
        }
    }
}