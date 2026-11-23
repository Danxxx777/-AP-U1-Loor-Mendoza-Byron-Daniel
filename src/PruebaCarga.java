import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class PruebaCarga {

    public static void main(String[] args) throws Exception {

        var config = new Properties();

        try (var archivo =
                     new FileInputStream("config.properties")) {
            config.load(archivo);
        }

        String host = config.getProperty("host");
        int puerto = Integer.parseInt(
                config.getProperty("puerto"));
        String token = config.getProperty("token");

        var ejecutor = Executors.newFixedThreadPool(30);

        var preparados = new CountDownLatch(30);
        var inicio = new CountDownLatch(1);

        List<Future<String>> resultados = new ArrayList<>();

        try {
            for (int i = 0; i < 30; i++) {

                final int solicitud = i + 1;

                resultados.add(ejecutor.submit(() -> {

                    preparados.countDown();
                    inicio.await();

                    try (Socket conexion =
                                 new Socket(host, puerto)) {

                        conexion.setSoTimeout(5000);

                        var entrada = new BufferedReader(
                                new InputStreamReader(
                                        conexion.getInputStream()));

                        var salida = new PrintWriter(
                                conexion.getOutputStream(), true);

                        salida.println("ENTRAR," + token);

                        String respuesta = entrada.readLine();

                        if (respuesta == null) {
                            return "ERROR: respuesta vacia";
                        }

                        System.out.println(
                                solicitud + ": " + respuesta);

                        return respuesta;

                    } catch (IOException e) {
                        return "ERROR: " + e.getMessage();
                    }
                }));
            }

            if (!preparados.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException(
                        "No se prepararon los 30 hilos");
            }

            System.out.println(
                    "Lanzando 30 solicitudes simultaneas...");

            inicio.countDown();

            int permitidas = 0;
            int rechazadas = 0;
            int errores = 0;

            for (Future<String> resultado : resultados) {

                String respuesta = resultado.get();

                if (respuesta.startsWith("OK:")) {
                    permitidas++;
                } else if (respuesta.startsWith("ERROR:")) {
                    errores++;
                    System.out.println(respuesta);
                } else {
                    rechazadas++;
                }
            }

            System.out.println("\nRESUMEN");
            System.out.println("Permitidas: " + permitidas);
            System.out.println("Rechazadas: " + rechazadas);
            System.out.println("Errores: " + errores);

            try (Socket conexion = new Socket(host, puerto)) {

                conexion.setSoTimeout(5000);

                var entrada = new BufferedReader(
                        new InputStreamReader(
                                conexion.getInputStream()));

                var salida = new PrintWriter(
                        conexion.getOutputStream(), true);

                salida.println("ESTADO," + token);

                System.out.println(
                        "Estado final: " + entrada.readLine());
            }

        } finally {
            inicio.countDown();
            ejecutor.shutdownNow();
        }
    }
}