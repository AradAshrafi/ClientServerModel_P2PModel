package ClientServerModel;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client extends Thread {

    private static Scanner scanner = new Scanner(System.in);
    private static String ip = "127.0.0.1";
    private static int portNum = 7654;


    public static void main(String[] args) {
        String command = scanner.nextLine();
        Socket socket = new Socket()
    }

}
