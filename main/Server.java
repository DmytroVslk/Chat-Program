package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    // Stores all active user connections
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Enter the server port:");
        int port = ConsoleHelper.readInt();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("The chat server is running.");
            while (true) {
                // Accept a new connection and start a handler thread
                Socket socket = serverSocket.accept();
                new Handler(socket).start();
            }
        } catch (Exception e) {
            ConsoleHelper.writeMessage("Error while starting or operating the server.");
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            // Log a new connection
            ConsoleHelper.writeMessage("A new connection has been established with " + socket.getRemoteSocketAddress());

            String userName = null;

            try (Connection connection = new Connection(socket)) {
                // Perform handshake to get the user's name
                userName = serverHandshake(connection);

                // Broadcast that the new user has joined
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));

                // Send the new user a list of existing participants
                notifyUsers(connection, userName);

                // Process incoming messages from the user
                serverMainLoop(connection, userName);

            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Error while exchanging data with " + socket.getRemoteSocketAddress());
            }

            if (userName != null) {
                // Remove the user and notify others of the disconnection
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            }

            // Log connection closure
            ConsoleHelper.writeMessage("Connection with " + socket.getRemoteSocketAddress() + " closed.");
        }

        // Handles the handshake process: request, validate, and accept the user name
        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));

                Message message = connection.receive();
                if (message.getType() != MessageType.USER_NAME) {
                    ConsoleHelper.writeMessage("Received invalid message type from " + socket.getRemoteSocketAddress());
                    continue;
                }

                String userName = message.getData();

                if (userName.isEmpty()) {
                    ConsoleHelper.writeMessage("Connection attempt with empty name from " + socket.getRemoteSocketAddress());
                    continue;
                }

                if (connectionMap.containsKey(userName)) {
                    ConsoleHelper.writeMessage("Username already in use: " + userName);
                    continue;
                }

                // Add the new user to the map and confirm acceptance
                connectionMap.put(userName, connection);
                connection.send(new Message(MessageType.NAME_ACCEPTED));
                return userName;
            }
        }

        // Sends a list of current users to the newly connected client
        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (String name : connectionMap.keySet()) {
                if (name.equals(userName)) {
                    continue;
                }
                connection.send(new Message(MessageType.USER_ADDED, name));
            }
        }

        // Main loop for reading user messages and broadcasting them
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();

                if (message.getType() == MessageType.TEXT) {
                    // Broadcast the received text message
                    String data = message.getData();
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + data));
                } else {
                    // Log unexpected message types
                    ConsoleHelper.writeMessage("Received unsupported message type from " + socket.getRemoteSocketAddress());
                }
            }
        }
    }

    // Broadcast a message to all connected clients
    public static void sendBroadcastMessage(Message message) {
        for (Connection connection : connectionMap.values()) {
            try {
                connection.send(message);
            } catch (IOException e) {
                // Log failure to send message to a specific client
                ConsoleHelper.writeMessage("Failed to send message to " + connection.getRemoteSocketAddress());
            }
        }
    }
}
