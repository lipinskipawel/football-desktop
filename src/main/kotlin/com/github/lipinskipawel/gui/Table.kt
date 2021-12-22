package com.github.lipinskipawel.gui

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.UIManager

/**
 * This class is a main container that holds every GUI object.
 * Besides, holding every GUI reference it is capable of placing them on the [gameFrame] as well as configuring global
 * font, window dimension and others.
 */
class Table(jMenuBar: JMenuBar, gamePanel: GamePanel, gameDrawer: GameDrawer) {
    private val gameFrame: JFrame

    companion object {
        private val WINDOW_SIZE = Dimension(700, 600)
        private val globalMenuFont = Font("sans-serif", Font.PLAIN, 20)
        private val textAreaFont = Font("sans-serif", Font.PLAIN, 15)
        private const val TITLE = "Football game"
    }

    /**
     * Default behavior of this class is to set-up warm-up mode.
     * In this case constructor invoke setWarmUp() method to create proper right view.
     * The board is created as warm-up in GameDrawer class.
     */
    init {
        // https://stackoverflow.com/questions/1951558/list-of-java-swing-ui-properties
        //UIManager.put("MenuBar.font", f);
        UIManager.put("Menu.font", globalMenuFont)
        UIManager.put("MenuItem.font", globalMenuFont)
        gameFrame = JFrame(TITLE)
        gameFrameSetup(gameFrame)
        jMenuBar
                .components
                .map { it as JMenu }
                .forEach { jMenu ->
                    jMenu.font = globalMenuFont
                    jMenu.menuComponents.forEach { it.font = globalMenuFont }
                }
        gameFrame.jMenuBar = jMenuBar

        gamePanel.setFontToPlayerPanel(globalMenuFont)
        gamePanel.setFontToCenterAndSouth(textAreaFont)

        gameFrame.add(gameDrawer, BorderLayout.CENTER)
        gameFrame.add(gamePanel, BorderLayout.EAST)
    }

    private fun gameFrameSetup(gameFrame: JFrame) {
        gameFrame.layout = BorderLayout()
        gameFrame.size = WINDOW_SIZE
        gameFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        gameFrame.setLocationRelativeTo(null)
    }

    fun show() {
        gameFrame.isVisible = true
    }
}
