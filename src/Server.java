import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int port = 44455;
    private static ServerSocket serverSocket;
    private static List<Player> players;

    private static void start() {
        System.out.println("Starting server...");
        try {
            serverSocket = new ServerSocket(port);
            players = new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Something went wrong on starting server: " + e.getMessage());
            System.exit(1);
        }
        run();
    }

    static void run() {
        try {
            System.out.println("Waiting for connections...");
            while (players.size() < 2) {
                Socket acceptSocket = serverSocket.accept();
                Player player = new Player(acceptSocket);
                players.add(player);
                new Thread(player).start();
            }
            players.get(0).setOpponent(players.get(1).getName());
            players.get(1).setOpponent(players.get(0).getName());
            System.out.println("2 players connected. Closing server to new connections.");
        } catch (IOException e) {
            System.out.println("Something went wrong accepting connections");
        }
    }

    private static int getPlayerIndex(String name) {
        return players.get(0).getName().equals(name) ? 0 : 1;
    }

    static void disconnect(String name) {
        int index = getPlayerIndex(name);
        players.get(index).disconnect();
        players.remove(index);
        run();
    }

    static void triggerAction(String name, int action) {
        System.out.println("Player " + name + " pressed " + action);

        Player opponent = players.get(getPlayerIndex(name));

        if (action == Action.GAME_OVER.getValue() || action == Action.STOP.getValue()) {
            stop();
        }
        opponent.triggerAction(action);
    }

    public static void main(String[] args) {
        Server.start();
    }

    private static void stop() {
        for (Player player : players) {
            player.disconnect();
        }
        players.clear();
        run();
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