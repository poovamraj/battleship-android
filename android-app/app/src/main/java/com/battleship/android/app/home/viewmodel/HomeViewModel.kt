package com.battleship.android.app.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.battleship.android.app.home.model.CheckServer
import com.battleship.android.websocketclient.WebSocketBuilder

class HomeViewModel : ViewModel(){

    val onlineGameLoading by lazy { MutableLiveData<Boolean>() }

    fun createRoom(address: String, port: Int, onSuccess: ()-> Unit, onFailure: (Exception)->Unit){
        onlineGameLoading.value = true
        checkServer(address, port, onSuccess, onFailure)
    }

    fun joinRoom(address: String, port: Int, onSuccess: ()-> Unit, onFailure: (Exception)->Unit){
        onlineGameLoading.value = true
        checkServer(address, port, onSuccess, onFailure)
    }

    private fun checkServer(address: String, port: Int, onSuccess: ()-> Unit, onFailure: (Exception)->Unit){
        val checkServer = CheckServer(WebSocketBuilder.createClient(address, port),{
            it.disconnect()
            onSuccess.invoke()
        }, {
            onlineGameLoading.value = false
            onFailure.invoke(it)
        })
        checkServer.connect()
    }
}