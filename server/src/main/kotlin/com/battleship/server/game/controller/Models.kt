package com.battleship.server.game.controller

import com.battleship.server.game.common.GameIdGenerator
import com.battleship.server.websocket.GameConnection

class Lobby(val rooms: ArrayList<Room>, private val idGenerator: GameIdGenerator){

    fun createNewRoom(gridSize: Int, noOfSteps: Int): Room? {
        for (i in 0.. 1000){
            val id = idGenerator.getId()
            if(rooms.none { it.id == id }) {
                val room =
                    Room(gridSize, noOfSteps, ArrayList(), id)
                rooms.add(room)
                return room
            }
        }
        return null //not lucky enough to play, tried to find a random number 1000 times, need to improve this logic
    }

}

class Room(
    val gridSize: Int,
    val noOfSteps: Int,
    val players: ArrayList<Player>,
    val id: Long
){
    fun addPlayer(player: Player){
        if(!isFull()){
            players.add(player)
        }
    }

    fun broadcastToAllPlayers(message: String){
        players.forEach { it.sendMessage(message) }
    }

    fun isFull(): Boolean{
        return players.size >= 2
    }
}

class Player(val gameConnection: GameConnection){
    private var readyToStartGame = false

    fun readyToStartGame(){
        readyToStartGame = true
    }

    fun isReadyToStartGame(): Boolean{
        return readyToStartGame
    }

    fun sendMessage(string: String){
        gameConnection.send(string)
    }
}