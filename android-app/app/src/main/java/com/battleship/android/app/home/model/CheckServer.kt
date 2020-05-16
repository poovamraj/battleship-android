package com.battleship.android.app.home.model

import com.battleship.android.app.common.runOnMainThread
import com.battleship.android.websocketclient.GameSocket

/**
 * We do not pass this instance to GameActivity
 * though no of connections to server will be high, activity is called by OS and is not reliable
 * it is better to create a connection there than pass this instance
 * in future we can make this into a http call
 */
class CheckServer(
    private val socket: GameSocket,
    private val onSuccess: (GameSocket) -> Unit,
    private val onFailure: (Exception) -> Unit
) : GameSocket.Events {

    fun connect() {
        socket.setEvents(this)
        socket.connect()
    }

    override fun onOpen() {
        runOnMainThread { onSuccess.invoke(socket) }
    }

    override fun onClose() {

    }

    override fun onMessage(message: String) {

    }

    override fun onError(ex: Exception) {
        runOnMainThread { onFailure.invoke(ex) }
    }

}