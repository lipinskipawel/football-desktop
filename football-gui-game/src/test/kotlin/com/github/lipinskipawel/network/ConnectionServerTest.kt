package com.github.lipinskipawel.network

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.net.URI

class ConnectionServerTest {

    @Test
    fun `should connect to WebServer`() {
        val connection = ConnectionManager.connectTo(URI.create("ws://localhost:8080/chat/one"))

        val isOpen = connection.isOpen()

        connection.close()
        Assertions.assertThat(isOpen).isTrue
    }
}