package com.github.lipinskipawel

import com.github.lipinskipawel.gui.Table
import java.awt.BorderLayout
import javax.swing.*

/**
 * This method create and returns JPanel that contains all necessary elements to display.
 * It will display a list of all players.
 */
fun gamePanelWeb(): JPanel {
    val listModel = DefaultListModel<String>()
    connectToLobby(listModel)
    return gamePanelWeb(listModel)
}

private fun gamePanelWeb(dataModel: DefaultListModel<String>): JPanel {
    val result = JPanel(BorderLayout())
    val playerList: JList<String> = JList(dataModel)
    playerList.font = Table.globalMenuFont
    val button = JButton("connect")

    button.addActionListener {
        if (playerList.selectedValuesList.size != 1) {
            JOptionPane.showMessageDialog(null, "Please select only one player")
            return@addActionListener
        }
        println(playerList.selectedValue)
    }

    result.add(playerList, BorderLayout.CENTER)
    result.add(button, BorderLayout.SOUTH)
    return result
}
