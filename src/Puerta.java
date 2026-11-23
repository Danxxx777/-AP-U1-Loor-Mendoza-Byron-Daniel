import java.util.Locale;
import java.util.Properties;
import java.io.*;
import java.net.Socket;

public class Puerta {
    public static void main(String[] args) throws Exception {

        var config = new Properties();
        try (var archivo = new FileInputStream("config.properties")){
            config.load(archivo);
        }
        int puerto = Integer.parseInt(config.getProperty("puerto"));

        try (Socket conexion = new Socket(config.getProperty("host"),puerto)) {
            System.out.println("Conectada al coordinador");
            var entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            var salida = new PrintWriter(conexion.getOutputStream(),true);
            var teclado = new BufferedReader(new InputStreamReader(System.in));
            String orden = teclado.readLine().trim().toUpperCase();
            salida.println(orden + "," + config.getProperty("token"));
            System.out.println("Respuesta: "+ entrada.readLine());
        }
    }
}