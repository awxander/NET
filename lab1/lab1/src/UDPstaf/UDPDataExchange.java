package UDPstaf;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class UDPDataExchange {

    private static List<InetAddress> addressList;

    public static void main(String[] args) {

        try (/*MulticastSocket sendSocket = new MulticastSocket(6789);*/
             MulticastSocket receiveSocket = new MulticastSocket(6789);) {
            InetAddress group = InetAddress.getByName("230.0.0.0");
            receiveSocket.joinGroup(group);

//            new SendThread(sendSocket, group).start();

            while(true){
            byte[] buf = new byte[1000];
            DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
            receiveSocket.receive(receivedPacket);
            InetAddress receivedPacketAddress = receivedPacket.getAddress();
                System.out.println(Arrays.toString(buf));
            }

        }catch (IOException e){
            e.printStackTrace();
        }


    }
}
