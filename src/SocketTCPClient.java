import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketTCPClient {

    public static void main(String[] args) {
        Socket socket = null;

        try {
            socket = new Socket("localhost", 49171);
            System.out.println("(Cliente) Conexión establecida.");

            /*
            // Iniciar el hilo para recibir mensajes del servidor
            Socket finalSocket = socket;
            new Thread(() -> {
                try {
                    InputStream is = finalSocket.getInputStream();
                    Scanner scanner = new Scanner(is);
                    while (scanner.hasNextLine()) {
                        String message = scanner.nextLine();
                        System.out.println("(Cliente) Mensaje del servidor: " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

             */

            // Enviar mensajes al servidor
            OutputStream os = socket.getOutputStream();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter a message (type 'END' to exit): ");
                String message = scanner.nextLine();
                os.write((message + "\n").getBytes());
                os.flush();
                if (message.equals("END")) {
                    break;
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
