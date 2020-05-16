package com.battleship.android.app.home.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.battleship.android.app.R
import com.battleship.android.app.game.view.GameActivity
import com.battleship.android.app.game.view.GameActivity.Companion.CREATE_ROOM_MODE
import com.battleship.android.app.game.view.GameActivity.Companion.GAME_MODE_KEY
import com.battleship.android.app.game.view.GameActivity.Companion.HOSTNAME_KEY
import com.battleship.android.app.game.view.GameActivity.Companion.JOIN_ROOM_MODE
import com.battleship.android.app.game.view.GameActivity.Companion.PLAY_BOT
import com.battleship.android.app.game.view.GameActivity.Companion.PORT_KEY
import com.battleship.android.app.home.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val networkError = { ex: Exception ->
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.onlineGameLoading.observe(this) {
            if (it) onlineLoaderView.visibility = View.VISIBLE else onlineLoaderView.visibility =
                View.GONE
        }

        createRoom.setOnClickListener {
            val (address, port) = getAddressAndPort()
            viewModel.createRoom(address, port, {
                createRoom(address, port)
            }, networkError)
        }

        joinRoom.setOnClickListener {
            val (address, port) = getAddressAndPort()
            viewModel.joinRoom(address, port, {
                joinRoom(address, port)
            }, networkError)
        }

        playBot.setOnClickListener {
            playBot()
        }

    }

    private fun getAddressAndPort(): Pair<String, Int> {
        val address = hostNameInput.text.toString()
        val port = portInput.text.toString().toInt()
        return Pair(address, port)
    }

    private fun createRoom(address: String, port: Int) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(GAME_MODE_KEY, CREATE_ROOM_MODE)
        intent.putExtra(HOSTNAME_KEY, address)
        intent.putExtra(PORT_KEY, port)
        startActivity(intent)
    }

    private fun joinRoom(address: String, port: Int) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(GAME_MODE_KEY, JOIN_ROOM_MODE)
        intent.putExtra(HOSTNAME_KEY, address)
        intent.putExtra(PORT_KEY, port)
        startActivity(intent)
    }

    private fun playBot() {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(GAME_MODE_KEY, PLAY_BOT)
        botLoaderView.visibility = View.VISIBLE
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        onlineLoaderView.visibility = View.GONE //This is a small hack, reach me to know details :)
        botLoaderView.visibility = View.GONE
    }
}