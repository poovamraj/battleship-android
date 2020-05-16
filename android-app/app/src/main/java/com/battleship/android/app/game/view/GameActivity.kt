package com.battleship.android.app.game.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.battleship.android.app.R
import com.battleship.android.app.game.model.bot.BotGame
import com.battleship.android.app.game.model.GameConfig
import com.battleship.android.app.game.viewmodel.GameViewModel
import com.battleship.android.app.game.viewmodel.GameViewModelFactory
import com.battleship.core.*
import kotlinx.android.synthetic.main.activity_game.*

//TODO Have to check how it holds for configuration change
class GameActivity : AppCompatActivity() {

    companion object {
        fun constructBotViewModel(activity: GameActivity, oceanGrid: Grid, targetGrid: Grid): GameViewModel {
            val game = BotGame(
                oceanGrid,
                targetGrid
            )
            return ViewModelProvider(
                activity,
                GameViewModelFactory(
                    game
                )
            ).get(GameViewModel::class.java)
        }
    }

    private lateinit var viewModel: GameViewModel

    //TODO :High declaring onCreate makes it very heavy, need to refactor this method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val gridSize = GameConfig.GRID_SIZE

        /*
        Viewmodel and model objects are constructed
        in future we can move this to DI
         */
        val oceanGrid = GridBuilder.createGrid(gridSize, GameConfig.createShips())
        val targetGrid = GridBuilder.createGrid(gridSize, GameConfig.createShips())

        viewModel =
            constructBotViewModel(
                this,
                oceanGrid,
                targetGrid
            )

        /*
        Declaring Ocean Grid and Target Grid adapters
         */
        val gridToAdapterMapping: HashMap<Grid, GridAdapter> = HashMap()

        val oceanAdapter = getOceanAdapter(gridSize)
        gridToAdapterMapping[viewModel.getOceanGrid()] = oceanAdapter

        val targetAdapter = getTargetGridAdapter(gridSize)
        gridToAdapterMapping[viewModel.getTargetGrid()] = targetAdapter

        gameHolder.adapter = BaseGridHolder(
            arrayListOf(
                GridHolderDataSet(
                    GridLayoutManager(this, gridSize),
                    oceanAdapter
                ),
                GridHolderDataSet(
                    GridLayoutManager(this, gridSize),
                    targetAdapter
                )
            )
        )

        /*
         Setting all the observables
         */

        gameHolder.isUserInputEnabled = false

        viewModel.getChosenShip().observe(this) {
            oceanAdapter.chosenShip = it
            oceanAdapter.notifyDataSetChanged()
        }

        viewModel.onGridDataChanged = { grid ->
            val adapter = gridToAdapterMapping[grid]
            adapter?.notifyDataSetChanged()//TODO all notify data set change can be targeted
        }

        viewModel.onShipCannotBePlaced = { reason ->
            showShipCannotBePlacedError(reason)
        }

        viewModel.getGameState().observe(this) { gameState ->
            processGameState(gameState, oceanAdapter, targetAdapter)
        }

        //initialize
        viewModel.initialize()
    }

    private fun showShipCannotBePlacedError(reason: ShipPlacingResult){
        val message = when(reason){
            ShipPlacingResult.OutOfBoundary -> getString(R.string.cannot_place_out_of_boundary)
            ShipPlacingResult.InUse -> getString(R.string.cannot_place_in_use)
            ShipPlacingResult.Placed -> getString(R.string.placed_successfully)
            ShipPlacingResult.Immovable -> getString(R.string.ship_cannot_move)
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun positionShipsView(placementMode: Boolean){
        gameHolder.currentItem = 0
        gameHolder.visibility = View.VISIBLE
        titleTextView.text = getString(R.string.position_ships)
        actionButton.text = getString(R.string.start_game)
        actionButton.visibility = View.VISIBLE

        secondaryButton.visibility = View.VISIBLE
        secondaryButton.text = getString(R.string.place_random)

        if (placementMode) {
            titleTextView.text = getString(R.string.place_chose_ship)
        } else {
            titleTextView.text = getString(R.string.position_ships)
        }

        actionButton.setOnClickListener {
            viewModel.onShipsPositioned()
        }
    }

    private fun waitingForOpponentView(){
        titleTextView.text = getString(R.string.waiting_for_opponent)
        actionButton.visibility = View.GONE
        secondaryButton.visibility = View.GONE
    }

    private fun playerTurnView(){
        actionButton.visibility = View.GONE
        secondaryButton.visibility = View.GONE
        gameHolder.currentItem = 1
        titleTextView.text = getString(R.string.firing_guide)
    }

    private fun opponentTurnView(){
        actionButton.visibility = View.GONE
        secondaryButton.visibility = View.GONE
        gameHolder.currentItem = 0
        titleTextView.text = getString(R.string.opponent_turn)
    }

    private fun gameEndView(title: String){
        titleTextView.text = title
        actionButton.visibility = View.VISIBLE
        actionButton.text = getString(R.string.home)
        actionButton.setOnClickListener {
            finish()
        }
    }

    private fun opponentTakingFireView(){
        titleTextView.text = getString(R.string.your_shot)
        gameHolder.currentItem = 1
    }

    private fun playerTakingFireView(){
        titleTextView.text = getString(R.string.opponent_shot)
        gameHolder.currentItem = 0
    }

    private fun processGameState(gameState: GameViewModel.GameState, oceanAdapter: GridAdapter, targetAdapter: GridAdapter){
        when (gameState) {

            is GameViewModel.GameState.PositionShips -> {
                positionShipsView(gameState.placementMode)

                secondaryButton.setOnClickListener {
                    viewModel.placeShipsRandomly()
                    oceanAdapter.notifyDataSetChanged()
                }
            }

            GameViewModel.GameState.WaitingForOpponent -> {
                waitingForOpponentView()
            }

            GameViewModel.GameState.PlayerTurn -> {
                playerTurnView()
            }

            GameViewModel.GameState.OpponentTurn -> {
                opponentTurnView()
            }

            GameViewModel.GameState.OpponentTakingFire -> {
                opponentTakingFireView()
                targetAdapter.notifyDataSetChanged()
            }

            GameViewModel.GameState.PlayerTakingFire -> {
                playerTakingFireView()
                oceanAdapter.notifyDataSetChanged()
            }

            GameViewModel.GameState.GameLost -> {
                oceanAdapter.notifyDataSetChanged()
                gameEndView(getString(R.string.lost_message))
            }

            GameViewModel.GameState.GameWon -> {
                targetAdapter.notifyDataSetChanged()
                gameEndView(getString(R.string.win_message))
            }

            is GameViewModel.GameState.GameInterrupted -> {
                targetAdapter.notifyDataSetChanged()
                gameEndView(gameState.reason)
            }
        }
    }



    private fun getOceanAdapter(gridSize: Int): GridAdapter {
        val oceanAdapter = GridAdapter(
            gridSize,
            true,
            viewModel.getOceanGrid().getCells(),
            viewModel.getOceanGrid().getShips()
        )
        oceanAdapter.clickListener = object :
            GridAdapter.GridCellClickListener {
            override fun onLongClick(longClickMode: Boolean, x: Int, y: Int): Boolean {
                return viewModel.onGridLongPressed(
                    viewModel.getOceanGrid(),
                    longClickMode,
                    x,
                    y
                )
            }

            override fun onClick(longClickMode: Boolean, x: Int, y: Int, cellState: CellState): Boolean {
                return viewModel.onGridTapped(viewModel.getOceanGrid(), longClickMode, x, y)
            }

            override fun onLongClickModeChange(longClickMode: Boolean) {
                viewModel.onPlacementModeChange(longClickMode)
            }
        }
        return oceanAdapter
    }

    private fun getTargetGridAdapter(gridSize: Int): GridAdapter {
        val targetAdapter = GridAdapter(
            gridSize,
            false,
            viewModel.getTargetGrid().getCells(),
            viewModel.getTargetGrid().getShips()
        )
        targetAdapter.clickListener = object :
            GridAdapter.GridCellClickListener {
            override fun onLongClick(longClickMode: Boolean, x: Int, y: Int): Boolean {
                return false
            }

            override fun onClick(
                longClickMode: Boolean,
                x: Int,
                y: Int,
                cellState:
                CellState
            ): Boolean {
                val fired = viewModel.fire(arrayListOf(Position(x, y)), cellState)
                if (!fired) {
                    Toast.makeText(
                        this@GameActivity,
                        getString(R.string.cannot_fire),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return false
            }

            override fun onLongClickModeChange(longClickMode: Boolean) {

            }
        }
        return targetAdapter
    }

    private fun showExitConfirmation() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.exit_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                finish()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    override fun onBackPressed() {
        showExitConfirmation() //TODO probably need not show when game is ended
    }
}