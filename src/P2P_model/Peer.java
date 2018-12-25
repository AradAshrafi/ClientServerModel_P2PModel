package P2P_model;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Peer {
    private final String multiCastIp = "230.0.0.10";
    private final int multiCastPort = 20000;
    boolean sendMode;
    ArrayList<File> files;

    public Peer(String sharedDirectoryPath) {
        files = new ArrayList<>();
        File file = new File(sharedDirectoryPath);
        for (File anFile : file.listFiles()) {
            files.add(anFile);
        }
    }

    private void request(String fileName) throws UnknownHostException {
        DatagramSocket udpSocket = null;
        try {
            udpSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress group = InetAddress.getByName(multiCastIp);
        byte[] reqBuffer = fileName.getBytes();
        DatagramPacket reqPacket = new DatagramPacket(reqBuffer, reqBuffer.length, group, multiCastPort);
        System.out.println("local port: " + udpSocket.getLocalPort());
        System.out.println("port: " + udpSocket.getInetAddress());
        try {
            udpSocket.send(reqPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        udpSocket.close();
        System.out.println("baste shod? : " + udpSocket.isClosed());

//        udpSocket.setSoTimeout(2000);
//        udpSocket.re ceive();
    }

    private void listen() throws UnknownHostException {
        MulticastSocket multicastSocket = null;
        try {
            multicastSocket = new MulticastSocket(multiCastPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InetAddress group = InetAddress.getByName(multiCastIp);
        try {
            multicastSocket.joinGroup(group);
            System.out.println("multicast socket ??" + multicastSocket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] received = new byte[128];
        DatagramPacket receivedPacket = new DatagramPacket(received, received.length);
        try {
            multicastSocket.receive(receivedPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String receivedString = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
        System.out.println(receivedString);
        File foundedFile = search(receivedString.trim());
        if (foundedFile != null)
            sendAck(receivedPacket.getAddress(), receivedPacket.getPort(), foundedFile.length());
        try {
            multicastSocket.leaveGroup(InetAddress.getByName(multiCastIp));
        } catch (IOException e) {
            e.printStackTrace();
        }
        multicastSocket.close();
    }

    private void sendAck(InetAddress srcIp, int srcPort, Long length) {
        String message = length.toString();
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, srcIp, srcPort);
        try {
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    private File search(String fileName) {
        for (File anFile : files) {
            if (fileName.equals(anFile.getName())){
                System.out.println("I found your file");
                return anFile;
            }
        }
        System.out.println("i can find!!");
        return null;
    }

    public static void main(String[] args) {
        Peer peer = new Peer("/home/milad/Downloads/shared1");
        Scanner scanner = new Scanner(System.in);
        int status = scanner.nextInt();
        if (status == 1) {
            try {
                peer.request("code.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                peer.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}