import java.io.*;
import java.net.Socket;

public class Puerta {
    public static void main(String[] args) throws Exception {
        try (Socket conexion = new Socket("localhost", 5000)) {
            System.out.println("Conectada al coordinador");
            var entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            var salida = new PrintWriter(conexion.getOutputStream(),true);
            salida.println("Estado");
            System.out.println("Respuesta: "+ entrada.readLine());
        }
    }
}
