import java.io.*;
import java.net.*;
import java.util.*;

public class Coordinador {

    private static volatile int dentro = 0;
    private static boolean inseguro = false;

    public static void main(String[] args) throws Exception {

        var config = new Properties();

        try (var archivo =
                     new FileInputStream("config.properties")) {
            config.load(archivo);
        }

        int puerto = Integer.parseInt(
                config.getProperty("puerto"));

        inseguro = Arrays.asList(args).contains("--inseguro");

        System.out.println(
                "Modo: " + (inseguro ? "INSEGURO" : "NORMAL"));

        try (var servidor = new ServerSocket(puerto)) {

            System.out.println(
                    "Coordinador listo en puerto " + puerto);

            while (true) {

                Socket puerta = servidor.accept();

                new Thread(() -> {

                    try (puerta) {

                        puerta.setSoTimeout(5000);

                        var entrada = new BufferedReader(
                                new InputStreamReader(
                                        puerta.getInputStream()));

                        var salida = new PrintWriter(
                                puerta.getOutputStream(), true);

                        String mensaje;

                        while ((mensaje =
                                entrada.readLine()) != null) {

                            String respuesta =
                                    procesar(mensaje, config);

                            System.out.println(respuesta);
                            salida.println(respuesta);
                        }

                    } catch (IOException e) {

                        System.out.println(
                                "Error de conexion: "
                                        + e.getMessage());
                    }

                }).start();
            }
        }
    }

    private static String procesar(
            String mensaje, Properties config) {

        if (inseguro) {
            return resolver(mensaje, config);
        }

        synchronized (Coordinador.class) {
            return resolver(mensaje, config);
        }
    }

    private static String resolver(
            String mensaje, Properties config) {

        String[] partes = mensaje.split(",", -1);

        if (partes.length != 3 ||
                !partes[1].equals(
                        config.getProperty("token"))) {

            return "RECHAZADA: mensaje o token invalido";
        }

        int id;

        try {
            id = Integer.parseInt(partes[2]);

        } catch (NumberFormatException e) {
            return "RECHAZADA: identificador invalido";
        }

        if (id < 1 || id > 3) {
            return "RECHAZADA: puerta invalida";
        }

        int aforo = Integer.parseInt(
                config.getProperty("aforo"));

        String orden = partes[0].trim()
                .toUpperCase(Locale.ROOT);

        String resultado;

        if (orden.equals("ENTRAR")) {

            int anterior = dentro;

            if (anterior >= aforo) {
                return "RECHAZADA: aforo completo";
            }

            if (inseguro) {

                try {
                    Thread.sleep(50);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    return "ERROR: operacion interrumpida";
                }
            }

            dentro = anterior + 1;
            resultado = "PERMITIDA: entrada P" + id;

        } else if (orden.equals("SALIR")) {

            if (dentro == 0) {
                return "RECHAZADA: nadie dentro";
            }

            dentro--;
            resultado = "PERMITIDA: salida P" + id;

        } else if (orden.equals("ESTADO")) {

            resultado = "ESTADO: consulta P" + id;

        } else {

            return "RECHAZADA: orden desconocida";
        }

        return resultado
                + " | dentro=" + dentro
                + " | aforo=" + aforo;
    }
}