package com.github.lipinskipawel.web

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

internal class LoginForm {
    private val frame = createLoginFrame()

    /**
     * Will show a login form.
     * @param onLogin is a callback which will be called with the given user input
     */
    fun showLoginForm(onLogin: (username: String) -> Unit) {
        val labelPanel = JPanel()
        labelPanel.add(JLabel("Register name"))

        val textPanel = JPanel()
        val text = JTextField()
        text.preferredSize = Dimension(100, 40)
        textPanel.add(text)

        val loginButton = JButton("login")
        loginButton.addActionListener {
            onLogin(text.text)
        }

        frame.add(labelPanel, BorderLayout.NORTH)
        frame.add(textPanel, BorderLayout.CENTER)
        frame.add(loginButton, BorderLayout.SOUTH)

        frame.isVisible = true
    }

    fun dispose() {
        frame.dispose()
    }

    private fun createLoginFrame(): JFrame {
        val frame = JFrame("new login")
        frame.setLocationRelativeTo(null)
        frame.setSize(100, 100)
        return frame
    }
}
