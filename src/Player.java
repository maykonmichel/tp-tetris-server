import javax.swing.*;
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

    void triggerIntAction(Action action, int payload) {
        printStream.println(action);
        printStream.println(payload);
    }

    void startGame(String name, int firstShape, int firstShapeOpponent) {
        System.out.println(name + firstShape + firstShapeOpponent);

        printStream.println(Action.OPPONENT_MATCHED.toString());
        printStream.println(name);
        printStream.println(firstShape);
        printStream.println(firstShapeOpponent);
    }

    @Override
    public void run() {
        running = true;
        while (scanner.hasNext()) {
            try {
                String action = "";
                while(action.isEmpty()) action = scanner.nextLine();
                switch (action) {
                    case "GET_NEXT_SHAPE":
                        Server.setNextShape(name);
                        break;
                    case "KEY_PRESSED":
                        Server.triggerIntAction(Action.KEY_PRESSED, name, scanner.nextInt());
                        break;
                    case "STOP":
                        break;
                    default:
                        System.out.println(action + "from " + name + " not recognized");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Something went wrong: " + e.getMessage());
                disconnect();
                Server.run();
            }
        }
        Server.disconnect(name);
    }

    enum Action {
        NEXT_SHAPE,
        OPPONENT_NEXT_SHAPE,
        KEY_PRESSED,
        OPPONENT_MATCHED
    }
}
