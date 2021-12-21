package com.github.lipinskipawel.gui

import com.github.lipinskipawel.OptionsMenu
import com.github.lipinskipawel.PlayMenu
import com.github.lipinskipawel.listener.PitchListener
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JMenuBar
import javax.swing.UIManager

class Table {
    private val gameFrame: JFrame
    val playMenu: PlayMenu
    val optionsMenu: OptionsMenu
    private val drawableFacade: DrawableFacade
    private val gameDrawer: GameDrawer
    private val gamePanel: GamePanel

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
        val tableMenuBar = JMenuBar()
        playMenu = PlayMenu()
        optionsMenu = OptionsMenu()
        tableMenuBar.add(playMenu)
        tableMenuBar.add(optionsMenu)
        gameFrame.jMenuBar = tableMenuBar
        gameDrawer = GameDrawer()
        gamePanel = GamePanel()
        drawableFacade = DrawableFacade(gameDrawer, gamePanel)
        gamePanel.setFontToPlayerPanel(globalMenuFont)
        gamePanel.setFontToCenterAndSouth(textAreaFont)
        gameFrame.add(gameDrawer, BorderLayout.CENTER)
        gameFrame.add(gamePanel, BorderLayout.EAST)
        setWarmUp()
        gameFrame.isVisible = true
    }

    fun addConnectListener(listener: ActionListener?) {
        gamePanel.addButtonConnectListener(listener)
    }

    fun addMouseClassToGameDrawer(actionClassGameBoard: PitchListener?) {
        gameDrawer.addMouse(actionClassGameBoard)
    }

    fun addActionClassForPlayMenu(actionListener: ActionListener?) {
        playMenu.addActionClassForAllMenuItems(actionListener!!)
    }

    fun addActionClassForOptionMenu(actionListener: ActionListener?) {
        optionsMenu.addActionClassForAllMenuItems(actionListener!!)
    }

    val drawableFootballPitch: DrawableFootballPitch
        get() = drawableFacade

    // --------------------------------- GETer to ActionTable --------------------------------
    @Synchronized
    fun setWarmUp() {
        gameFrame.title = TITLE
        gamePanel.setWarmUP()
    }

    @Synchronized
    fun setOneVsOne() {
        gameFrame.title = TITLE
        gamePanel.setONEvsONE()
    }

    @Synchronized
    fun setHellMode() {
        gameFrame.title = TITLE
        gamePanel.setHellMode()
    }

    @Synchronized
    fun setOneVsLAN(ipLocalhost: String) {
        gameFrame.title = "Ip address: $ipLocalhost"
        gamePanel.setONEvsLAN()
    }

    @Synchronized
    fun setOneVsAI() {
        gameFrame.title = TITLE
        gamePanel.setONEvsAI()
    }

    fun setButtonEnabled(toBoolean: Boolean) {
        gamePanel.buttonSouth?.isEnabled = toBoolean
    }

    private fun gameFrameSetup(gameFrame: JFrame) {
        gameFrame.layout = BorderLayout()
        gameFrame.size = WINDOW_SIZE
        gameFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        gameFrame.setLocationRelativeTo(null)
    }

    val connectButton: JButton
        get() = gamePanel.buttonSouth!!

    fun IPEnemy(): String {
        return gamePanel.textFieldSouth?.text!!
    }
}
