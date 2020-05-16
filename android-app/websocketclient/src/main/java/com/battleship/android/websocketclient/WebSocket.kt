package com.battleship.android.websocketclient

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer

internal class SocketImpl(private val client: JavaNetWebSocketClient) : GameSocket {

    override fun connect() {
        client.connect()
    }

    override fun sendMessage(message: String) {
        client.send(message)
    }

    override fun disconnect() {
        client.close()
    }

    override fun setEvents(events: GameSocket.Events) {
        client.events = events
    }

}

internal class JavaNetWebSocketClient(serverURI: URI?) : WebSocketClient(serverURI) {

    var events: GameSocket.Events? = null

    override fun onOpen(handshakedata: ServerHandshake) {
        events?.onOpen()
    }

    override fun onClose(
        code: Int,
        reason: String,
        remote: Boolean
    ) {
        events?.onClose()
    }

    override fun onMessage(message: String) {
        events?.onMessage(message)
    }

    override fun onMessage(message: ByteBuffer?) {

    }

    override fun onError(ex: Exception) {
        events?.onError(ex)
    }

}