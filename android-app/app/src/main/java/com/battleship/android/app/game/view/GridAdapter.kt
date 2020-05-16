package com.battleship.android.app.game.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.battleship.android.app.R
import com.battleship.core.Cell
import com.battleship.core.CellState
import com.battleship.core.DirectionFacing
import com.battleship.core.Ship
import kotlinx.android.synthetic.main.cell.view.*

class GridAdapter(private val gridSize: Int, private val drawShips: Boolean, private val dataSet: Array<Array<Cell>>, private val ships: Array<Ship>) :
    RecyclerView.Adapter<GridAdapter.CellView>() {

    class CellView(val cellView: View) : RecyclerView.ViewHolder(cellView)

    var chosenShip: Ship? = null

    var clickListener: GridCellClickListener? = null

    private var longClickMode = false
        set(value) {
            if(field != value){
                clickListener?.onLongClickModeChange(value)
            }
            field = value
        }

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

        view.text = "" //default state

        if(drawShips){
            ships.forEach {
                it.let {
                    val shipPart = it.partPosition(cell)
                    if(shipPart != null){
                        drawShip(holder.cellView.innerCellView, it, shipPart)
                        shipDrawn = true
                    }
                }
            }
        }

        when(cell.getCellState()) {
            is CellState.Sunk -> {
                view.setBackgroundResource(R.color.sunkColor)
                view.text = ""
            }
            CellState.Hit -> {
                view.setBackgroundResource(R.color.hitColor)
                view.text = "X"
            }
            CellState.Miss -> {
                view.setBackgroundResource(R.color.missColor)
                view.text = "X"
            }
            CellState.None -> {
                if(!shipDrawn){
                    view.text = ""
                    view.setBackgroundResource(R.color.backgroundColor)
                }
            }
        }

        holder.cellView.setOnClickListener {
            longClickMode = clickListener?.onClick(longClickMode, x, y, cell.getCellState()) ?: false
        }

        holder.cellView.setOnLongClickListener {
            longClickMode = clickListener?.onLongClick(longClickMode, x, y) ?: false
            return@setOnLongClickListener longClickMode
        }
    }

    private fun getCoordinates(position: Int): Pair<Int, Int> {
        return Pair(position/gridSize, position % gridSize)
    }

    private fun drawShip(view: TextView, ship: Ship, position: Int){
        val direction = ship.directionFacing
        if(ship == chosenShip){
            view.setBackgroundResource(R.color.chosenShipColor)
        } else {
            view.setBackgroundResource(R.color.shipPlacedColor)
        }
        var text = when(direction){
            DirectionFacing.North -> "△"
            DirectionFacing.West -> "◁"
            DirectionFacing.South -> "▽"
            DirectionFacing.East -> "▷"
        }
        if(ship.size == 1){
            text = "◎"
        }
        view.text = text
    }

    override fun getItemCount() = dataSet.size * dataSet.size

    interface GridCellClickListener {

        /**
         * return value is used to determine whether to go into long click mode
         */
        fun onLongClick(longClickMode: Boolean, x: Int, y: Int): Boolean

        /**
         * return value is used to tell whether to remain in long click mode
         */
        fun onClick(longClickMode: Boolean, x: Int, y: Int, cellState: CellState): Boolean

        /**
         * used to denote change in the click mode
         */
        fun onLongClickModeChange(longClickMode: Boolean)
    }
}