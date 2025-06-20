package client;

import main.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {
    // Provide custom socket thread
    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    // Disable console input for bot
    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    // Generate a random bot username
    @Override
    protected String getUserName() {
        return "date_bot_" + (int) (Math.random() * 100);
    }

    // Start the bot client
    public static void main(String[] args) {
        Client client = new BotClient();
        client.run();
    }

    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            String hello = "Hello chat. I'm a robot. I understand the commands: date, day, month, year, time, hour, minutes, seconds.";
            BotClient.this.sendTextMessage(hello);

            super.clientMainLoop();
        }

        // Process incoming messages
        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);

            String userNameDelimiter = ": ";
            String[] split = message.split(userNameDelimiter);
            if (split.length != 2) return;

            String messageWithoutUserName = split[1];

            String format = null;
            switch (messageWithoutUserName) {
                case "date":
                    format = "d.MM.YYYY";
                    break;
                case "day":
                    format = "d";
                    break;
                case "month":
                    format = "MMMM";
                    break;
                case "year":
                    format = "YYYY";
                    break;
                case "time":
                    format = "H:mm:ss";
                    break;
                case "hour":
                    format = "H";
                    break;
                case "minute":
                    format = "m";
                    break;
                case "seconds":
                    format = "s";
                    break;
            }

            if (format != null) {
                String answer = new SimpleDateFormat(format).format(Calendar.getInstance().getTime());
                BotClient.this.sendTextMessage("Information for " + split[0] + ": " + answer);
            }
        }
    }
}