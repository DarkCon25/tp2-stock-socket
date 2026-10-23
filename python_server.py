import csv
import socket
from dataclasses import dataclass

HOST = "127.0.0.1"
PORT = 5000


@dataclass
class Item:
    id: int
    name: str
    price: float
    quantity: int


def load_items():
    items = []
    with open("data.csv", newline="", encoding="utf-8") as file:
        rows = csv.reader(file)
        next(rows)  # skip the header
        for row in rows:
            items.append(Item(int(row[0]), row[1], float(row[2]), int(row[3])))
    return items


def handle(command, items):
    parts = command.strip().split(" ")
    try:
        if len(parts) == 2 and parts[0] == "name":
            item_id = int(parts[1])
            for item in items:
                if item.id == item_id:
                    return item.name
            return "Item not found"
        if len(parts) == 2 and parts[0] == "quantity":
            item_id = int(parts[1])
            for item in items:
                if item.id == item_id:
                    return str(item.quantity)
            return "Item not found"
        if len(parts) == 3 and parts[0] == "price":
            item_id = int(parts[1])
            quantity = int(parts[2])
            for item in items:
                if item.id == item_id:
                    return f"{item.price * quantity:.2f}"
            return "Item not found"
        if parts == ["end"]:
            return "Goodbye"
        return "Invalid command"
    except ValueError:
        return "Invalid command"


def main():
    items = load_items()
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server:
        server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server.bind((HOST, PORT))
        server.listen()
        print(f"Python server listening on {HOST}:{PORT}")
        while True:
            connection, address = server.accept()
            print("Client connected:", address)
            with connection, connection.makefile("r", encoding="utf-8") as reader:
                for line in reader:
                    answer = handle(line, items)
                    connection.sendall((answer + "\n").encode("utf-8"))
                    if line.strip() == "end":
                        break


if __name__ == "__main__":
    main()
