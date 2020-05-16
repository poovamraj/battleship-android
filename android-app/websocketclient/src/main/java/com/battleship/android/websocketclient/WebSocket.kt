package com.battleship.android.websocketclient

import java.net.URI

class WebSocket {
    companion object {
        fun createClient(hostname: String, port: Int): GameSocket {
            return SocketImpl(JavaNetWebSocketClient(URI("ws://$hostname:$port")))
        }
    }
}