package com.battleship.server.websocket

interface GameServer {
    fun start()

    fun stop()

    fun setEvents(events: ServerEvents)

    fun getEvents(): ServerEvents?

    fun getHostname(): String

    fun getPort(): Int

    interface ServerEvents {
        fun onServerStarted()

        fun onNewConnection(conn: GameConnection)
    }
}