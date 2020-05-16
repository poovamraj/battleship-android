package com.battleship.core

class Ship (val size: Int) {

    var directionFacing = DirectionFacing.East

    private var parts = arrayOfNulls<Cell>(size)

    companion object {
        fun makeAirCarrier(): Ship{
            return Ship(4)
        }

        fun makeDestroyer(): Ship{
            return Ship(3)
        }

        fun makeCruiser(): Ship{
            return Ship(2)
        }

        fun makeMine(): Ship{
            return Ship(1)
        }

    }
}

enum class DirectionFacing {
    North, East, South, West
}