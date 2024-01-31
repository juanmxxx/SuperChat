import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketTCPServer {
//PRUEBA PARA SEGUNDO COMMIT
    static ServerSocket serverSocket = null;
    static BufferedReader reader;
    public static void main(String[] args) {

        try {
            serverSocket = new ServerSocket(49171);
            System.out.println("(Servidor) Esperando conexiones...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("(Servidor) Nuevo cliente conectado.");

                new Thread(() -> {
                    try {
                        leerMensajeClientes(clientSocket);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void abrirLector(Socket clientSocket) throws IOException {
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public static void cerrarLector(Socket clientSocker) throws IOException {

        reader.close();
        clientSocker.close();
    }

    public static void leerMensajeClientes(Socket clientSocket) throws IOException, InterruptedException {
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String message;
        while ((message = reader.readLine()) != null) {
            System.out.println(message);
        }
        reader.close();
        clientSocket.close();
    }
}
