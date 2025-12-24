package com.github.lipinskipawel.listener

import com.github.lipinskipawel.controller.GameViewController
import com.github.lipinskipawel.controller.HellController
import com.github.lipinskipawel.controller.OneVsAiController
import com.github.lipinskipawel.controller.OneVsLanController
import com.github.lipinskipawel.controller.OneVsOneController
import com.github.lipinskipawel.controller.PitchController
import com.github.lipinskipawel.controller.WarmupController
import com.github.lipinskipawel.gui.DefaultUserDialogPresenter
import com.github.lipinskipawel.gui.DrawableFootballPitch
import com.github.lipinskipawel.gui.RenderablePoint
import com.github.lipinskipawel.network.ConnectionManager
import com.github.lipinskipawel.network.ConnectionManager.Companion.getInetAddress
import com.github.lipinskipawel.network.ConnectionManager.Companion.waitForConnection
import com.github.lipinskipawel.web.OneVsWebController
import com.github.lipinskipawel.web.connectToGame
import io.github.lipinskipawel.board.engine.Move
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.net.InetAddress
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.swing.SwingUtilities

class PitchListener internal constructor(private val drawableFootballPitch: DrawableFootballPitch) : MouseListener {
    private val pool: ExecutorService = Executors.newSingleThreadExecutor()
    private var currentActiveController: PitchController = WarmupController(drawableFootballPitch)

    override fun mouseClicked(e: MouseEvent) {
        val src = e.source
        if (SwingUtilities.isRightMouseButton(e)) {
            currentActiveController.rightClick((src as RenderablePoint))
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            currentActiveController.leftClick((src as RenderablePoint))
        }
    }

    fun setGameMode(gameMode: String?) {
        setGameMode(gameMode, emptyList())
    }

    fun setGameMode(gameMode: String?, moves: List<Move>) {
        tearDownActiveController()
        when (gameMode) {
            "warm-up" -> currentActiveController = WarmupController(drawableFootballPitch)
            "1vs1" -> currentActiveController = OneVsOneController(drawableFootballPitch)
            "hell mode" -> currentActiveController = HellController(drawableFootballPitch)
            "1vsAI" -> currentActiveController = OneVsAiController(drawableFootballPitch)
            "1vsLAN-server" -> {
                currentActiveController = OneVsLanController(drawableFootballPitch, DefaultUserDialogPresenter())
                pool.submit {
                    val connection = waitForConnection(getInetAddress())
                    (currentActiveController as OneVsLanController?)!!.injectConnection(connection, false)
                }
            }

            "1vsLAN-client" -> currentActiveController = OneVsLanController(drawableFootballPitch, DefaultUserDialogPresenter())
            "1vsWeb" -> currentActiveController = OneVsWebController(drawableFootballPitch, connectToGame())
            "game-view" -> currentActiveController = GameViewController(drawableFootballPitch, moves)
            else -> throw RuntimeException("Should never happen")
        }
    }

    fun gameMoves(): List<Move> {
        return currentActiveController.gameMoves()
    }

    private fun tearDownActiveController() {
        currentActiveController.tearDown()
    }

    fun connectTo(address: InetAddress?) {
        val connection = ConnectionManager.connectTo(address!!)
        (currentActiveController as OneVsLanController?)!!.injectConnection(connection, true)
    }

    override fun mousePressed(e: MouseEvent) {}
    override fun mouseReleased(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}
}
