import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SocketTCPServer {
//PRUEBA PARA SEGUNDO COMMIT
    static String nameLogFile = "LOGS\\chatLog.txt";
    static ServerSocket serverSocket = null;
    static BufferedReader reader;
    static String logLines;
    public static void main(String[] args) {
        updateFileName();
        try {
            serverSocket = new ServerSocket(49171);
            logLines = "(Servidor) Esperando conexiones...";
            guardarMensajeTexto(logLines);
            System.out.println(logLines);

            while (true) {
                Socket clientSocket = serverSocket.accept(); //AQUI LLEGA CUANDO SE CONECTA UN NUEVO CLIENTE Y SE REPITE LA EXPLICACION
                logLines = "(Servidor) Nuevo cliente conectado.";
                guardarMensajeTexto(logLines);
                System.out.println(logLines);

                /**
                 * Esto es un hilo
                 *
                 * Un hilo para cada cliente, se crea cada vez que nuestro socket del servidor acepta un nuevo cliente
                 * es decir, iniciamos un nuevo cliente desde la otra clase, lo instanciamos manualmente nosotros
                 *
                 * Explicacion: un hilo es una instancia que va a parte de nuestro main y esta se queda escuchando siempre
                 * desde el lado del cliente, como son concurrentes, pues soluciona el impas de recepcion de mansajes desde
                 * distintos clientes, 1 hilo por cliente
                 */
                new Thread(() -> {
                    try {
                        leerMensajeClientes(clientSocket);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                /**
                 * Aqui sigue nuestro main ejecutandose y vuelve al principio del BUCLE automaticamente, [IR AL PRINCIPIO DEL WHILE]
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
        /*
        try {
            FileWriter fw = new FileWriter(nameLogFile, true);
            fw.write("\r\n" + mensaje);
            fw.close();
        } catch (Exception var3) {
            System.out.println(var3);
        }

         */
//DESCOMENTAR, IMPORTANTE
    }

    public static void leerMensajeClientes(Socket clientSocket) throws IOException, InterruptedException {
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        while ((logLines = reader.readLine()) != null) {
            System.out.println(logLines);
            guardarMensajeTexto(logLines);
            /**
             * IMPORTANTE HACER AQUI EL ENVIO DEL MENSAJE YA QUE SI LO HACEMOS FUERA EL WHILE QUE LO ENVUELVE
             * ESTA SIEMPRE DANDO VUELTAS HASTA QUE NO HAY NINGUN MENSAJE POR PARTE DE CLIENTE, ENTONCES HASTA QUE EL
             * CLIENTE NO DEJE DE ENVIAR MENSAJES AL SERVIDOR ESTOS MENSAJES NO SE ENVIARIAN AL CLIENTE
             */
            sendMessage(clientSocket);
        }
        reader.close();
    }

    public static void sendMessage(Socket clientSocket) throws IOException {
        OutputStream os = clientSocket.getOutputStream();
        os.write((logLines + "\n").getBytes());
        os.flush();
    }

    public static void updateFileName(){
        String fechaHoraActual = formatter(LocalDateTime.now());

        String []nameLogTroceado = nameLogFile.split("\\.");

        nameLogFile = nameLogTroceado[0] + fechaHoraActual + "." + nameLogTroceado[1];
    }

    private static String formatter(LocalDateTime fechaHoraActual){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH-mm-ss");

        // Formatear la fecha y hora actual según el formato
        return fechaHoraActual.format(formatter);
    }
}
