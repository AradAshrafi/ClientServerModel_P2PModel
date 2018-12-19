package P2P_model;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Peer {
    String ip;
    ArrayList<Peer> peers;
    DatagramSocket datagramSocket = new DatagramSocket(4490);

    int generateRandomPort() {
        return (int) (Math.random() * 62536 + 2000);
    }

    void request(String fileName) throws IOException {
        DatagramSocket udpSocket = new DatagramSocket();
        byte[] reqBuffer = fileName.getBytes();
        for (Peer anPeer : peers) {
            DatagramPacket reqPacket = new DatagramPacket(reqBuffer, reqBuffer.length, InetAddress.getByName(anPeer.ip), generateRandomPort());
            udpSocket.send(reqPacket);
        }
        DatagramPacket reqPacket = new DatagramPacket(fileName.getBytes(), fileName.getBytes().length);
        udpSocket.send(new DatagramPacket());


    }

    void listen() throws SocketException {
        DatagramSocket udpSocket = new DatagramSocket();
        byte[] received = new byte[256];
    }

}
