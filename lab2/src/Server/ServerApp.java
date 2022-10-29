package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerApp {
    private int port;
    static Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public ServerApp(int port) {
        this.port = port;
    }

    public void run(){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                logger.info("client with port and ip " + clientSocket.getPort() + " "
                        + clientSocket.getInetAddress() + " was connected");
                threadPool.submit(new ReadFileTask(clientSocket));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "socket accept failed", e);
        }

    }
}
