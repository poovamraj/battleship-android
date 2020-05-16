package com.battleship.android.app.model

import com.battleship.core.Ship

class GameConfig {
    companion object {

        const val GRID_SIZE = 10

        const val NO_OF_STEPS = 1

        fun createShips(): Array<Ship> {
            return arrayOf(
                Ship.makeAirCarrier(),
                Ship.makeDestroyer(),
                Ship.makeDestroyer(),
                Ship.makeCruiser(),
                Ship.makeCruiser(),
                Ship.makeCruiser(),
                Ship.makeMine(),
                Ship.makeMine(),
                Ship.makeMine(),
                Ship.makeMine(),
            )
        }

        const val RESPONSE_ANIMATION_DELAY = 2000L
    }
}