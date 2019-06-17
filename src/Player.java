import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Runnable {
    private Socket socket;
    private Scanner scanner;
    private PrintStream printStream;
    private boolean running;
    private String name;

    Player(Socket socket) {
        this.socket = socket;
        try {
            scanner = new Scanner(socket.getInputStream());
            printStream = new PrintStream(socket.getOutputStream());
            name = scanner.nextLine();
            System.out.println("Player " + name + " connected.");
        } catch (IOException e) {
            System.out.println("Something went wrong connecting player: " + e.getMessage());
        }
    }

    String getName() {
        return name;
    }

    void disconnect() {
        running = false;
        scanner.close();
        printStream.close();
        try {
            socket.close();
            System.out.println("Player " + name + " disconnected.");
        } catch (IOException e) {
            System.out.println("Something went wrong disconnecting player " + name + ": " + e.getMessage());
        }
    }

    void triggerAction(int action) {
        printStream.println(action);
    }

    @Override
    public void run() {
        running = true;
        while (scanner.hasNextInt()) {
            try {
                Server.triggerAction(name, scanner.nextInt());
            } catch (Exception e) {
                disconnect();
                Server.run();
            }
        }
        Server.disconnect(name);
    }
}
