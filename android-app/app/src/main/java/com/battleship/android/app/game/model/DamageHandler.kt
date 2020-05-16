package com.battleship.android.app.game.model

import com.battleship.core.CellState
import com.battleship.core.Grid
import com.battleship.core.Position
import com.battleship.core.Result

data class DamageReport(val hit: ArrayList<Position>, val miss: ArrayList<Position>, val sunk:ArrayList<ArrayList<Position>>)

fun setDamageOnGrid(grid: Grid, positions: ArrayList<Position>): DamageReport {
    val damageReport = DamageReport(
        arrayListOf(),
        arrayListOf(),
        arrayListOf()
    )
    positions.forEach { position ->
        val (x, y) = position

        when(val result = grid.takeFire(x, y)){
            is Result.Success -> {
                when(val cellState = result.value){
                    is CellState.Sunk -> { damageReport.sunk.add(ArrayList(cellState.positions.map { (x, y) -> Position(x, y) })) }

                    CellState.Hit -> { damageReport.hit.add(Position(x, y))}

                    CellState.Miss -> { damageReport.miss.add(Position(x, y))}
                }
            }
            is Result.Err -> {}
        }}
    return damageReport
}

fun setDamageOnGrid(grid: Grid, damageReport: DamageReport){
    damageReport.hit.forEach { (x, y) ->
        grid.setCellState(x, y, CellState.Hit)
    }
    damageReport.miss.forEach { (x, y) ->
        grid.setCellState(x, y, CellState.Miss)
    }
    damageReport.sunk.forEach {
        it.forEach {(x, y) -> grid.setCellState(x, y, CellState.Sunk(it))}
    }
}