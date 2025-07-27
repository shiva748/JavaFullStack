
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server {

    public Consumer<Socket> consumer() {
        return (socket) -> {
            try {
                PrintWriter toClient = new PrintWriter(socket.getOutputStream(), true);
                toClient.println("Hello from the server!");
                toClient.flush();
                toClient.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("Error handling socket: " + e.getMessage());
            }
        };
    }

    public static void main(String[] args) {
        int port = 8080;
        Server server = new Server();
        try {
            ServerSocket socket = new ServerSocket(port);
            socket.setSoTimeout(10000);
            System.out.println("Server started on port " + port);
            while (true) {
                try {
                    Socket acceptedSocket = socket.accept();
                    Thread thread = new Thread(() -> server.consumer().accept(acceptedSocket));
                    thread.start();
                } catch (java.net.SocketTimeoutException ste) {
                    System.out.println("Socket timed out!");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error starting server on port " + port);
            e.printStackTrace();
        }
    }
}
