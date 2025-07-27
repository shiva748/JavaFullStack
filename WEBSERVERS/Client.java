import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public void run() throws Exception {
        int port = 8080;
        InetAddress address = InetAddress.getByName("localhost");
        Socket socket = new Socket(address, port);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // autoFlush = true
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println("First request from Java client");

        String response = in.readLine();
        System.out.println("Response from server: " + response);

        in.close();
        out.close();
        socket.close();
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
