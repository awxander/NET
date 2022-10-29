package Server;

import Client.ClientApp;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class ReadFileTask implements Runnable {

    private static final String UPLOAD_DIR = "./uploads";
    private final int TIME_GAP_MILLIS = 3000;
    static Logger logger = Logger.getLogger(ReadFileTask.class.getName());

    private final Socket clientSocket;
    private Long readBytesAmount = 0L;
    private Long lastGapReadBytesAmount = 0L;
    private Long readSpeed = 1L;

    public ReadFileTask(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());) {
            String fileName = inputStream.readUTF();
            logger.info("successfully read filename");
            long fileSize = inputStream.readLong();
            logger.info("successfully read filesize");

            Path confDir = Paths.get(UPLOAD_DIR);
            if (Files.notExists(confDir)) {
                try {
                    Files.createDirectory(confDir);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "failed create uploads directory", e);
                    e.printStackTrace();
                }
            }

            String strFilePath = UPLOAD_DIR + "/" + fileName;
            Thread countSpeedThread = new Thread(() -> {
                while (!clientSocket.isClosed()) {
                    lastGapReadBytesAmount = 0L;
                    try {
                        sleep(TIME_GAP_MILLIS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    readSpeed = lastGapReadBytesAmount / (TIME_GAP_MILLIS / 1000);
                    if (readSpeed == 0) {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "failed client socket close", e);
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    System.out.println(readSpeed);
                }
            });
            countSpeedThread.start();

            try (FileWriter myWriter = new FileWriter(strFilePath);) {
                while (!clientSocket.isClosed() && readBytesAmount < fileSize) {
                    String str = inputStream.readUTF();
                    readBytesAmount += str.getBytes().length;
                    lastGapReadBytesAmount += str.getBytes().length;
                    myWriter.write(str);
                }
            }
            Path filePath = Paths.get(strFilePath);
            long realFileSize = Files.size(filePath);
            if (realFileSize != fileSize) {
                outputStream.writeUTF("brother, we have problem...");
                logger.log(Level.WARNING, "wrong file size");
            } else {
                outputStream.writeUTF("yea, we cool");
                logger.info("file successfully read ");
            }
            try {
                countSpeedThread.join();
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "failed join countSpeedThread", e);
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "failed reading file", e);
            throw new RuntimeException(e);
        }

    }

}
