package com.battleship.server.game

class IdGenerator(private val minValue: Long, private val maxValue: Long):
    GameIdGenerator {

    override fun getId(): Long {
        return (minValue.. maxValue).random()
    }

}

interface GameIdGenerator{
    fun getId(): Long
}