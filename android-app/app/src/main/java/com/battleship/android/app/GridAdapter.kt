package com.battleship.android.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.battleship.core.Cell
import com.battleship.core.DirectionFacing
import com.battleship.core.Ship
import kotlinx.android.synthetic.main.cell.view.*

class GridAdapter(private val gridSize: Int, private val dataSet: Array<Array<Cell>>, private val ships: Array<Ship>) :
    RecyclerView.Adapter<GridAdapter.CellView>() {

    class CellView(val cellView: View) : RecyclerView.ViewHolder(cellView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CellView {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.cell, parent, false)
        return CellView(textView)
    }

    override fun onBindViewHolder(holder: CellView, position: Int) {
        val (x, y) = getCoordinates(position)
        val cell = dataSet[x][y]
        val view = holder.cellView.innerCellView
        var shipDrawn = false

        view.text = cell.getCellState().toString() //default state

    }

    private fun getCoordinates(position: Int): Pair<Int, Int> {
        return Pair(position / gridSize, position % gridSize)
    }

    private fun drawShip(view: TextView, ship: Ship, position: Int) {
        val direction = ship.directionFacing

        var text = when (direction) {
            DirectionFacing.North -> "△"
            DirectionFacing.West -> "◁"
            DirectionFacing.South -> "▽"
            DirectionFacing.East -> "▷"
        }
        if (ship.size == 1) {
            text = "◎"
        }
        view.text = text
    }

    override fun getItemCount() = dataSet.size * dataSet.size
}