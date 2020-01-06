package com.github.lipinskipawel.network;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.controller.GameController;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * how to stopTimer Thread
 * https://docs.oracle.com/javase/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html
 */
public class ConnectionHandler extends Thread {

    private final int BOARD_SEND_PORT = 6786;
    private final String ipEnemy;
    private BoardInterface board;
    private GameController gameController;


    private volatile ConnectionState state;
    private ServerSocket listener;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;



    public ConnectionHandler(final GameController gameController) {
        this.state = ConnectionState.LISTENING;
        this.ipEnemy = null;
        this.gameController = gameController;
        start();
    }

    public ConnectionHandler(final GameController gameController, final String ipEnemy) {
        this.state = ConnectionState.CONNECTING;
        this.ipEnemy = ipEnemy;
        this.gameController = gameController;
        start();
    }


    public synchronized ConnectionState getConnectionState() {return this.state;}


    public synchronized void send(final BoardInterface board) throws IOException {
        if (this.state == ConnectionState.CONNECTED) {
            this.out.writeObject(board);
            this.out.flush();
        }
    }

    public synchronized void close() {
        this.state = ConnectionState.CLOSED;
        try {
            if (this.socket != null)
                this.socket.close();
            else if (this.listener != null)
                this.listener.close();
        }
        catch (IOException e) {
            System.out.println("Something wrong in synchronized close()");
        }
    }

    private synchronized void received(final BoardInterface board) {
        if (this.state == ConnectionState.CONNECTED)
            this.gameController.postBoard(board);
    }

    private synchronized void sendMessage(final String message) {
        if (this.state == ConnectionState.CONNECTED)
            this.gameController.postMessage(message);
    }

    private synchronized void connectionOpened() throws IOException {
        this.listener = null;
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(this.socket.getInputStream());
        this.state = ConnectionState.CONNECTED;
        //this.gameController.enableChat(true);
    }


    private synchronized void connectionClosedFromOtherSide() {
        if (this.state == ConnectionState.CONNECTED) {
            this.state = ConnectionState.CLOSED;
        }
    }


    private void cleanUp() {
        this.state = ConnectionState.CLOSED;
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (this.in != null) {
            try {
                this.in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (this.out != null) {
            try {
                this.out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (this.listener != null) {
            try {
                this.listener.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.socket = null;
        this.in = null;
        this.out = null;
        this.listener = null;
    }

    @Override
    public void run() {
        try {
            if (this.state == ConnectionState.LISTENING) {
                this.listener = new ServerSocket(this.BOARD_SEND_PORT, 5);
                this.socket = this.listener.accept();
                sendMessage("Server is now connected to " + this.socket.getInetAddress());
                this.listener.close();
            } else if (state == ConnectionState.CONNECTING) {
                this.socket = new Socket(this.ipEnemy, this.BOARD_SEND_PORT);
                sendMessage("Connected via socket with: " + this.socket.getInetAddress());
            }
            connectionOpened();
            while (this.state == ConnectionState.CONNECTED) {
                this.board = (BoardInterface) this.in.readObject();
                if (this.board == null)
                    connectionClosedFromOtherSide();
                else
                    received(this.board);
            }

        } catch (SocketException ee) {
            sendMessage("\nYour enemy left the game.\n");
        } catch (EOFException e) {
            sendMessage("\nYour enemy left the game.\n");
        } catch (IOException e) {
            if (this.state != ConnectionState.CLOSED) {
                System.out.println("\n\nERROR: " + e);
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            sendMessage("\nOther user tried to send you unknown object");
            System.exit(0);
        }
        finally { // Clean up before terminating the thread.
            cleanUp();
        }
    }

}
