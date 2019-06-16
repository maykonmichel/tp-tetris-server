import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int port = 44455;
    private static ServerSocket serverSocket;
    private static List<Player> players;

    private Server() {
        System.out.println("Starting server...");
        try {
            serverSocket = new ServerSocket(port);
            players = new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Something went wrong on starting server: " + e.getMessage());
            System.exit(1);
        }
    }

    static void triggerAction(int id, int action) {
        Player opponent = players.get((id + 1) % 2);

        if (action == Action.GAME_OVER.getValue() || action == Action.STOP.getValue()) {
            stop();
        }
        opponent.triggerAction(action);
    }

    public static void main(String[] args) {
        new Server().run();
    }

    private static void stop() {
        for (Player player :
                players) {
            player.disconnect();
        }
    }

    private void run() {
        try {
            System.out.println("Waiting for connections...");
            while (players.size() < 2) {
                Socket acceptSocket = serverSocket.accept();
                players.add(new Player(players.size() + 1, acceptSocket));
            }
            System.out.println("2 players connected. Closing server to new connections.");
            for (Player player : players) {
                new Thread(player).start();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong accepting connections");
        }
    }

    private enum Action {
        GAME_OVER(5),
        STOP(6);

        private int value;

        Action(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}