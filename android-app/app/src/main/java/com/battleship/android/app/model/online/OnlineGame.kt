package com.battleship.android.app.model.online

import com.battleship.android.app.common.runOnMainThread
import com.battleship.android.app.model.DamageReport
import com.battleship.android.app.model.Game
import com.battleship.android.app.model.setDamageOnGrid
import com.battleship.android.websocketclient.GameSocket
import com.battleship.core.Grid
import com.battleship.core.Position

class OnlineGame(
    private val gameSocket: GameSocket,
    private val oceanGrid: Grid,
    private val targetGrid: Grid,
): Game, GameSocket.Events {

    private var events: Game.GameEvents? = null

    private var onRoomCreatedSuccessfully: ((Long)-> Unit)? = null

    private var onJoinRoomError: ((Error)->Unit)? = null

    override fun initializeGame() {
        gameSocket.setEvents(this)
        oceanGrid.placeShipsRandomly()
        oceanGrid.setGridEventListener(this)
    }


    fun connect(){
        gameSocket.connect()
    }

    fun disconnect(){
        gameSocket.disconnect()
    }

    fun createRoom(gridSize: Int, noOfSteps: Int, onRoomCreatedSuccessfully: (Long)->Unit) {
        this.onRoomCreatedSuccessfully = onRoomCreatedSuccessfully
        gameSocket.sendMessage(
            constructCreateRoomProtocol(
                gridSize,
                noOfSteps
            )
        )
    }

    fun joinRoom(roomId: Long, onJoinRoom: ((Error)->Unit)?){
        this.onJoinRoomError = onJoinRoom
        gameSocket.sendMessage(
            constructJoinRoomProtocol(
                roomId
            )
        )
    }


    override fun fire(positions: ArrayList<Position>) {
        gameSocket.sendMessage(
            constructFireProtocol(
                positions
            )
        )
    }

    override fun sendDamageReport(damageReport: DamageReport) {
        gameSocket.sendMessage(
            constructDamageReportProtocol(
                damageReport
            )
        )
    }

    override fun sendGameLost() {
        gameSocket.sendMessage(constructGameLostProtocol())
    }

    override fun setGameEvents(events: Game.GameEvents) {
        this.events = events
    }

    override fun onGameLost() {
        sendGameLost()
        events?.onGameLost()
    }

    override fun getOceanGrid(): Grid {
        return oceanGrid
    }

    override fun getTargetGrid(): Grid {
        return targetGrid
    }

    override fun readyToPlay() {
        gameSocket.sendMessage(constructShipsPositionedProtocol())
    }


    override fun onOpen() {
        runOnMainThread {
            events?.onGameInitialized()
        }
    }

    override fun onClose() {
        runOnMainThread { } //TODO handle close code
    }

    override fun onMessage(message: String) {
        val type =
            getTypeFromMessage(message)
        runOnMainThread {
            when(type) {

                ROOM_CREATION_SUCCESS -> {
                    val room =
                        parseRoomCreationSuccess(
                            message
                        )
                    onRoomCreatedSuccessfully?.let { it(room.id) }
                }

                JOIN_ROOM_FAILED -> {
                    val reason =
                        parseJoinRoomFailed(
                            message
                        )
                    onJoinRoomError?.let { it(reason) }
                }

                POSITION_SHIPS -> {
                    val gameData =
                        parsePositionShips(
                            message
                        )
                    events?.onReadyToPositionShips()
                }

                START_GAME -> {
                    val startGameDetails =
                        parseStartGame(
                            message
                        )
                    events?.onGameStarted(startGameDetails.takeTurn)
                }

                HIT -> {
                    val hitPositions =
                        parseHitMessage(
                            message
                        )
                    events?.onBeingFired(setDamageOnGrid(getOceanGrid(), hitPositions))
                }

                HIT_RESPONSE -> {
                    val hitResponse =
                        parseHitResponse(
                            message
                        )
                    setDamageOnGrid(targetGrid, hitResponse)
                    events?.onReceivingDamageReport(hitResponse)
                }

                GAME_WON -> {
                    events?.onGameWon()
                }

                OPPONENT_DISCONNECT -> {
                    events?.onGameInterrupted("Opponent disconnected")
                }
            }
        }
    }

    override fun onError(ex: Exception) {
        runOnMainThread { }//TODO implement error handling
    }
}