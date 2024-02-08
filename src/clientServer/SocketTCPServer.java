package clientServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SocketTCPServer {
    private static String nameLogFile = "src\\registros\\chatLog.txt";
    private static ServerSocket serverSocket = null;
    private static ArrayList<OutputStream> clientOutputStreams = new ArrayList<>();

    public static void main(String[] args) {

        updateFileName();
        try {
            serverSocket = new ServerSocket(49171);
            tratamientoMensajes("(Servidor) Esperando conexiones...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); //AQUI LLEGA CUANDO SE CONECTA UN NUEVO CLIENTE Y SE REPITE LA EXPLICACION
                tratamientoMensajes("(Servidor) Nuevo cliente conectado.");

                OutputStream os = clientSocket.getOutputStream();
                clientOutputStreams.add(os);

                /**
                 * Esto es un hilo
                 *
                 * Un hilo para cada cliente, se crea cada vez que nuestro
                 * socket del servidor acepta un nuevo cliente es decir,
                 * iniciamos un nuevo cliente desde la otra clase, lo
                 * instanciamos manualmente nosotros
                 *
                 * Explicacion: un hilo es una instancia que va a parte de
                 * nuestro main y esta se queda escuchando siempre desde el lado
                 * del cliente, como son concurrentes, pues soluciona el impas
                 * de recepcion de mansajes desde distintos clientes, 1 hilo por
                 * cliente
                 */
                new Thread(() -> {
                    try {
                        leerMensajeClientes(clientSocket);
                        cerrarConexion(clientSocket);
                    } catch (IOException | InterruptedException e) {
                        if (e instanceof SocketException) {
                            tratamientoMensajes("(Servidor) Cliente desconectado.");
                        } else {
                            e.printStackTrace();
                        }
                        // En caso de que la excepción no sea una SocketException de "Socket closed",
                        // puedes lanzar una RuntimeException con la excepción original
                    }
                }).start();

                /**
                 * Aqui sigue nuestro main ejecutandose y vuelve al principio
                 * del BUCLE automaticamente, [IR AL PRINCIPIO DEL WHILE]
                 */
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

    public static void guardarMensajeTexto(String mensaje) {

        try {
            FileWriter fw = new FileWriter(nameLogFile, true);
            fw.write("\r\n" + mensaje);
            fw.close();
        } catch (Exception var3) {
            System.out.println(var3);
        }

    }

    public static void leerMensajeClientes(Socket clientSocket) throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        boolean seguirLeyendo = true;
        while (seguirLeyendo) {
            
            String msj = reader.readLine();
            if (msj == null || msj.equals("Desconectame")) {
                seguirLeyendo = false;
            } else 
                tratamientoMensajes(msj);
            
        }
        reader.close();
    }
    
    private static void broadcast(String message) { 
        if(!clientOutputStreams.isEmpty()){
            for (OutputStream os : clientOutputStreams) {
                try {
                    os.write((message + "\n").getBytes());
                    os.flush();
                } catch (IOException e) {}
            }
        }
    }
    
    
    private static void tratamientoMensajes(String message){
        System.out.println(message);
        broadcast(message);
        guardarMensajeTexto(message);
    }
    
    
    public static void cerrarConexion(Socket clientSocket) throws IOException {
        clientOutputStreams.remove(clientSocket.getOutputStream());
    }

    public static void updateFileName() {
        String fechaHoraActual = formatter(LocalDateTime.now());
        String[] nameLogTroceado = nameLogFile.split("\\.");
        nameLogFile = nameLogTroceado[0] + fechaHoraActual + "." + nameLogTroceado[1];
    }

    private static String formatter(LocalDateTime fechaHoraActual) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH-mm-ss");
        // Formatear la fecha y hora actual según el formato
        return fechaHoraActual.format(formatter);
    }
}
