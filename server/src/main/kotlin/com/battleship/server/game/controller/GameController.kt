package com.battleship.server.game.controller

import com.battleship.server.game.network.*
import com.battleship.server.websocket.GameConnection

class GameController(private val coordinator: GameCoordinator) {
    fun onCreateRoom(gameConnection: GameConnection, gridSize: Int, noOfSteps: Int){
        val room = coordinator.createNewRoom(gridSize, noOfSteps)
        val player = coordinator.createPlayer(gameConnection)
        room.addPlayer(player)
        player.sendMessage(constructRoomCreationSuccessProtocol(room.id))
    }

    fun onJoinRoom(gameConnection: GameConnection, roomId: Long){
        val error = coordinator.joinRoom(roomId, gameConnection)
        if(error == null){
            if(coordinator.canStartGame(roomId)){
                val room = coordinator.findRoom(roomId)
                if(room != null){
                    val gameData = GameData(room.gridSize, room.noOfSteps)
                    room.broadcastToAllPlayers(constructPositionShipsProtocol(gameData))
                }
            }
        } else {
            gameConnection.send(constructJoinRoomFailed(error))
        }
    }

    fun onShipsPositioned(gameConnection: GameConnection){
        val room = coordinator.findRoom(gameConnection)
        coordinator.findPlayer(room!!, gameConnection)?.readyToStartGame()
        if(room.players.none { !it.isReadyToStartGame() }){
            val turnOnePlayer = room.players.random()
            turnOnePlayer.sendMessage(constructStartGameProtocol(StartGame(true)))
            room.players.filter { it != turnOnePlayer }.forEach{ it.sendMessage(constructStartGameProtocol(StartGame(false)))}
        }
    }

    fun onHit(gameConnection: GameConnection, hitPostions: ArrayList<Position>){
        val room = coordinator.findRoom(gameConnection)
        val player = coordinator.findPlayer(gameConnection)
        room?.players?.filter { it != player }?.forEach{ it.sendMessage(constructFireProtocol(Protocol(HIT,hitPostions)))}
    }

    fun onHitResponse(gameConnection: GameConnection, damageReport: DamageReport){
        val room = coordinator.findRoom(gameConnection)
        val player = coordinator.findPlayer(gameConnection)
        room?.players?.filter { it != player }?.forEach{ it.sendMessage(constructDamageReportProtocol(Protocol(HIT_RESPONSE,damageReport)))}
    }

    fun onLost(gameConnection: GameConnection){
        val room = coordinator.findRoom(gameConnection)
        val player = coordinator.findPlayer(gameConnection)
        room?.players?.filter { it != player }?.forEach{ it.sendMessage(constructGameWonProtocol())}
    }

    fun onDisconnect(gameConnection: GameConnection){
        val room = coordinator.findRoom(gameConnection) ?: return
        val player = coordinator.findPlayer(room, gameConnection)
        room.players.filter { it != player }.forEach { it.sendMessage(constructOpponentDisconnectedProtocol()) }
    }

}