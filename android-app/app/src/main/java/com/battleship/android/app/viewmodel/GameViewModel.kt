package com.battleship.android.app.viewmodel

import androidx.lifecycle.ViewModel
import com.battleship.android.app.model.Game

class GameViewModel(private val game: Game) : ViewModel(), Game.GameEvents {
    override fun onGameInitialized() {
        TODO("Not yet implemented")
    }

    override fun onReadyToPositionShips() {
        TODO("Not yet implemented")
    }

    override fun onShipsPositioned() {
        TODO("Not yet implemented")
    }

    override fun onGameStarted(takeTurn: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onBeingFired() {
        TODO("Not yet implemented")
    }

    override fun onReceivingDamageReport() {
        TODO("Not yet implemented")
    }

    override fun onGameWon() {
        TODO("Not yet implemented")
    }

    override fun onGameLost() {
        TODO("Not yet implemented")
    }

    override fun onGameInterrupted(message: String) {
        TODO("Not yet implemented")
    }

}