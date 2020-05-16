package com.battleship.core

class Ship (val size: Int) {

    var directionFacing = DirectionFacing.East
        internal set

    internal fun isShipMovable(): Boolean{
        return parts.none { it?.getCellState() == CellState.Hit && it.getCellState() is CellState.Sunk}
    }

    /*
     Ship has the dependency to cells because if cells have dependency to ship,
     we need to ensure to change cell state when ship is moving
     the right dependency is for ship to have cells in which it is positioned
    */
    private var parts = arrayOfNulls<Cell>(size)

    internal fun setShipParts(cells: Array<Cell?>){
        this.parts = cells
    }

    internal fun getShipParts(): Array<Cell?>{
        return parts
    }

    internal fun removeAllParts(){
        parts = arrayOfNulls(size)
    }

    internal fun isSunk(): Boolean {
        return parts.all { it?.getCellState() == CellState.Hit }
    }

    internal fun containsPart(cell: Cell): Boolean{
        return parts.contains(cell)
    }

    fun partPosition(cell: Cell): Int?{
        if(!containsPart(cell)) return null
        return parts.indexOf(cell)
    }

    internal fun takeFire(cell: Cell): CellState{
        if(parts.contains(cell)){
            cell.hit()
            return CellState.Hit
        }
        return CellState.None
    }

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