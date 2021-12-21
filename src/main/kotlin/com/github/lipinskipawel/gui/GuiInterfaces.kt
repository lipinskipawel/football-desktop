package com.github.lipinskipawel.gui

import com.github.lipinskipawel.board.engine.Board
import com.github.lipinskipawel.board.engine.Player
import java.awt.Component
import java.awt.Dialog
import javax.swing.JOptionPane

interface RenderablePoint {
    val position: Int
}

interface DrawableFootballPitch {
    /**
     * This method draw [Board] state on the Pitch.
     *
     * @param board
     * @param playerView
     */
    fun drawPitch(board: Board, playerView: Player)

    /**
     * This method set the active player in the PLayer panel.
     *
     * @param activePlayer
     */
    fun activePlayer(activePlayer: Player)
}

interface UserDialogPresenter {
    fun showMessage(parent: Component?, messagee: String?): Dialog?
}


class DefaultUserDialogPresenter : UserDialogPresenter {
    override fun showMessage(parent: Component?, message: String?): Dialog? {
        val pane = JOptionPane(message)
        val dialog = pane.createDialog("Message")
        dialog.isVisible = true
        return dialog
    }
}
