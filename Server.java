import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Server {
    private static final int PORT = 5000;
    private final Map<Integer, Item> items = new HashMap<>();

    private static class Item {
        String name;
        double price;
        String quantity;
        Item(String name, double price, String quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
    }

    private void loadItems() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("data.csv"), StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            String[] values = lines.get(i).split(",", 4);
            items.put(Integer.parseInt(values[0]), new Item(values[1],
                    Double.parseDouble(values[2]), values[3]));
        }
    }

    private String handle(String command) {
        String[] parts = command.trim().split("\\s+");
        try {
            if (parts.length == 2 && parts[0].equals("name")) {
                Item item = items.get(Integer.parseInt(parts[1]));
                return item == null ? "Item not found" : item.name;
            }
            if (parts.length == 2 && parts[0].equals("quantity")) {
                Item item = items.get(Integer.parseInt(parts[1]));
                return item == null ? "Item not found" : item.quantity;
            }
            if (parts.length == 3 && parts[0].equals("price")) {
                Item item = items.get(Integer.parseInt(parts[1]));
                if (item == null) return "Item not found";
                return String.format(Locale.US, "%.2f", item.price * Integer.parseInt(parts[2]));
            }
            if (parts.length == 1 && parts[0].equals("end")) return "Goodbye";
            return "Invalid command";
        } catch (NumberFormatException e) {
            return "Invalid command";
        }
    }

    private void serve() throws IOException {
        loadItems();
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Java server listening on port " + PORT);
            while (true) {
                try (Socket client = server.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8))) {
                    String command;
                    while ((command = in.readLine()) != null) {
                        out.write(handle(command));
                        out.newLine();
                        out.flush();
                        if (command.trim().equals("end")) break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException { new Server().serve(); }
}
