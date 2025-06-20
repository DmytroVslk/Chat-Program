package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    // Console reader
    private static BufferedReader bis = new BufferedReader(new InputStreamReader(System.in));

    // Print message
    public static void writeMessage(String message) {
        System.out.println(message);
    }

    // Read text line
    public static String readString() {
        while (true) {
            try {
                String buf = bis.readLine();
                if (buf != null) {
                    return buf;
                }
            } catch (IOException e) {
                writeMessage("Error reading text. Please try again.");
            }
        }
    }

    // Read integer
    public static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(readString().trim());
            } catch (NumberFormatException e) {
                writeMessage("Error reading number. Please enter a valid integer.");
            }
        }
    }
}
