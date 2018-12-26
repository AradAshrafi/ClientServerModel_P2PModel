package P2P_model;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Peer {
    //common
    private final byte ACK_MESSAGE = 1;
    private final byte FILE_MESSAGE = 2;
    private final String MULTICAST_IP = "230.0.0.10";
    private final int MULTICAST_PORT = 20000;
    private final int PACKET_SIZE = 63 * 1024;

    //requester
    private String requestState;
    private int totalRound = -1;
    private int totalIndex;

    //sender
    private int packetNukber;
    private String downloadsDirectoryPath;
    ArrayList<File> files;

    boolean sendMode;

    public Peer(String sharedDirectoryPath, String downloadsDirectoryPath) {
        files = new ArrayList<>();
        File file = new File(sharedDirectoryPath);
        this.downloadsDirectoryPath = downloadsDirectoryPath;
        for (File anFile : file.listFiles()) {
            files.add(anFile);
        }
    }

    private void request(String fileName) throws IOException, SocketException {
        requestState = "RECEIVING_ACK";
        FileOutputStream fileOutputStream = null;
        int currentRound = -1;
        DatagramSocket udpSocket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(MULTICAST_IP);
        byte[] reqBuffer = fileName.getBytes();
        DatagramPacket reqPacket = new DatagramPacket(reqBuffer, reqBuffer.length, group, MULTICAST_PORT);
        System.out.println("local port: " + udpSocket.getLocalPort());
        System.out.println("port: " + udpSocket.getInetAddress());
        udpSocket.send(reqPacket);
        a:
        while (true) {
            byte[] received = new byte[PACKET_SIZE + 2];
            DatagramPacket packet = new DatagramPacket(received, received.length);
            udpSocket.setSoTimeout(3000);
            udpSocket.receive(packet);
            switch (requestState) {
                case "RECEIVING_ACK":
                    if (packet.getData() == null) {
                        System.out.println("no one has your file :(");
                        break a;
                    } else if (getMessageType(packet) == ACK_MESSAGE) {
                        Long fileSize = Long.parseLong(new String(packet.getData(), 1, packet.getLength() - 1).trim());
                        System.out.println("filesize: " + fileSize);
                        setTransferAtts(fileSize);
                        System.out.println("round: " + totalRound);
                        fileOutputStream = new FileOutputStream(new File(downloadsDirectoryPath + "/" + fileName), true);
                        requestState = "RECEIVING_FILE";
                    }
                    break;
                case "RECEIVING_FILE":
                    System.out.println("round: " + currentRound);
                    if (getMessageType(packet) == FILE_MESSAGE) {
                        if (getPacketIndex(packet) == 0)
                            currentRound++;
                        fileOutputStream.write(packet.getData(), 2, packet.getLength() - 2);
                        System.out.println("packetIndex:  " + getPacketIndex(packet));
                        if (currentRound == totalRound && getPacketIndex(packet) == totalIndex) {
                            fileOutputStream.close();
                            System.out.println("file downloaded!!!! :)))");
                            break a;
                        }
                    }
            }
        }
        udpSocket.close();
    }

    int getPacketIndex(DatagramPacket packet) {
        return packet.getData()[1] + 128;
    }

    void readFile(File file) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);


    }

    private byte getMessageType(DatagramPacket packet) {
        return packet.getData()[0];
    }

    private int setTransferAtts(Long fileSize) {
        int numberOfPackets = (int) Math.ceil(fileSize * 1.0 / PACKET_SIZE);
        totalRound = numberOfPackets / 256;
        totalIndex = (numberOfPackets - 1) % 256;
        return numberOfPackets;
    }


    private void listen() throws UnknownHostException, IOException, InterruptedException {
        FileInputStream fileInputStream = null;
        MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
        joinToMaultiCastIp(multicastSocket);
        byte[] received = new byte[128];
        DatagramPacket receivedPacket = new DatagramPacket(received, received.length);
        multicastSocket.receive(receivedPacket);
        String receivedString = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
        System.out.println(receivedString);
        File foundedFile = search(receivedString.trim());
        int numberOfPackets = 0;
        if (foundedFile != null) {
            DatagramSocket datagramSocket = new DatagramSocket();
            setTransferAtts(foundedFile.length());
            sendAck(receivedPacket.getAddress(), receivedPacket.getPort(), foundedFile.length());
            fileInputStream = new FileInputStream(foundedFile);
            numberOfPackets = setTransferAtts(foundedFile.length());
            System.out.println("noPP: " + numberOfPackets);
            TimeUnit.SECONDS.sleep(1);
            for (int i = 0; i < numberOfPackets; i++) {
                byte[] sendingBytes = new byte[PACKET_SIZE + 2];
                sendingBytes[0] = FILE_MESSAGE;
                System.out.println("index: " + i % 256);
                sendingBytes[1] = (byte) (i % 256 - 128);
//                System.out.println("sendingbyte: " + sendingBytes[1]);
                int howManyRead = fileInputStream.read(sendingBytes, 2, PACKET_SIZE);
                DatagramPacket datagramPacket = new DatagramPacket(sendingBytes, 2 + howManyRead, receivedPacket.getAddress(), receivedPacket.getPort());
                datagramSocket.send(datagramPacket);
                System.out.println("senttttt");
            }
            System.out.println("sending done");
            fileInputStream.close();
            datagramSocket.close();
        }
        multicastSocket.leaveGroup(InetAddress.getByName(MULTICAST_IP));
        multicastSocket.close();
    }

    void joinToMaultiCastIp(MulticastSocket multicastSocket) throws IOException {
        InetAddress group = InetAddress.getByName(MULTICAST_IP);
        multicastSocket.joinGroup(group);
    }

    private void sendAck(InetAddress srcIp, int srcPort, Long length) throws SocketException, IOException {
        String message = length.toString();
        byte[] messageBytes = new byte[message.getBytes().length + 1];
        messageBytes[0] = ACK_MESSAGE;
        byte[] messageGetBytes = message.getBytes();
        for (int i = 1; i < messageBytes.length; i++) {
            messageBytes[i] = messageGetBytes[i - 1];
        }
        DatagramSocket datagramSocket = new DatagramSocket();
        DatagramPacket datagramPacket = new DatagramPacket(messageBytes, messageBytes.length, srcIp, srcPort);
        datagramSocket.send(datagramPacket);
    }

    private File search(String fileName) {
        for (File anFile : files) {
            if (fileName.equals(anFile.getName())) {
                System.out.println("I found your file");
                return anFile;
            }
        }
        System.out.println("i cant find!!");
        return null;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        //todo:create app directory
        Peer peer = new Peer("/home/milad/Downloads/p2pApp/shared", "/home/milad/Downloads/p2pApp/downloads");
        Scanner scanner = new Scanner(System.in);
        int status = scanner.nextInt();
        if (status == 1) {
            peer.request("arad.txt");
        } else {
            peer.listen();
        }
    }
}