package Server;

import Client.ClientApp;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ServerMain {

    private static final Logger logger = Logger.getLogger(ServerMain.class.getName());
    private static final int DEFAULT_PORT = 8000;

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(
                    ClientApp.class.getResourceAsStream("/server_logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
        logger.info("server is starting ");
        if (args.length < 1) {
            logger.log(Level.SEVERE, "no port number");
            System.exit(1);
        }

        int port = DEFAULT_PORT;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "wrong port format: " + args[0] + ", needs int");
        }
        new ServerApp(port).run();

    }
}
