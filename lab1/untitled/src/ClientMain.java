import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientMain {

    private static final int DEFAULT_PORT = 6789;

    public static void main(String[] args) {
        try {
            InetAddress group = InetAddress.getByName(args[0]);
            if (!group.isMulticastAddress()) {
                System.out.println("not a multicast address");
                System.exit(1);
            }
            new UDPDataExchange(group, DEFAULT_PORT).run();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
