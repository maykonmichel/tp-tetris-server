import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Connection implements Runnable {
    private Socket socket;
    private Scanner scanner;
    private PrintStream printStream;
    private Player player;
    private Status status;

    private enum Status {
        STARTED,
        WON,
        LOST
    }

    Connection(int id, Socket socket) {
        this.socket = socket;
        try {
            scanner = new Scanner(socket.getInputStream());
            printStream = new PrintStream(socket.getOutputStream());
            System.out.println("Player #" + id + " connected.");
            player = new Player(id);
        } catch (IOException e) {
            System.out.println("Something went wrong connecting player: " + e.getMessage());
        }
    }

    @Override
    public void run() {
    }
}
