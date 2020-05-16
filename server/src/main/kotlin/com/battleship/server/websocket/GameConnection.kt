package com.battleship.server.websocket

interface GameConnection {

    fun send(message: String)

    fun close()

    fun getEvents(): Events?

    fun setEvents(events: Events)

    interface Events {
        fun onClose()

        fun onMessage(message: String?)

        fun onError(ex: Exception?)
    }
}