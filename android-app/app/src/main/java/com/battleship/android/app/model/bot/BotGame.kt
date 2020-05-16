package com.battleship.android.app.model.bot

import com.battleship.android.app.model.DamageReport
import com.battleship.android.app.model.Game
import com.battleship.android.app.model.setDamageOnGrid
import com.battleship.core.CellState
import com.battleship.core.Grid
import com.battleship.core.Position
import java.util.*
import kotlin.collections.ArrayList


class BotGame(private val oceanGrid: Grid, private val targetGrid: Grid):
    Game {

    private var gameEvents: Game.GameEvents? = null

    override fun initializeGame() {
        oceanGrid.placeShipsRandomly()
        targetGrid.placeShipsRandomly()
        oceanGrid.setGridEventListener(this)
        targetGrid.setGridEventListener(object : Grid.GridEventListener{
            override fun onGameLost() {
                gameEvents?.onGameWon()
            }
        })
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
        gameEvents?.onReceivingDamageReport(
            setDamageOnGrid(
                targetGrid,
                positions
            )
        )
        android.os.Handler().postDelayed({botFire()}, 2000)
    }

    override fun sendDamageReport(damageReport: DamageReport) {
        //TODO use this data to make bot play intelligently
    }

    override fun sendGameLost() {
        //Not required in bot game
    }

    /**This is a stupid bot implemented to test, need to provide it "Intelligence" */
    private fun botFire(){
        var x = 0
        var y = 0
        while (oceanGrid.getCell(x,y)?.getCellState() != CellState.None){
            x = oceanGrid.getCells().indices.random()
            y = oceanGrid.getCells().indices.random()
        }
        val positions = arrayListOf(Position(x, y))
        gameEvents?.onBeingFired(
            setDamageOnGrid(
                oceanGrid,
                positions
            )
        )
    }

    override fun setGameEvents(events: Game.GameEvents) {
        gameEvents = events
    }

    override fun onGameLost() {
        gameEvents?.onGameLost()
    }

}