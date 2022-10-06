import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static java.lang.Thread.sleep;

public class SendThread implements Runnable {

    private final MulticastSocket multicastSocket;
    private final int port;
    private final InetAddress inetAddress;

    public SendThread(MulticastSocket multicastSocket, InetAddress inetAddress, int port) {
        this.multicastSocket = multicastSocket;
        this.inetAddress = inetAddress;
        this.port = port;
    }

    @Override
    public void run() {

        while (true) {
            String msg = "";
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(),
                    inetAddress, port);
            try {
                multicastSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}

