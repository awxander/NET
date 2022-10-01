

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class UDPDataExchange {

    private static final int TTL = 2000;
    private static HashMap<String, Long> addressMap = new HashMap<>();

    private static void removeRottenAdresses(){
        for (Iterator<Map.Entry<String, Long>> it = addressMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Long> entry = it.next();
            if (System.currentTimeMillis() - entry.getValue() > TTL){
                System.out.println("user with ip and port " + entry.getKey() + " was disconnected");
                it.remove();
            }
        }
    }

    public static void main(String[] args) {
        int port = 6789;
        try (MulticastSocket sendSocket = new MulticastSocket();
             MulticastSocket receiveSocket = new MulticastSocket(port);) {
            InetAddress group = InetAddress.getByName(args[0]);
            if (!group.isMulticastAddress()) {
                System.out.println("not a multicast address");
                System.exit(1);
            }

            receiveSocket.joinGroup(group);

            new SendThread(sendSocket, group, port).start();
            while (true) {
                byte[] buf = new byte[512];
                DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
                receiveSocket.receive(receivedPacket);
                InetAddress receivedPacketAddress = receivedPacket.getAddress();
                int packetPort = receivedPacket.getPort();
                String key = receivedPacketAddress + "; " + packetPort;
                if(!addressMap.containsKey(key)){
                    System.out.println("new user added, ip and port are: " + key);
                }
                addressMap.put(key, System.currentTimeMillis());
                removeRottenAdresses();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
