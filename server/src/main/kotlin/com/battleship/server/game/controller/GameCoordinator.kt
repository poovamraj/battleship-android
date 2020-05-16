package com.battleship.server.game.controller

import com.battleship.server.game.network.ROOM_FULL
import com.battleship.server.game.network.ROOM_NOT_FOUND
import com.battleship.server.websocket.GameConnection
import com.battleship.server.game.network.Error

class GameCoordinator(private val lobby: Lobby) {

    fun createNewRoom(gridSize: Int, noOfSteps: Int): Room {
        return lobby.createNewRoom(gridSize, noOfSteps)!!
    }

    fun createPlayer(gameConnection: GameConnection): Player {
        return Player(gameConnection)
    }

    fun joinRoom(id: Long, gameConnection: GameConnection): Error? {
        val room = lobby.rooms.find { it.id == id } ?: return ROOM_NOT_FOUND
        if (!room.isFull()) {
            room.addPlayer(createPlayer(gameConnection))
            return null
        }
        return ROOM_FULL
    }

    fun findRoom(connection: GameConnection): Room? {
        return lobby.rooms.find { it.players.find { player -> player.gameConnection == connection } != null }
    }


    fun findPlayer(room: Room, connection: GameConnection): Player? {
        return room.players.find { it.gameConnection == connection }
    }

    fun findPlayer(connection: GameConnection): Player? {
        return findPlayer(findRoom(connection)!!, connection)
    }

    fun findRoom(id: Long): Room? {
        return lobby.rooms.find { it.id == id }
    }

    fun canStartGame(id: Long): Boolean {
        val room = lobby.rooms.find { it.id == id }
        return room?.isFull() ?: false
    }
}