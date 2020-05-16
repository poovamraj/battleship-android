package com.battleship.android.app.model.online

import com.battleship.android.app.model.DamageReport
import com.battleship.core.Position
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

/***
 * Base data structure and common data structures
 */
val gson = Gson()

data class Protocol<E>(val type: String, val message: E)

data class Error(val code: Int, val message: String)

fun getTypeFromMessage(message: String): String?{
    val element = JSONObject(message)
    return element.optString("type", "")
}

/**
 * Room creation related protocol handling are done in this class
 */
const val ROOM_CREATION_SUCCESS = "room_creation_success"

const val CREATE_ROOM = "create_room"

data class RoomMessage(val id: Long)

data class RoomCreationInput(val gridSize: Int, val noOfSteps: Int)

fun constructCreateRoomProtocol(gridSize: Int, noOfSteps: Int): String {
    return gson.toJson(
        Protocol(
            CREATE_ROOM,
            RoomCreationInput(
                gridSize,
                noOfSteps
            )
        )
    )
}

fun parseRoomCreationSuccess(message: String): RoomMessage {
    val collectionType = object : TypeToken<Protocol<RoomMessage>>() {}.type
    val input = gson.fromJson<Protocol<RoomMessage>>(message, collectionType)
    return input.message
}

/**
 * Room join related protocol handling
 */

const val JOIN_ROOM = "join_room"

const val JOIN_ROOM_FAILED = "join_room_failed"

fun constructJoinRoomProtocol(roomId: Long): String{
    return gson.toJson(Protocol(JOIN_ROOM, RoomMessage(roomId)))
}

fun parseJoinRoomFailed(message: String): Error {
    val collectionType = object : TypeToken<Protocol<Error>>() {}.type
    val input = gson.fromJson<Protocol<Error>>(message, collectionType)
    return input.message
}

/**
 * Alerting user to position their ships
 */
data class GameData(val gridSize: Int, val noOfSteps: Int)

const val POSITION_SHIPS = "position_ships"

const val SHIPS_POSITIONED = "ships_positioned"

fun parsePositionShips(message: String): GameData {
    val collectionType = object : TypeToken<Protocol<GameData>>() {}.type
    val input = gson.fromJson<Protocol<GameData>>(message, collectionType)
    return input.message
}

fun constructShipsPositionedProtocol(): String{
    return gson.toJson(
        Protocol(
            SHIPS_POSITIONED,
            ""
        )
    )
}

/**
 * Once all players have positioned ships, start game is called
 */
const val START_GAME = "start_game"

data class StartGameDetails(val takeTurn: Boolean)

fun parseStartGame(message: String): StartGameDetails {
    val collectionType = object : TypeToken<Protocol<StartGameDetails>>() {}.type
    val input = gson.fromJson<Protocol<StartGameDetails>>(message, collectionType)
    return input.message
}

/**
 * Game taking turns and response of turns taken
 */
const val HIT = "hit"

const val HIT_RESPONSE = "hit_response"

fun constructFireProtocol(positions: ArrayList<Position>): String{
    return gson.toJson(
        Protocol(
            HIT,
            positions
        )
    )
}

fun parseHitMessage(message: String): ArrayList<Position>{
    val collectionType = object : TypeToken<Protocol<ArrayList<Position>>>() {}.type
    val input = gson.fromJson<Protocol<ArrayList<Position>>>(message, collectionType)
    return input.message
}

fun constructDamageReportProtocol(response: DamageReport): String{
    return gson.toJson(
        Protocol(
            HIT_RESPONSE,
            response
        )
    )
}

fun parseHitResponse(message: String): DamageReport {
    val collectionType = object : TypeToken<Protocol<DamageReport>>() {}.type
    val input = gson.fromJson<Protocol<DamageReport>>(message, collectionType)
    return input.message
}


/**
 * The end result of the game
 */
const val GAME_LOST = "lost"

const val GAME_WON = "game_won"

fun constructGameLostProtocol(): String {
    return gson.toJson(
        Protocol(
            GAME_LOST,
            ""
        )
    )
}

/**
 * When opponent disconnects
 */
const val OPPONENT_DISCONNECT = "opponent_disconnect"