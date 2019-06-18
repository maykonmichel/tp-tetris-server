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
            int firstShape1 = (int) (Math.random() * 7);
            int firstShape2 = (int) (Math.random() * 7);

            players.get(0).startGame(players.get(1).getName(), firstShape1, firstShape2);
            players.get(1).startGame(players.get(0).getName(), firstShape2, firstShape1);
            System.out.println("2 players connected. Closing server to new connections.");
        } catch (IOException e) {
            System.out.println("Something went wrong accepting connections");
        }
    }

    private static int getPlayerIndex(String name) {
        return players.get(0).getName().equals(name) ? 0 : 1;
    }

    private static Player getOpponent(int index) {
        return players.get((index + 1) % 2);
    }

    static void disconnect(String name) {
        int index = getPlayerIndex(name);
        players.get(index).disconnect();
        players.remove(index);
        run();
    }

    static void triggerIntAction(Player.Action action, String playerName, int payload) {
        System.out.println("Player " + playerName + " " + action.toString() + " " + payload);

        Player opponent = getOpponent(getPlayerIndex(playerName));

        opponent.triggerIntAction(action, payload);
    }

    static void setNextShape(String playerName) {
        System.out.println("Player " + playerName + " request new shape");

        Player opponent = getOpponent(getPlayerIndex(playerName));

        int index = (int) (Math.random() * 7);

        players.get(getPlayerIndex(playerName)).triggerIntAction(Player.Action.NEXT_SHAPE, index);
        opponent.triggerIntAction(Player.Action.OPPONENT_NEXT_SHAPE, index);
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
}