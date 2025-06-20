package main;

import java.io.Serializable;

public class Message implements Serializable {
    private final MessageType type;
    private final String data;

    // Constructor for messages without data (e.g., NAME_REQUEST, NAME_ACCEPTED)
    public Message(MessageType type) {
        this.type = type;
        this.data = null;
    }

    // Constructor for messages with a data payload (e.g., USER_ADDED with username, TEXT with content)
    public Message(MessageType type, String data) {
        this.type = type;
        this.data = data;
    }

    // Returns the data payload of the message, or null if none
    public String getData() {
        return data;
    }

    // Returns the type of this message
    public MessageType getType() {
        return type;
    }
}
