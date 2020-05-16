package com.battleship.core

class BaseGrid (private val gridSize: Int, private val ships: Array<Ship>) {

    private val cells: Array<Array<Cell>> = Array(gridSize) { Array(gridSize) { Cell() } }

    private val shipsPlaced = arrayOfNulls<Ship>(ships.size)

    private val shipsLost = arrayListOf<Ship>()

    fun getCells(): Array<Array<Cell>> {
        return cells
    }

    private fun getCell(x: Int, y: Int): Cell? {
        if(x > gridSize || y > gridSize) return null
        return cells[x][y]
    }

    fun getShips(): Array<Ship> {
        return ships
    }

    private fun isGameLost(): Boolean{
        return ships.size == shipsLost.size
    }

    private fun isAllShipsPlaced(): Boolean{
        return ships.size == shipsPlaced.filterNotNull().size
    }

    private fun getPosition(cell: Cell): Position?{
        for(i in cells.indices){
            for(j in cells[i].indices){
                if(cells[i][j] == cell) return Position(i, j)
            }
        }
        return null
    }

    fun setCellState(x: Int, y: Int, state: CellState) {
        val cell = getCell(x, y)?: return
        when(state){
            CellState.Hit -> cell.hit()
            CellState.Miss -> cell.miss()
            is CellState.Sunk -> cell.sunk()
            CellState.None -> cell.none()
        }
    }
}