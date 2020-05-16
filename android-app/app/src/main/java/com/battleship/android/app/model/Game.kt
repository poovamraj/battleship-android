package com.battleship.android.app.model

import com.battleship.core.BaseGrid
import com.battleship.core.Position

interface Game {

    fun initializeGame()

    fun getOceanGrid(): BaseGrid

    fun getTargetGrid(): BaseGrid

    fun readyToPlay()

    fun fire(positions: ArrayList<Position>)

    fun sendDamageReport()

    fun sendGameLost()

    fun setGameEvents(events: GameEvents)

    interface GameEvents {
        fun onGameInitialized()

        fun onReadyToPositionShips()

        fun onShipsPositioned()

        fun onGameStarted(takeTurn: Boolean)

        fun onBeingFired()

        fun onReceivingDamageReport()

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
