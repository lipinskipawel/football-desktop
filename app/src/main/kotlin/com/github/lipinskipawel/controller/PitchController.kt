package com.github.lipinskipawel.controller

import com.github.lipinskipawel.gui.RenderablePoint
import io.github.lipinskipawel.board.engine.Move

interface PitchController {

    fun leftClick(renderablePoint: RenderablePoint)

    fun rightClick(renderablePoint: RenderablePoint)

    fun tearDown()

    fun gameMoves(): List<Move>
}