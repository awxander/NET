import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SendThread extends Thread{

    MulticastSocket multicastSocket;
    InetAddress inetAddress;
    int port;

    public SendThread(MulticastSocket multicastSocket, InetAddress inetAddress, int port) {
        this.multicastSocket = multicastSocket;
        this.inetAddress = inetAddress;
        this.port = port;
    }

    @Override
    public void run() {

        while(true){
            String msg = "";
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(),
                    inetAddress, port);
            try {
                multicastSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            synchronized (Thread.currentThread()){
                try {
                    wait(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
