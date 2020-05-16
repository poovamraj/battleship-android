package com.battleship.core

class Cell {

    private var cellState: CellState = CellState.None

    internal fun hit(){ //using methods instead of providing direct access, provides abstraction
        setState(CellState.Hit)
    }

    internal fun miss(){
        setState(CellState.Miss)
    }

    internal fun sunk(positions: ArrayList<Position>){
        setState(CellState.Sunk(positions))
    }

    internal fun none(){
        setState(CellState.None)
    }

    private fun setState(to: CellState){
        cellState = to
    }

    fun getCellState(): CellState {
        return cellState
    }
}

data class Position(val x: Int, val y: Int)

sealed class CellState{
    object Hit : CellState()
    object Miss: CellState()
    data class Sunk(val positions: ArrayList<Position>): CellState()
    object None: CellState()
}