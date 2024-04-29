package com.github.lipinskipawel.controller

import com.github.lipinskipawel.gui.RenderablePoint

interface PitchController {

    fun leftClick(renderablePoint: RenderablePoint)

    fun rightClick(renderablePoint: RenderablePoint)

    fun tearDown()
}