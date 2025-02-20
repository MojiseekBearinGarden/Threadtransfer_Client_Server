import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 12345;
    private static final String FILE_DIRECTORY = "C:/Users/User/Downloads";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected");
                    // Create and start a new thread for handling the client connection
                    new ClientThread(socket).start();
                } catch (IOException ex) {
                    System.out.println("Server exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Inner class to handle client connections in a separate thread
    private static class ClientThread extends Thread {
        private Socket socket;

        public ClientThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (InputStream Server_input = socket.getInputStream();
                 OutputStream Server_output = socket.getOutputStream()) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(Server_input));
                String fileName = reader.readLine().trim();
                System.out.println("Requested file: " + fileName);
                
                File file = new File(FILE_DIRECTORY, fileName);
                
                if (!file.exists()) {
    String errorMessage = "File not found\n";
    Server_output.write(errorMessage.getBytes());
    Server_output.flush();
    System.out.println("File not found sent");
} else {
    // Notify client that the file transfer is starting
    String startMessage = "START\n";
    Server_output.write(startMessage.getBytes());
    Server_output.flush();
    System.out.println("START message sent");

    // Send the file to the client
    try (FileInputStream fileIn = new FileInputStream(file)) {
        byte[] buffer = new byte[1048576];
        int bytesRead;
        while ((bytesRead = fileIn.read(buffer)) != -1) {
            Server_output.write(buffer, 0, bytesRead);
        }
        Server_output.flush();
        System.out.println("File data sent");
    }
}

            } catch (IOException ex) {
                System.out.println("ClientThread exception: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    System.out.println("Failed to close socket: " + ex.getMessage());
                }
            }
        }
    }
}