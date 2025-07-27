import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {
    public void run() throws Exception {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(10000);

        System.out.println("Server started. Waiting for connections on port " + port + "...");

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established with " + clientSocket.getRemoteSocketAddress());

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // autoFlush true

                String request = in.readLine();
                System.out.println("Received from client: " + request);

                out.println("First response from Java server");

                in.close();
                out.close();
                clientSocket.close();
            } catch (SocketTimeoutException e) {
                System.out.println("Socket timed out waiting for a connection.");
                break;
            } catch (IOException e) {
                System.out.println("I/O error: " + e.getMessage());
            }
        }

        serverSocket.close();
        System.out.println("Server stopped.");
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
