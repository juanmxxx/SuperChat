import java.io.*;
import java.net.Socket;

public class GestorProcesos extends Thread {
    private Socket socket;

    public GestorProcesos(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            realizarProceso();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarMensajeTexto(String mensaje) {
        try {
            FileWriter fw = new FileWriter("chat.txt", true);
            fw.write("\r\n" + mensaje);
            fw.close();
        } catch (Exception var3) {
            System.out.println(var3);
        }

    }

    public void enviarMensajeServidor(String message) throws IOException {
        OutputStream os = socket.getOutputStream();
        os.write((message + "\n").getBytes());
        os.flush();
    }

    private void realizarProceso() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String message;
        if (reader.readLine() != null) {
            message = reader.readLine();
            enviarMensajeServidor(message);
        }
    }
}
