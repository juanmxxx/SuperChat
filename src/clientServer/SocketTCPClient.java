package clientServer;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class SocketTCPClient {
    private static String nombre;
    private static BufferedReader br;
    private static InputStream is;


    public static void main(String[] args) {
        Socket socket = null;

        try {
            socket = new Socket("localhost", 49171);
            is = socket.getInputStream();
            System.out.println("(Cliente) Conexión establecida.");


            Socket finalSocket = socket;
            new Thread(() -> {
                while (true) {
                    try {
                        leerMensajes(finalSocket);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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
                    message = horaActual() + " <" + nombre + "> " + message;
                    os.write((message + "\n").getBytes());
                    os.flush();
                    System.out.println("mensaje enviado....");
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

    public static String horaActual(){
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return "[" + time.format(formatter) + "]";
    }

    public static void leerMensajes(Socket socket) throws IOException, InterruptedException {
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String message;
        while ((message = br.readLine()) != null) {
            System.out.println(message);
        }
        br.close();
        socket.close();
    }


    public static void leerMensajesTexto(String rutaArchivo) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(rutaArchivo));

            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
