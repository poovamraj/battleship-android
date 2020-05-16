package com.battleship.server.game.network

import com.battleship.server.game.controller.GameController
import com.battleship.server.websocket.GameConnection
import com.battleship.server.websocket.GameServer


class BattleShipServer(private val server: GameServer, private val controller: GameController): GameServer.ServerEvents{

    fun start(){
        server.setEvents(this)
        server.start()
    }

    override fun onServerStarted() {
        println("Server started on ${server.getHostname()}:${server.getPort()}")
    }

    override fun onNewConnection(conn: GameConnection) {
        conn.setEvents(object : GameConnection.Events {
            override fun onClose() {
                controller.onDisconnect(conn)
            }

            override fun onMessage(message: String?) {
                message?.let { process(message, conn, controller) }
            }

            override fun onError(ex: Exception?) {

            }
        })
    }
}


/**
 * Conn Success
 * Create Room (or) Join Room
 * create room with number of ships and grid size -> get room id
 * join room, send room id
 * if(join room) p1 will wait for other
 * when 2 joined -> position_ships
 * get ready from both
 * once both ready -> start_game
 * pass move from p1 to p2
 * response from p2 to p1 with (state, [cells(x,y);N])
 * keep doing this
 * both sides will count number of ships sunk
 * if all ships sunk, send self lost and other won
 * send win message to other player
 **/