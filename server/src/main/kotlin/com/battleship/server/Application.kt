package com.battleship.server

import com.battleship.server.game.common.GameIdGenerator
import com.battleship.server.game.common.IdGenerator
import com.battleship.server.game.controller.GameController
import com.battleship.server.game.controller.GameCoordinator
import com.battleship.server.game.controller.Lobby
import com.battleship.server.game.network.BattleShipServer
import com.battleship.server.websocket.GameServer
import com.battleship.server.websocket.thirdparty.JavaWebSocket

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        val game = BattleShipServer(
            ServiceLocator.server,
            ServiceLocator.gameController
        )
        game.start()
    }

}

object ServiceLocator {
    val server: GameServer = JavaWebSocket("0.0.0.0", 8080)

    private val idGenerator: GameIdGenerator = IdGenerator(11111, 99999)

    private val lobby: Lobby = Lobby(ArrayList(), idGenerator)

    private val gameCoordinator = GameCoordinator(lobby)

    val gameController = GameController(gameCoordinator)
}