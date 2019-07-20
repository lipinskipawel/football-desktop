package io.lipinski.network;

import io.lipinski.board.legacy.Player;
import io.lipinski.gui.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionChat extends Thread {

    private final int CHAT_SEND_PORT = 7986;
    private String ipEnemy;
    private Table table;


    private volatile ConnectionState state;
    private ServerSocket listener;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Player currentPlayerHolder;

    public ConnectionChat(final Table table) {
        this.state = ConnectionState.LISTENING;
        this.table = table;
        this.ipEnemy = null;
        this.currentPlayerHolder = Player.SECOND;
        start();
    }
    public ConnectionChat(final Table table, final String ipEnemy) {
        this.state = ConnectionState.CONNECTING;
        this.table = table;
        this.ipEnemy = ipEnemy;
        this.currentPlayerHolder = Player.FIRST;
        start();
    }

    public synchronized ConnectionState getConnectionState() {
        return this.state;
    }

    public synchronized void send(final String message) {
        if (this.state == ConnectionState.CONNECTED) {
            this.table.appendChat("SEND: " + message + "\n");
            this.out.println(message);
            this.out.flush();
            if (this.out.checkError()) {
                this.table.appendChat("Error occurred while trying to send data");
                this.out.close();
            }
        }
    }

    private synchronized void connectionOpened() throws IOException {
        this.listener = null;
        out = new PrintWriter(this.socket.getOutputStream());
        out.flush();
        in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.state = ConnectionState.CONNECTED;
        this.table.setChatEditable(true);
        this.table.appendChat("\nYou are connected via LAN with " + this.ipEnemy + "\n");
        this.table.appendRight("\nYou are playing as: " + this.currentPlayerHolder);
    }

    public synchronized void close() {
        this.state = ConnectionState.CLOSED;
        this.table.setChatEditable(false);
        try {
            if (this.listener != null)
                this.listener.close();
            if (this.socket != null)
                this.socket.close();
        } catch (IOException e) {
            System.out.println("Something wrong in synchronized close()");
        }

    }

    private synchronized void connectionClosedFromOtherSide() {
        if (this.state == ConnectionState.CONNECTED) {
            this.table.appendChat("Connection closed from other side");
            this.state = ConnectionState.CLOSED;
        }
    }

    private synchronized void received(final String message) {
        if (this.state == ConnectionState.CONNECTED)
            this.table.appendChat("RECEIVE: " + message + "\n");
    }

    private void cleanUp() {
        this.state = ConnectionState.CLOSED;
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            }
            catch (IOException e) {
            }
        }
        socket = null;
        in = null;
        out = null;
        listener = null;
    }

    @Override
    public void run() {
        try {

            if (this.state == ConnectionState.LISTENING) {
                this.listener = new ServerSocket(CHAT_SEND_PORT);
                this.socket = this.listener.accept();
                this.listener.close();
                this.ipEnemy = socket.getInetAddress().getHostAddress();
            } else if (this.state == ConnectionState.CONNECTING) {
                this.socket = new Socket(this.ipEnemy, CHAT_SEND_PORT);
            }
            connectionOpened();
            while (this.state == ConnectionState.CONNECTED) {
                String input = in.readLine();
                if (input == null)
                    connectionClosedFromOtherSide();
                else
                    received(input);
            }
        } catch (IOException ee) {
            this.state = ConnectionState.CLOSED;

        } finally {
            cleanUp();
        }
    }



}
