package com.battleship.android.app.model

import com.battleship.core.Grid
import com.battleship.core.Position
import java.util.*
import kotlin.collections.ArrayList


class BotGame(private val oceanGrid: Grid, private val targetGrid: Grid):
    Game {

    private var gameEvents: Game.GameEvents? = null

    override fun initializeGame() {
        gameEvents?.onGameInitialized()
        gameEvents?.onReadyToPositionShips()
    }

    override fun getOceanGrid(): Grid {
        return oceanGrid
    }

    override fun getTargetGrid(): Grid {
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