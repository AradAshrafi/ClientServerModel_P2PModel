package ClientServerModel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int PORT = 7654;

    public static void main(String[] args) {

        int count = 0;
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.print("Server started.\nWaiting for a client ... ");
            while (count < 10) {
                Socket client = server.accept();
                count++;
                System.out.println("client accepted!");
                ClientHandler clientHandler = new ClientHandler(client);
                clientHandler.run();
            }
            System.t.print("done.\nClosing server ... ");
        } catch (IOException ex) {
            System.err.println(ex);
        }
        System.out.println("done.");
    }

    private static void parse_commands(String command) {
        String[] parsedCommand = command.split(" ");
        switch (parsedCommand[0]) {
            case ("Add"):

            case ("Subtract"):

            case ("Divide"):

            case ("Multiply"):

            case ("Sin"):

            case ("Cos"):

            case ("Tan"):

            case ("Cot"):


            default:
                System.out.println("Wrong Syntax");
        }
    }

}


class ClientHandler implements Runnable {

    private Socket client;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter printWriter = new PrintWriter(client.getOutputStream(), true);
            String command = bufferedReader.readLine();
            String result = parse_commands(command);

            printWriter.println(result);
            printWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
}