package com.battleship.android.app.game.model

import com.battleship.core.Grid
import com.battleship.core.Position

interface Game : Grid.GridEventListener {

    fun initializeGame()

    fun getOceanGrid(): Grid

    fun getTargetGrid(): Grid

    fun readyToPlay()

    fun fire(positions: ArrayList<Position>)

    fun sendDamageReport(damageReport: DamageReport)

    fun sendGameLost()

    fun setGameEvents(events: GameEvents)

    interface GameEvents {
        fun onGameInitialized()

        fun onReadyToPositionShips()

        fun onShipsPositioned()

        fun onGameStarted(takeTurn: Boolean)

        fun onBeingFired(damageReport: DamageReport)

        fun onReceivingDamageReport(damageReport: DamageReport)

        fun onGameWon()

        fun onGameLost()

        fun onGameInterrupted(message: String)//TODO in future we can make into an error object and handle different errors
    }
}

/**
 * On Ready To position ships
 * 2 Grids are created > 1 Ocean Grid, 1 Target Grid
 * Position Ships on the Ocean Grid
 * tell ships are positioned
 * Fire on target grid > Get response > set whether hit/miss/sunk on target grid
 * Get fired by opponent > set on ocean grid > send response to opponent
 * If all ships are sunk send onlost
 */
