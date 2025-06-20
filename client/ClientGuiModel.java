package client;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class ClientGuiModel {
    // Sorted set of usernames
    private final Set<String> allUserNames = new TreeSet<>();
    // Latest incoming message
    private String newMessage;

    // Get unmodifiable view of users
    public Set<String> getAllUserNames() {
        return Collections.unmodifiableSet(allUserNames);
    }

    // Get latest message
    public String getNewMessage() {
        return newMessage;
    }

    // Update latest message
    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    // Add a user
    public void addUser(String newUserName) {
        allUserNames.add(newUserName);
    }

    // Remove a user
    public void deleteUser(String userName) {
        allUserNames.remove(userName);
    }
}
