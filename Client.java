import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 5000);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             Scanner scan = new Scanner(System.in)) {
            while (true) {
                System.out.print("Request (name id / quantity id / price id qty / end): ");
                String command = scan.nextLine();
                out.write(command);
                out.newLine();
                out.flush();
                System.out.println("Server response: " + in.readLine());
                if (command.trim().equals("end")) break;
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}
