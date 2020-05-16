package com.battleship.server.game

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
)

class Player(val gameConnection: GameConnection)