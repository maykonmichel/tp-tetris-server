import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Runnable {
    private Socket socket;
    private Scanner scanner;
    private PrintStream printStream;
    private boolean running;
    private int id;

    Player(int id, Socket socket) {
        this.id = id;
        this.socket = socket;
        try {
            scanner = new Scanner(socket.getInputStream());
            printStream = new PrintStream(socket.getOutputStream());
            System.out.println("Player " + id + " connected.");
        } catch (IOException e) {
            System.out.println("Something went wrong connecting player " + id + ": " + e.getMessage());
        }
    }

    void disconnect() {
        running = false;
        scanner.close();
        printStream.close();
        try {
            socket.close();
            System.out.println("Player " + id + " disconnected.");
        } catch (IOException e) {
            System.out.println("Something went wrong disconnecting player " + id + ": " + e.getMessage());
        }
    }

    void triggerAction(int action) {
        printStream.println(action);
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            Server.triggerAction(id, scanner.nextInt());
        }
    }
}
