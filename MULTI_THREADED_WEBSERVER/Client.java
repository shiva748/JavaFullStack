import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
public class Client{
    public Runnable getRunnable() {
        return new Runnable(){
            @Override
            public void run() {
                int port = 8080;
                try {
                    InetAddress address = InetAddress.getByName("localhost");
                    Socket socket = new Socket(address, port);
                    try(
                        PrintWriter toSocket = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader fromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                    ){
                        toSocket.println("Hello from the client!");
                        String line = fromSocket.readLine();
                        System.out.println("Response From Server : "+line);
                    }catch(Exception e){
                        System.out.println("Error communicating with server: " + e.getMessage());
                    }
                } catch (Exception e) {
                    System.out.println("Error connecting to server: " + e.getMessage());
                }
            }
        };
    }
    public static void main(String[] args){
        Client client = new Client();
        for(int i = 0; i<100; i++){
            try {
                Thread thread = new Thread(client.getRunnable());
                thread.start();
            } catch (Exception e) {
                System.out.println("Error connecting to server: " + e.getMessage());
            }
        }
    }
}