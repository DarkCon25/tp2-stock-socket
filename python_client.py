import socket

HOST = "127.0.0.1"
PORT = 5000


def main():
    with socket.create_connection((HOST, PORT)) as connection:
        reader = connection.makefile("r", encoding="utf-8")
        while True:
            command = input("Request (name id / quantity id / price id qty / end): ")
            connection.sendall((command + "\n").encode("utf-8"))
            answer = reader.readline()
            if not answer:
                print("Server disconnected")
                break
            print("Server response:", answer.strip())
            if command.strip() == "end":
                break


if __name__ == "__main__":
    main()
