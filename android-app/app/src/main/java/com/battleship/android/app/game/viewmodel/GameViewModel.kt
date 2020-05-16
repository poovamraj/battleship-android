package com.battleship.android.app.game.viewmodel

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.battleship.android.app.game.model.DamageReport
import com.battleship.android.app.game.model.Game
import com.battleship.android.app.game.model.GameConfig
import com.battleship.android.app.game.model.online.OnlineGame
import com.battleship.core.*

class OnlineGameViewModel(private val onlinePlayer: OnlineGame, private val createRoom: Boolean) :
    GameViewModel(onlinePlayer) {

    private var socketInitialized = false

    private val roomId: MutableLiveData<Long> by lazy { MutableLiveData() }

    var onJoinRoomFailed: ((String) -> Unit)? = null

    init {
        onlinePlayer.connect()
    }

    fun getRoomIdObservable(): LiveData<Long> {
        return roomId
    }

    override fun onGameInitialized() {
        super.onGameInitialized()
        socketInitialized = true
        if (createRoom) {
            gameState.value = GameState.CreateRoom
            createRoom()
        } else {
            gameState.value = GameState.JoinRoom
        }
    }

    private fun createRoom() {
        if (socketInitialized) {
            //grid size and no of steps given below is not useful now, but will be required in future
            //when host can create game with different grid size and number of steps
            onlinePlayer.createRoom(GameConfig.GRID_SIZE, GameConfig.NO_OF_STEPS) {
                roomId.value = it
            }
        }
    }

    fun joinRoom(roomId: Long) {
        if (socketInitialized) {
            onlinePlayer.joinRoom(roomId) {
                onJoinRoomFailed?.invoke(it.message)
            }
        }
    }


    override fun onCleared() {
        onlinePlayer.disconnect()
    }
}

open class GameViewModel(private val game: Game) : ViewModel(), Game.GameEvents {

    sealed class GameState {
        object CreateRoom : GameState()
        object JoinRoom : GameState()
        data class PositionShips(val placementMode: Boolean) : GameState()
        object WaitingForOpponent : GameState()
        object PlayerTurn : GameState()
        object OpponentTurn : GameState()
        object PlayerTakingFire : GameState()
        object OpponentTakingFire : GameState()
        object GameLost : GameState()
        object GameWon : GameState()
        data class GameInterrupted(val reason: String) : GameState()
    }

    private var gameOver = false

    var onGridDataChanged: ((Grid) -> Unit)? = null

    var onShipCannotBePlaced: ((ShipPlacingResult) -> Unit)? = null

    protected val gameState: MutableLiveData<GameState> by lazy { MutableLiveData() }

    private val shipChosenToMove: MutableLiveData<Ship?> by lazy { MutableLiveData() }

    fun initialize() {
        game.setGameEvents(this)
        game.initializeGame()
    }

    fun getOceanGrid(): Grid {
        return game.getOceanGrid()
    }

    fun getTargetGrid(): Grid {
        return game.getTargetGrid()
    }

    fun getGameState(): LiveData<GameState> {
        return gameState
    }

    fun getChosenShip(): LiveData<Ship?> {
        return shipChosenToMove
    }

    fun placeShipsRandomly() {
        if (gameState.value is GameState.PositionShips) {
            getOceanGrid().placeShipsRandomly()
        }
    }

    fun onGridLongPressed(grid: Grid, placementMode: Boolean, x: Int, y: Int): Boolean {
        if (gameState.value is GameState.PositionShips) {
            return if (!placementMode) {
                shipChosenToMove.value = grid.getShipAtPosition(x, y)
                shipChosenToMove.value != null
            } else {
                shipChosenToMove.value = null
                false
            }
        }
        return false
    }

    fun onGridTapped(grid: Grid, placementMode: Boolean, x: Int, y: Int): Boolean {
        if (gameState.value is GameState.PositionShips) {
            if (placementMode) {
                val tempShip =
                    shipChosenToMove.value //not use let?. since it would cause confusion while returning
                if (tempShip != null) {
                    val result = grid.placeShip(tempShip, x, y)
                    if (result != ShipPlacingResult.Placed) {
                        onShipCannotBePlaced?.invoke(result)
                    }
                    shipChosenToMove.value = null
                    onGridDataChanged?.invoke(grid)
                    return false
                }
                return true
            } else {
                val ship = grid.getShipAtPosition(x, y)
                ship?.let { grid.rotateShip(ship) }
                onGridDataChanged?.invoke(grid)
                return false
            }
        }
        return false
    }

    fun onPlacementModeChange(placementMode: Boolean) {
        gameState.value = GameState.PositionShips(placementMode)
    }

    override fun onGameInitialized() {
        //no logic needed here only [OnlineGameViewModel] uses this method to create/join room
    }

    override fun onReadyToPositionShips() {
        gameState.value = GameState.PositionShips(false)
    }

    override fun onShipsPositioned() {
        gameState.value = GameState.WaitingForOpponent
        game.readyToPlay()
    }

    override fun onGameStarted(takeTurn: Boolean) {
        if (takeTurn) {
            gameState.value = GameState.PlayerTurn
        } else {
            gameState.value = GameState.OpponentTurn
        }
    }

    fun fire(positions: ArrayList<Position>, cellState: CellState): Boolean {
        if (gameState.value == GameState.PlayerTurn && cellState == CellState.None) {
            game.fire(positions)
            return true
        }
        return false
    }

    override fun onBeingFired(damageReport: DamageReport) {
        if (!gameOver) {
            gameState.value = GameState.PlayerTakingFire
            game.sendDamageReport(damageReport)
            Handler().postDelayed({
                gameState.value = GameState.PlayerTurn
            }, GameConfig.RESPONSE_ANIMATION_DELAY)
        }
    }

    override fun onReceivingDamageReport(damageReport: DamageReport) {
        if (!gameOver) {
            gameState.value = GameState.OpponentTakingFire
            Handler().postDelayed({
                gameState.value = GameState.OpponentTurn
            }, GameConfig.RESPONSE_ANIMATION_DELAY)
        }
    }


    override fun onGameWon() {
        gameState.value = GameState.GameWon
        gameOver = true
    }

    override fun onGameLost() {
        gameState.value = GameState.GameLost
        gameOver = true
    }

    override fun onGameInterrupted(message: String) {
        if (gameState.value != GameState.GameWon && gameState.value != GameState.GameLost) {
            gameState.value = GameState.GameInterrupted(message)
        }
    }

}

class GameViewModelFactory(private val repository: Game) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GameViewModel(repository) as T
    }
}

class OnlineGameViewModelFactory(
    private val repository: OnlineGame,
    private val createRoom: Boolean
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OnlineGameViewModel(repository, createRoom) as T
    }
}