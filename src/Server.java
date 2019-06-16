import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private static final int port = 44455;
    private List<Connection> players;

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

    private void start() {
        try {
            System.out.println("Waiting for connections...");
            while (players.size() < 2) {
                Socket acceptSocket = serverSocket.accept();
                players.add(new Connection(players.size() + 1, acceptSocket));
            }
            System.out.println("2 players connected. Closing server to new connections.");
            for (Connection player : players) {
                new Thread(player).start();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong accepting connections");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}