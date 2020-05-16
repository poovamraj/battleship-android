package com.battleship.core

internal class BaseGrid (private val gridSize: Int, private val ships: Array<Ship>): Grid{

    private val cells: Array<Array<Cell>> = Array(gridSize){ Array(gridSize) { Cell() } }

    private val shipsPlaced = arrayOfNulls<Ship>(ships.size)

    private val shipsLost = arrayListOf<Ship>()

    private var gridEventListener: Grid.GridEventListener? = null

    private fun isAllShipsPlaced(): Boolean{
        return ships.size == shipsPlaced.filterNotNull().size
    }

    override fun getCell(x: Int, y: Int): Cell? {
        if(x > gridSize || y > gridSize) return null
        return cells[x][y]
    }

    private fun isGameLost(): Boolean{
        return ships.size == shipsLost.size
    }

    private fun getPosition(cell: Cell): Position?{
        for(i in cells.indices){
            for(j in cells[i].indices){
                if(cells[i][j] == cell) return Position(i, j)
            }
        }
        return null
    }

    override fun setCellState(x: Int, y: Int, state: CellState) {
        val cell = getCell(x, y)?: return
        when(state){
            CellState.Hit -> cell.hit()
            CellState.Miss -> cell.miss()
            is CellState.Sunk -> cell.sunk(state.positions)
            CellState.None -> cell.none()
        }
    }

    override fun setGridEventListener(eventListener: Grid.GridEventListener) {
        this.gridEventListener = eventListener
    }

    override fun getCells(): Array<Array<Cell>> {
        return cells
    }

    override fun getShips(): Array<Ship> {
        return ships
    }

    override fun getShipAtPosition(x: Int, y: Int): Ship? {
        val cell = getCell(x, y) ?: return null
        return ships.find {
            it.containsPart(cell)
        }
    }

    override fun placeShip(ship: Ship, x: Int, y: Int): ShipPlacingResult {
        if(!ships.contains(ship)){
            throw IllegalArgumentException("ship is not part of the grid")
        }
        val (result, cells) = canPlaceShip(ship, x, y)
        if(result == ShipPlacingResult.Placed){
            if(shipsPlaced.contains(ship)){
                shipsPlaced[shipsPlaced.indexOf(ship)] = null
                ship.removeAllParts()
            }
            ship.setShipParts(cells!!)
            for(i in shipsPlaced.indices){
                if(shipsPlaced[i] == null){
                    shipsPlaced[i] = ship
                    break
                }
            }
        }
        return result
    }

    override fun rotateShip(ship: Ship): Boolean {
        val (x, y) = ship.getShipParts()[0]?.let { getPosition(it) } ?: return false
        val initialDirection = ship.directionFacing
        val directions = DirectionFacing.values()
        for (i in directions.indices){
            val currentIndex = directions.indexOf(ship.directionFacing)
            val temp = if(currentIndex == directions.size - 1) 0 else directions.indexOf(ship.directionFacing) + 1
            ship.directionFacing = directions[temp]
            val result = placeShip(ship, x, y)
            if(result == ShipPlacingResult.Placed){
                break
            }
        }
        return initialDirection != ship.directionFacing
    }

    override fun placeShipsRandomly(){
        for(i in shipsPlaced.indices){
            shipsPlaced.forEach { it?.removeAllParts() }
            shipsPlaced[i] = null
        }

        while (!isAllShipsPlaced()){
            ships.filter { !shipsPlaced.contains(it) }
                .forEach{
                    val x = (0 until gridSize).random()
                    val y = (0 until gridSize).random()
                    it.let {
                        it.directionFacing =  DirectionFacing.values().random()
                        placeShip(it, x, y)
                    }
                }
        }
    }

    private fun canPlaceShip(ship: Ship, x: Int, y: Int): Pair<ShipPlacingResult, Array<Cell?>?>{
        val requiredCells = arrayOfNulls<Cell>(ship.size)
        if(!ship.isShipMovable()){
            return Pair(ShipPlacingResult.Immovable, null)
        }

        var counter = 0;
        while (counter < ship.size){
            try{
                val cell: Cell = when(ship.directionFacing){
                    DirectionFacing.North -> getCell(x + counter, y)
                    DirectionFacing.South -> getCell(x - counter, y)
                    DirectionFacing.West -> getCell(x, y + counter)
                    DirectionFacing.East -> getCell(x, y - counter)
                } ?: return Pair(ShipPlacingResult.OutOfBoundary, null)

                if(shipsPlaced.filter { it != ship }.none { it?.containsPart(cell) == true } &&
                    cell.getCellState() == CellState.None)
                {
                    requiredCells[counter] = cell
                    counter++
                } else {
                    return Pair(ShipPlacingResult.InUse, null)
                }

            } catch (e: ArrayIndexOutOfBoundsException){
                return Pair(ShipPlacingResult.OutOfBoundary, null)
            }
        }

        return Pair(ShipPlacingResult.Placed, requiredCells)
    }

    override fun takeFire(x: Int, y: Int): Result<CellState, String> {
        val cell = getCell(x,y) ?: return Result.Err("Out of boundary")

        if(cell.getCellState() != CellState.None){
            return Result.Err("Already in use")
        }

        val ship = getShipAtPosition(x, y)
        return if(ship != null){
            val hit = ship.takeFire(cell)
            if(ship.isSunk()){
                val positions = ArrayList(ship.getShipParts().map { cell -> getPosition(cell!!)!! })
                positions.forEach { (x,y) -> setCellState(x, y, CellState.Sunk(positions)) }
                shipsLost.add(ship)
                if(isGameLost()){
                    gridEventListener?.onGameLost()
                }
                Result.Success(CellState.Sunk(positions))
            } else {
                Result.Success(hit)
            }
        } else {
            cell.miss()
            Result.Success(CellState.Miss)
        }
    }
}

enum class ShipPlacingResult {
    OutOfBoundary, InUse, Placed, Immovable
}

//TODO we can implement lock/unlock grid to control changing ship's position
interface Grid {
    fun getCells(): Array<Array<Cell>>

    fun getCell(x: Int, y: Int): Cell?

    fun getShips(): Array<Ship>

    fun getShipAtPosition(x: Int, y: Int): Ship?

    fun placeShipsRandomly()

    fun placeShip(ship: Ship, x: Int, y: Int): ShipPlacingResult

    fun rotateShip(ship: Ship): Boolean

    fun takeFire(x: Int, y: Int): Result<CellState, String>

    fun setCellState(x: Int, y: Int, state: CellState)

    fun setGridEventListener(eventListener: GridEventListener)

    interface GridEventListener {
        fun onGameLost()
    }
}

sealed class Result<T, U> {
    data class Success<T, U>(val value: T): Result<T, U>()
    data class Err<T, U>(val value: U): Result<T, U>()
}

class GridBuilder {
    companion object {
        fun createGrid(gridSize: Int, ships: Array<Ship>): Grid{
            return BaseGrid(gridSize, ships)
        }
    }
}