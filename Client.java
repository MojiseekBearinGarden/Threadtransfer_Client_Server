import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             InputStream Client_Input = socket.getInputStream();
             OutputStream Client_Output = socket.getOutputStream()) {

            // Request file from server
            System.out.print("Enter the name of the file to request: ");
            String fileName = userInput.readLine();
           Client_Output.write((fileName + "\n").getBytes());
           Client_Output.flush();

            // Read the initial response from the server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(Client_Input));
            String serverResponse = serverReader.readLine().trim();

            if (serverResponse.equals("File not found")) {
                System.out.println(serverResponse);
                return; // Exit if file is not found
            } else if (serverResponse.equals("START")) {
                System.out.println("Receiving file...");

                // Receive file from server
                try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                    byte[] buffer = new byte[1048576];
                    int bytesRead;
                    while ((bytesRead = Client_Input.read(buffer)) != -1) {
                        fileOut.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("File received successfully");
            } 

        } catch (IOException ex) {
            System.out.println("Client exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}