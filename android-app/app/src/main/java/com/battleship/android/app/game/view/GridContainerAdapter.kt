package com.battleship.android.app.game.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.battleship.android.app.R
import kotlinx.android.synthetic.main.grid.view.*

/** Used to hold both Ocean grid and target grid, will slide according to the current player*/
class BaseGridHolder(private val dataSet: ArrayList<GridHolderDataSet>) : RecyclerView.Adapter<BaseGridHolder.GridHolder>() {

    class GridHolder(val gridHolder: View) : RecyclerView.ViewHolder(gridHolder)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridHolder {
        val grid = LayoutInflater.from(parent.context).inflate(R.layout.grid, parent, false)
        return GridHolder(grid)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: GridHolder, position: Int) {
        holder.gridHolder.grid.apply {
            setHasFixedSize(true)
            layoutManager = dataSet[position].gridLayoutManager
            adapter = dataSet[position].gridAdapter
        }
    }
}

data class GridHolderDataSet(val gridLayoutManager: GridLayoutManager, val gridAdapter: GridAdapter)