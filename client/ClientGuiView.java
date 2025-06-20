package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGuiView {
    // Reference to controller
    private final ClientGuiController controller;
    // Chat window frame
    private JFrame frame = new JFrame("Chat");
    // Input field for messages
    private JTextField textField = new JTextField(50);
    // Area to display messages
    private JTextArea messages = new JTextArea(10, 50);
    // Area to display user list
    private JTextArea users = new JTextArea(10, 10);

    // Constructor
    public ClientGuiView(ClientGuiController controller) {
        this.controller = controller;
        initView();
    }

    // Initialize GUI components
    private void initView() {
        textField.setEditable(false);
        messages.setEditable(false);
        users.setEditable(false);

        frame.getContentPane().add(textField, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(messages), BorderLayout.WEST);
        frame.getContentPane().add(new JScrollPane(users), BorderLayout.EAST);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Send message on Enter
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.sendTextMessage(textField.getText());
                textField.setText("");
            }
        });
    }

    // Prompt for server address
    public String getServerAddress() {
        return JOptionPane.showInputDialog(
                frame,
                "Enter server address:",
                "Client configuration",
                JOptionPane.QUESTION_MESSAGE);
    }

    // Prompt for server port
    public int getServerPort() {
        while (true) {
            String port = JOptionPane.showInputDialog(
                    frame,
                    "Enter server port:",
                    "Client configuration",
                    JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Invalid port. Try again.",
                        "Client configuration",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Prompt for user name
    public String getUserName() {
        return JOptionPane.showInputDialog(
                frame,
                "Enter your name:",
                "Client configuration",
                JOptionPane.QUESTION_MESSAGE);
    }

    // Update connection status and UI
    public void notifyConnectionStatusChanged(boolean clientConnected) {
        textField.setEditable(clientConnected);
        if (clientConnected) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Connected to server",
                    "Chat",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    frame,
                    "Disconnected from server",
                    "Chat",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Append new message to view
    public void refreshMessages() {
        messages.append(controller.getModel().getNewMessage() + "\n");
    }

    // Update user list in view
    public void refreshUsers() {
        StringBuilder sb = new StringBuilder();
        for (String userName : controller.getModel().getAllUserNames()) {
            sb.append(userName).append("\n");
        }
        users.setText(sb.toString());
    }
}
