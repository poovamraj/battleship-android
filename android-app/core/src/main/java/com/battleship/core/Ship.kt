package com.battleship.core

class Ship (val size: Int) {

    var directionFacing = DirectionFacing.East

    private var parts = arrayOfNulls<Cell>(size)
}

enum class DirectionFacing {
    North, East, South, West
}