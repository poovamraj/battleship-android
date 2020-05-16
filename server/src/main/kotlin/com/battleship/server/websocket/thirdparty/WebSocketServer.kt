package com.battleship.server.websocket.thirdparty

import com.battleship.server.websocket.GameConnection
import com.battleship.server.websocket.GameServer
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class BattleShipServer(private val hostname: String,private val port: Int): GameServer {

    private val libSocket = WebSocket(InetSocketAddress(hostname, port))

    override fun start() {
        libSocket.start()
    }

    override fun stop() {
        libSocket.stop()
    }

    override fun getEvents(): GameServer.ServerEvents? {
        return libSocket.events
    }

    override fun getHostname(): String {
        return hostname
    }

    override fun getPort(): Int {
        return port
    }

    override fun setEvents(events: GameServer.ServerEvents) {
        libSocket.events = events
    }
}

private class WebSocket(address: InetSocketAddress) : WebSocketServer(address) {

    var events: GameServer.ServerEvents? = null

    private val sockets = HashMap<org.java_websocket.WebSocket, GameConnection>()

    override fun onOpen(conn: org.java_websocket.WebSocket?, handshake: ClientHandshake?) {
        val socket = WebSocketConnection(conn!!)
        sockets[conn] = socket
        events?.onNewConnection(socket)
    }

    override fun onClose(conn: org.java_websocket.WebSocket?, code: Int, reason: String?, remote: Boolean) {
        val socket = sockets[conn]
        socket?.getEvents()?.onClose()
    }

    override fun onMessage(conn: org.java_websocket.WebSocket?, message: String?) {
        sockets[conn]?.getEvents()?.onMessage(message)
    }

    override fun onStart() {
        events?.onServerStarted()
    }

    override fun onError(conn: org.java_websocket.WebSocket?, ex: Exception?) {
        println(ex)
        sockets[conn]?.getEvents()?.onError(ex)
    }

}