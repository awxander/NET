package UDPstaf;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SendThread extends Thread{

    MulticastSocket multicastSocket;
    InetAddress inetAddress;

    public SendThread(MulticastSocket multicastSocket, InetAddress inetAddress) {
        this.multicastSocket = multicastSocket;
        this.inetAddress = inetAddress;
    }

    @Override
    public void run() {

        while(true){
            String msg = "i am here, retard" + multicastSocket.getLocalAddress().toString();
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(),
                    inetAddress, 8000);
            try {
                multicastSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            synchronized (Thread.currentThread()){
                try {
                    wait(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
