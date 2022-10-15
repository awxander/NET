import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class UDPDataExchange {

    private final int port;
    private final InetAddress group;
    private final int TTL = 2000;

    private final int TIMEOUT = 200;

    public UDPDataExchange(InetAddress group, int port){
        this.port = port;
        this.group = group;
    }

    private  final HashMap<String, Long> addressMap = new HashMap<>();

    private  void printAliveAddresses(){
        System.out.println("current addresses amount is: " + addressMap.size());
        for(Map.Entry<String, Long> entry : addressMap.entrySet()) {
            String key = entry.getKey();
            System.out.println(key);
        }
    }

    private  void removeRottenAddresses(){
        for (Iterator<Map.Entry<String, Long>> it = addressMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Long> entry = it.next();
            if (System.currentTimeMillis() - entry.getValue() > TTL){
                System.out.println("user with ip and port " + entry.getKey() + " was disconnected");
                it.remove();
                printAliveAddresses();
            }
        }
    }

    public void run() {
        try (MulticastSocket sendSocket = new MulticastSocket();
             MulticastSocket receiveSocket = new MulticastSocket(port);) {

            receiveSocket.joinGroup(group);
            receiveSocket.setSoTimeout(TIMEOUT);

            new Thread(new SendThread(sendSocket, group, port)).start();

            while (true) {
                byte[] buf = new byte[512];
                DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
                try {
                    receiveSocket.receive(receivedPacket);
                    InetAddress receivedPacketAddress = receivedPacket.getAddress();
                    int packetPort = receivedPacket.getPort();
                    String key = receivedPacketAddress + "; " + packetPort;
                    if(!addressMap.containsKey(key)){
                        System.out.println("new user added, ip and port are: " + key);
                        addressMap.put(key, System.currentTimeMillis());
                        printAliveAddresses();
                    }else{
                        addressMap.put(key, System.currentTimeMillis());
                    }
                } catch (IOException e) {
                }
                removeRottenAddresses();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}