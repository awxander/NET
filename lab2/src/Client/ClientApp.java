package Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ClientApp {

    static Logger logger = Logger.getLogger(ClientApp.class.getName());
    private static final int MAX_FILENAME_LEN = 4096;

    public static void main(String[] args) {

        try {
            LogManager.getLogManager().readConfiguration(
                    ClientApp.class.getResourceAsStream("/client_logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }

        logger.info("start client application");

        if (args.length < 3) {
            System.out.println("not enough arguments");
            logger.log(Level.WARNING, "not enough arguments in console");
            System.exit(1);
        }

        String strFilePath = args[0];
        String host = args[1];
        int port = Integer.parseInt(args[2]);


        try (Socket socket = new Socket(host, port);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
             DataInputStream inputStream = new DataInputStream(socket.getInputStream());) {

            Path filePath = Paths.get(strFilePath);
            long fileSize = Files.size(filePath);
            String fileName = filePath.getFileName().toString();

            if (fileName.getBytes(StandardCharsets.UTF_8).length > MAX_FILENAME_LEN) {
                logger.log(Level.SEVERE, "filename too long");
            }


            outputStream.writeUTF(fileName);
            outputStream.writeLong(fileSize);

            BufferedReader reader = new BufferedReader(new FileReader(strFilePath));
            String str;
            while ((str = reader.readLine()) != null) {
                System.out.println(str);
                outputStream.writeUTF(str);
            }
            String msg = inputStream.readUTF();
            logger.info(msg);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "failed write to server", e);
            System.out.println(e);
        }
    }
}
