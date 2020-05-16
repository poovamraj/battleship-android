package com.battleship.android.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.battleship.android.app.R
import com.battleship.core.BaseGrid
import com.battleship.core.Ship
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val gridSize = 10

        val oceanGrid = BaseGrid(gridSize, arrayOf(
            Ship.makeAirCarrier(),
            Ship.makeDestroyer(),
            Ship.makeDestroyer(),
            Ship.makeCruiser(),
            Ship.makeCruiser(),
            Ship.makeCruiser(),
            Ship.makeMine(),
            Ship.makeMine(),
            Ship.makeMine(),
            Ship.makeMine(),
        ))

        val oceanAdapter = GridAdapter(
            gridSize,
            oceanGrid.getCells(),
            oceanGrid.getShips()
        )

        grid.apply {
            layoutManager = GridLayoutManager(this@GameActivity, gridSize)
            adapter = oceanAdapter
        }
    }
}