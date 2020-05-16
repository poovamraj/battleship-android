package com.battleship.android.app.model

import com.battleship.core.BaseGrid
import com.battleship.core.Position
import java.util.*
import kotlin.collections.ArrayList


class BotGame(private val oceanGrid: BaseGrid, private val targetGrid: BaseGrid):
    Game {

    private var gameEvents: Game.GameEvents? = null

    override fun initializeGame() {
        gameEvents?.onGameInitialized()
        gameEvents?.onReadyToPositionShips()
    }

    override fun getOceanGrid(): BaseGrid {
        return oceanGrid
    }

    override fun getTargetGrid(): BaseGrid {
        return targetGrid
    }

    override fun readyToPlay() {
        val playerTakesTurn =  Random().nextBoolean()
        gameEvents?.onGameStarted(playerTakesTurn)
        if(!playerTakesTurn){
            botFire()
        }
    }

    override fun fire(positions: ArrayList<Position>) {

    }

    override fun sendDamageReport() {
        //TODO use this data to make bot play intelligently
    }

    override fun sendGameLost() {
        //Not required in bot game
    }

    private fun botFire(){

    }

    override fun setGameEvents(events: Game.GameEvents) {
        gameEvents = events
    }

}