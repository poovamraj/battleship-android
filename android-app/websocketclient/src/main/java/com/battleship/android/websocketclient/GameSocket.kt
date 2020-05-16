package com.battleship.android.websocketclient

interface GameSocket {

    fun connect()

    fun sendMessage(message: String)

    fun disconnect()

    fun setEvents(events: Events)

    interface Events {
        fun onOpen()

        fun onClose()

        fun onMessage(message: String)

        fun onError(ex: Exception)
    }

}