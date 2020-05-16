package com.battleship.server.websocket.thirdparty

import com.battleship.server.websocket.GameConnection
import org.java_websocket.WebSocket

class WebSocketConnection(private val webSocket: WebSocket) : GameConnection {

    private var events: GameConnection.Events? = null

    override fun send(message: String) {
        println(message)
        webSocket.send(message)
    }

    override fun close() {
        webSocket.close()
    }

    override fun setEvents(events: GameConnection.Events) {
        this.events = events
    }

    override fun getEvents(): GameConnection.Events? {
        return events
    }

}