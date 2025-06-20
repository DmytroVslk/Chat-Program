package client;

import main.ConsoleHelper;
import main.Message;
import main.MessageType;
import main.Connection;

import java.io.IOException;
import java.net.Socket;

public class Client {
    // Chat connection
    protected Connection connection;
    private volatile boolean clientConnected;

    // Prompt server address
    protected String getServerAddress() {
        ConsoleHelper.writeMessage("Enter server address:");
        return ConsoleHelper.readString();
    }

    // Prompt server port
    protected int getServerPort() {
        ConsoleHelper.writeMessage("Enter server port:");
        return ConsoleHelper.readInt();
    }

    // Prompt user name
    protected String getUserName() {
        ConsoleHelper.writeMessage("Enter your name:");
        return ConsoleHelper.readString();
    }

    // Thread handling socket communication
    public class SocketThread extends Thread {
        @Override
        public void run() {
            // Connect and handshake
            try {
                connection = new Connection(new Socket(getServerAddress(), getServerPort()));
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }
        }

        // Name handshake with server
        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST) {
                    connection.send(new Message(MessageType.USER_NAME, getUserName()));
                } else if (message.getType() == MessageType.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    return;
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        // Main loop for server messages
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    processIncomingMessage(message.getData());
                } else if (message.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(message.getData());
                } else if (message.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(message.getData());
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        // Print received text
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        // Notify when a user joins
        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage("User '" + userName + "' joined the chat.");
        }

        // Notify when a user leaves
        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage("User '" + userName + "' left the chat.");
        }

        // Update connection status
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }
    }

    // Create socket thread
    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    // Send text message
    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Failed to send message");
            clientConnected = false;
        }
    }

    // Allow console input
    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    // Client execution
    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();

        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            ConsoleHelper.writeMessage("Client error occurred.");
            return;
        }

        if (clientConnected) {
            ConsoleHelper.writeMessage("Connection established. Type 'exit' to quit.");
        } else {
            ConsoleHelper.writeMessage("Client error occurred.");
        }

        // Read console and send messages until exit
        while (clientConnected) {
            String text = ConsoleHelper.readString();
            if (text.equalsIgnoreCase("exit")) break;
            if (shouldSendTextFromConsole()) sendTextMessage(text);
        }
    }

    // Entry point
    public static void main(String[] args) {
        new Client().run();
    }
}
