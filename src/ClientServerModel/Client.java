package ClientServerModel;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private static Scanner scanner = new Scanner(System.in);
    private static String ip = "127.0.0.1";
    private static int portNum = 7654;


    public static void main(String[] args) {
        try {
            Socket socket = new Socket(ip, portNum);
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("connected");
                String command = scanner.nextLine();
                System.out.println(command);
                printWriter.println(command);
                printWriter.flush();

                String msg = bufferedReader.readLine();
                System.out.println(msg);

            } catch (IOException e) {
                System.out.println("Read or Write Problemً");
            }
        } catch (IOException e) {
            System.out.println("Can't open Socket");
        }
    }

}
