import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketTCPClient {
    private static String nombre;
    public static void main(String[] args) {
        Socket socket = null;

        try {
            socket = new Socket("localhost", 49171);
            System.out.println("(Cliente) Conexión establecida.");


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



            registrarse();
            // Enviar mensajes al servidor
            OutputStream os = socket.getOutputStream();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter a message (type 'END' to exit): ");
                String message = scanner.nextLine();

                if (message.equals("END")) {
                    break;
                }else if(!message.isEmpty()){
                    //Si el cliente no ha escrito "END" ya se contruye el mensaje con su nombre asignado y lo envia
                    message = nombre + ": " + message;
                    os.write((message + "\n").getBytes());
                    os.flush();
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


    public static void registrarse(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Con que nombre desea registrarse en el SUPER CHAT???!!!!");
        nombre = sc.nextLine();
    }
}
