package com.github.lipinskipawel.gui

import io.github.lipinskipawel.board.engine.Move
import io.github.lipinskipawel.board.engine.Player
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.SwingUtilities

class GamePanel : JPanel(BorderLayout()) {
    // NORTH
    private var playerONE: JLabel? = null
    private var playerTWO: JLabel? = null
    private var playerPanel // GRID(1, 2) holds player name and time
            : JPanel? = null

    // CENTER
    private var textArea: JTextArea? = null
    private var scrollPaneText: JScrollPane? = null

    // SOUTH
    private var labelSouth: JLabel? = null
    var textFieldSouth: JTextField? = null
    var buttonSouth: JButton? = null
    private var littleBox: JPanel? = null
    private var southPanel: JPanel? = null

    companion object {
        private val defaultColor = Color(238, 238, 238)
        private val activeColor = Color(0x64fe64)
        private val textWarUp =
            """
                This is Warmp-up mode.
                You can play here and try new tactics
                You can make a move by click on the
                empty point. You can undo the move by right-clicking.

                Rules:
                - move can be done into empty point
                - you can bounce of from the
                    existing lines
                - you can not move further than one
                    point
                - game ends when ball is in the goal
                    area or ball hits the corner
                Created by Paweł Lipiński
                """.trimIndent()

        private val text1vs1 = """
                In this mode you can play 1 vs 1 on
                one computer. Invite your friend
                and try to win a game. Rules are
                really simple, try to score a goal
                by putting ball into enemy goal area.
                """.trimIndent()

        private val textHellMode = """
                Each player have 2 tokens.
                Each undo move costs one token
                and gave this token to enemy.
                If player have no tokens left undo
                is NOT allowed anymore.
                """.trimIndent()

        private val text1vsLAN = """
                In this mode try to win with your friend
                connected by LAN. Rules are really
                simple, try to score a goal by putting
                ball into enemy goal area.
                Good luck.
                """.trimIndent()

        private val text1vsAI = """
                In this mode you will be playing
                against of AI. This is powerful
                engine which can beat you. Try
                to win with it.
                Good luck
                """.trimIndent()

        private val textGameView =
            """
                This is Game view.
                """.trimIndent()
    }

    init {
        preferredSize = Dimension(280, 100)
        creatingNORTH()
        creatingCENTER()
        creatingSOUTH()
    }

    /**
     * All method required to create all JPanels. Also to setUp these JPanels from other object
     */
    fun setWarmUP() {
        removeAll()
        textArea!!.text = textWarUp
        add(scrollPaneText, BorderLayout.CENTER)
        SwingUtilities.invokeLater { revalidate() }
        repaint()
    }

    fun setONEvsONE() {
        removeAll()
        playerPanel!!.removeAll()
        playerPanel!!.add(playerONE)
        playerPanel!!.add(playerTWO)
        textArea!!.text = text1vs1
        add(playerPanel, BorderLayout.NORTH)
        add(scrollPaneText, BorderLayout.CENTER)
        SwingUtilities.invokeLater { revalidate() }
        repaint()
    }

    fun setHellMode() {
        removeAll()
        playerPanel!!.removeAll()
        playerPanel!!.add(playerONE)
        playerPanel!!.add(playerTWO)
        textArea!!.text = textHellMode
        add(playerPanel, BorderLayout.NORTH)
        add(scrollPaneText, BorderLayout.CENTER)
        SwingUtilities.invokeLater { revalidate() }
        repaint()
    }

    fun setONEvsLAN(ipAddress: String) {
        removeAll()
        playerPanel!!.removeAll()
        playerPanel!!.add(playerONE)
        playerPanel!!.add(playerTWO)
        textArea!!.text = text1vsLAN.plus(
            """
            
            IP address: $ipAddress
        """.trimIndent()
        )
        add(playerPanel, BorderLayout.NORTH)
        add(scrollPaneText, BorderLayout.CENTER)
        add(southPanel, BorderLayout.SOUTH)
        SwingUtilities.invokeLater { revalidate() }
        repaint()
    }

    fun setONEvsAI() {
        removeAll()
        playerPanel!!.removeAll()
        playerPanel!!.add(playerONE)
        playerPanel!!.add(playerTWO)
        textArea!!.text = text1vsAI
        add(playerPanel, BorderLayout.NORTH)
        add(scrollPaneText, BorderLayout.CENTER)
        SwingUtilities.invokeLater { revalidate() }
        repaint()
    }

    fun setGameView() {
        removeAll()
        textArea!!.text = textGameView
        playerPanel!!.add(playerONE)
        playerPanel!!.add(playerTWO)
        add(scrollPaneText, BorderLayout.CENTER)
        SwingUtilities.invokeLater { revalidate() }
        repaint()
    }

    fun setExportGameMoves(moves: List<Move>) {
        val toList = moves
            .stream()
            .map { it -> it.move.joinToString { it.name } }
            .toList()
        textArea!!.text = toList.stream()
            .reduce { acc, s -> acc.plus(s).plus("\n") }
            .get()
        SwingUtilities.invokeLater { revalidate() }
        repaint()
    }

    /**
     * This method will replace all elements on current [GamePanel] with the [scrollPaneText] area. Given [message] will
     * be visible on the [scrollPaneText] through the [textArea].
     *
     * Given [message] will be automatically [trimIndent].
     */
    fun setInformationText(message: String) {
        removeAll()
        textArea!!.text = message.trimIndent()
        add(scrollPaneText, BorderLayout.CENTER)
        SwingUtilities.invokeLater { revalidate() }
        repaint()
    }

    fun setPanel(panelToDisplay: JPanel) {
        removeAll()
        add(panelToDisplay, BorderLayout.CENTER)
        SwingUtilities.invokeLater { revalidate() }
        repaint()
    }

    private fun creatingNORTH() {
        playerONE = JLabel("FIRST")
        playerTWO = JLabel("SECOND")
        playerONE!!.isOpaque = true
        playerTWO!!.isOpaque = true
        playerPanel = JPanel(GridLayout(1, 2))
    }

    private fun creatingCENTER() {
        textArea = JTextArea()
        textArea!!.lineWrap = true
        scrollPaneText = JScrollPane(textArea)
    }

    private fun creatingSOUTH() {
        labelSouth = JLabel("Write IP address of enemy")
        textFieldSouth = JTextField(10)
        textFieldSouth!!.toolTipText = "On top of window you have IP address"
        buttonSouth = JButton("Connect")
        littleBox = JPanel()
        littleBox!!.add(textFieldSouth)
        littleBox!!.add(buttonSouth)
        southPanel = JPanel(BorderLayout())
        southPanel!!.add(labelSouth, BorderLayout.NORTH)
        southPanel!!.add(littleBox, BorderLayout.CENTER)
    }

    /**
     * Package method
     *
     * @param font It's used to set up a font.
     */
    fun setFontToPlayerPanel(font: Font?) {
        playerONE!!.font = font
        playerTWO!!.font = font
    }

    fun setFontToCenterAndSouth(font: Font?) {
        textArea!!.font = font
        labelSouth!!.font = font
        textFieldSouth!!.font = font
        buttonSouth!!.font = font
    }

    fun activePlayer(activePlayer: Player) {
        if (activePlayer === Player.FIRST) {
            playerONE!!.background = activeColor
            playerTWO!!.background = defaultColor
        } else {
            playerTWO!!.background = activeColor
            playerONE!!.background = defaultColor
        }
    }

    fun activePlayer(isFirstPlayer: Boolean) {
        if (isFirstPlayer) {
            playerONE!!.background = activeColor
            playerTWO!!.background = defaultColor
        } else {
            playerTWO!!.background = activeColor
            playerONE!!.background = defaultColor
        }
    }

    fun addButtonConnectListener(listener: ActionListener?) {
        buttonSouth!!.addActionListener(listener)
    }

    fun setButtonStatus(status: Boolean) {
        buttonSouth?.isEnabled = status
    }
}
