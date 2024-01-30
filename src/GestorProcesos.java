import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private void realizarProceso() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String message;
        while ((message = reader.readLine()) != null) {
            System.out.println("(Servidor) " + message);
            if (message.equals("END")) {
                break;
            }
        }

        reader.close();
        socket.close();
    }
}
