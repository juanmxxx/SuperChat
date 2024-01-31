import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SocketTCPServer {
//PRUEBA PARA SEGUNDO COMMIT
    static String nameLogFile = "chatLog.txt";
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
                Socket clientSocket = serverSocket.accept();
                logLines = "(Servidor) Nuevo cliente conectado.";
                guardarMensajeTexto(logLines);
                System.out.println(logLines);

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
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String message;
        while ((message = reader.readLine()) != null) {
            System.out.println(message);
            logLines = message;
            guardarMensajeTexto(logLines);
        }
        reader.close();
        clientSocket.close();
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
