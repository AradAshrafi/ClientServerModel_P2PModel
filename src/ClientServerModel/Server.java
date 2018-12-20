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


    private String parse_commands(String command) {
        String[] parsedCommand = command.split(" ");
        int op1 = Integer.parseInt(parsedCommand[1]);
        int op2;
        String result;
        long startTime = System.nanoTime();
        switch (parsedCommand[0]) {
            case ("Add"):
                op2 = Integer.parseInt(parsedCommand[2]);
                result = Integer.toString((op1 + op2));
                break;
            case ("Subtract"):
                op2 = Integer.parseInt(parsedCommand[2]);
                result = Integer.toString((op1 - op2));
                break;
            case ("Divide"):
                op2 = Integer.parseInt(parsedCommand[2]);
                result = Double.toString((op1 * 1.0 / op2));
                break;
            case ("Multiply"):
                op2 = Integer.parseInt(parsedCommand[2]);
                result = Integer.toString((op1 * op2));
                break;
            case ("Sin"):
                result = Double.toString(Math.sin(op1));
                break;
            case ("Cos"):
                result = Double.toString(Math.cos(op1));
                break;
            case ("Tan"):
                result = Double.toString(Math.tan(op1));
                break;
            case ("Cot"):
                result = Double.toString(Math.tan(Math.PI / 2 - op1));
                break;
            default:
                result = "Wrong Syntax";
        }
        long endTime = System.nanoTime();
        long totalTIme = endTime - startTime;
        return "Execution time in milliseconds : " +
                totalTIme * 1.0 / 1000000 + " " + "Result = " + result;
    }
}
