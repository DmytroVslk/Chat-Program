package client;

public class ClientGuiController extends Client {
    // MVC model
    private ClientGuiModel model = new ClientGuiModel();
    // MVC view
    private ClientGuiView view = new ClientGuiView(this);

    // Provide GUI socket thread
    @Override
    protected SocketThread getSocketThread() {
        return new GuiSocketThread();
    }

    // Expose model
    public ClientGuiModel getModel() {
        return model;
    }

    // Start client in current thread
    @Override
    public void run() {
        getSocketThread().run();
    }

    // Entry point
    public static void main(String[] args) {
        new ClientGuiController().run();
    }

    // Get server address via GUI
    @Override
    protected String getServerAddress() {
        return view.getServerAddress();
    }

    // Get server port via GUI
    @Override
    protected int getServerPort() {
        return view.getServerPort();
    }

    // Get user name via GUI
    @Override
    protected String getUserName() {
        return view.getUserName();
    }

    // Send text message
    @Override
    protected void sendTextMessage(String text) {
        super.sendTextMessage(text);
    }

    public class GuiSocketThread extends SocketThread {
        // Display incoming messages
        @Override
        protected void processIncomingMessage(String message) {
            model.setNewMessage(message);
            view.refreshMessages();
        }

        // Handle new user added
        @Override
        protected void informAboutAddingNewUser(String userName) {
            model.addUser(userName);
            view.refreshUsers();
        }

        // Handle user removal
        @Override
        protected void informAboutDeletingNewUser(String userName) {
            model.deleteUser(userName);
            view.refreshUsers();
        }

        // Update GUI connection status
        @Override
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }
}
