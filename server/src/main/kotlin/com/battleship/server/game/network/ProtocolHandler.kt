package com.battleship.server.game.network

import com.battleship.server.game.controller.GameController
import com.battleship.server.websocket.GameConnection
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject


val gson = Gson()

data class Protocol<E>(val type: String, val message: E)


/** Room creation handling */

const val CREATE_ROOM = "create_room"

const val ROOM_CREATION_SUCCESS = "room_creation_success"

data class RoomMessage(val id: Long)

data class RoomCreationInput(val gridSize: Int, val noOfSteps: Int)

fun constructRoomCreationSuccessProtocol(id: Long): String {
    return gson.toJson(
        Protocol(
            ROOM_CREATION_SUCCESS,
            RoomMessage(id)
        )
    )
}

/** Join room handling */

const val JOIN_ROOM = "join_room"

const val JOIN_ROOM_FAILED = "join_room_failed"

val ROOM_FULL = Error(1001, "Room full")

val ROOM_NOT_FOUND = Error(404, "Room not found")

fun constructJoinRoomFailed(error: Error): String{
    return gson.toJson(
        Protocol(
            JOIN_ROOM_FAILED,
            error
        )
    )
}

/** Ship positioning handling */

const val POSITION_SHIPS = "position_ships"

const val SHIPS_POSITIONED = "ships_positioned"

data class GameData(val gridSize: Int, val noOfSteps: Int)

fun constructPositionShipsProtocol(gameData: GameData): String {
    return gson.toJson(
        Protocol(
            POSITION_SHIPS,
            gameData
        )
    )
}

/** Start game protocol handling */

const val START_GAME = "start_game"

data class StartGame(val takeTurn: Boolean)

fun constructStartGameProtocol(startGame: StartGame): String{
    return gson.toJson(
        Protocol(
            START_GAME,
            startGame
        )
    )
}

/** Firing and receiving fire protocol */

const val HIT = "hit"

const val HIT_RESPONSE = "hit_response"

data class Position(val x: Int, val y: Int)

data class DamageReport(val hit: ArrayList<Position>, val miss: ArrayList<Position>, val sunk:ArrayList<ArrayList<Position>>)

fun constructFireProtocol(positions: Protocol<ArrayList<Position>>): String{
    return gson.toJson(positions)
}

fun constructDamageReportProtocol(response: Protocol<DamageReport>): String{
    return gson.toJson(response)
}

/** Game ending protocols are handled here*/

const val GAME_WON = "game_won"

const val LOST = "lost"

const val OPPONENT_DISCONNECT = "opponent_disconnect"

fun constructGameWonProtocol(): String{
    return gson.toJson(
        Protocol(
            GAME_WON,
            ""
        )
    )
}

fun constructOpponentDisconnectedProtocol(): String{
    return gson.toJson(
        Protocol(
            OPPONENT_DISCONNECT,
            ""
        )
    )
}

data class Error(val code: Int, val message: String)

fun process(message: String, conn: GameConnection, controller: GameController){

    println(message)
    val element = JSONObject(message)
    when(element.optString("type", "")){
        CREATE_ROOM -> {
            val collectionType = object : TypeToken<Protocol<RoomCreationInput>>() {}.type
            val input = Gson().fromJson<Protocol<RoomCreationInput>>(message, collectionType)
            controller.onCreateRoom(conn, input.message.gridSize, input.message.noOfSteps)
        }

        JOIN_ROOM -> {
            val collectionType = object : TypeToken<Protocol<RoomMessage>>() {}.type
            val input = Gson().fromJson<Protocol<RoomMessage>>(message, collectionType)
            controller.onJoinRoom(conn, input.message.id)
        }

        SHIPS_POSITIONED -> {
            controller.onShipsPositioned(conn)
        }

        HIT -> {
            val collectionType = object : TypeToken<Protocol<ArrayList<Position>>>() {}.type
            val input = Gson().fromJson<Protocol<ArrayList<Position>>>(message, collectionType)
            controller.onHit(conn, input.message)
        }

        HIT_RESPONSE -> {
            val collectionType = object : TypeToken<Protocol<DamageReport>>() {}.type
            val input = Gson().fromJson<Protocol<DamageReport>>(message, collectionType)
            controller.onHitResponse(conn, input.message)
        }

        LOST -> {
            controller.onLost(conn)
        }

        else->{
            println(message)
        }
    }
}
