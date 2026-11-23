import java.io.*;
import java.net.*;
import java.util.*;

public class Puerta {

    public static String solicitar(
            Properties config,
            int id,
            String orden,
            String token) {

        try (var conexion = new Socket()) {

            int puerto = Integer.parseInt(
                    config.getProperty("puerto"));

            conexion.connect(
                    new InetSocketAddress(
                            config.getProperty("host"),
                            puerto),
                    3000);

            conexion.setSoTimeout(5000);

            var entrada = new BufferedReader(
                    new InputStreamReader(
                            conexion.getInputStream()));

            var salida = new PrintWriter(
                    conexion.getOutputStream(), true);

            salida.println(
                    orden + "," + token + "," + id);

            String respuesta = entrada.readLine();

            return respuesta == null
                    ? "ERROR: respuesta vacia"
                    : respuesta;

        } catch (IOException e) {

            return "ERROR: coordinador no disponible; "
                    + "operacion no confirmada";
        }
    }

    public static void main(String[] args) throws Exception {

        var config = new Properties();

        try (var archivo =
                     new FileInputStream("config.properties")) {
            config.load(archivo);
        }

        int id = args.length == 0
                ? 1
                : Integer.parseInt(args[0]);

        if (id < 1 || id > 3) {
            throw new IllegalArgumentException(
                    "Use Puerta 1, 2 o 3");
        }

        var teclado = new BufferedReader(
                new InputStreamReader(System.in));

        System.out.println("Puerta P" + id);

        System.out.println(
                "Ordenes: entrar, salir, estado, invalido, fin");

        while (true) {

            System.out.print("P" + id + "> ");

            String linea = teclado.readLine();

            if (linea == null) {
                break;
            }

            String orden = linea.trim()
                    .toUpperCase(Locale.ROOT);

            if (orden.equals("FIN")) {
                break;
            }

            if (orden.isBlank()) {
                continue;
            }

            String token = config.getProperty("token");

            if (orden.equals("INVALIDO")) {
                orden = "ESTADO";
                token += "-incorrecto";
            }

            System.out.println(
                    solicitar(config, id, orden, token));
        }
    }
}