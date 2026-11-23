import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class PruebaCarga {

    public static void main(String[] args) throws Exception {

        var config = new Properties();

        try (var archivo =
                     new FileInputStream("config.properties")) {
            config.load(archivo);
        }

        String token = config.getProperty("token");

        String inicial = Puerta.solicitar(
                config, 1, "ESTADO", token);

        if (!inicial.startsWith("ESTADO:") ||
                !inicial.contains(" | dentro=0 |")) {

            System.out.println(
                    "Reinicia Coordinador para empezar en cero");

            System.out.println(inicial);
            return;
        }

        var ejecutor = Executors.newFixedThreadPool(30);

        var preparados = new CountDownLatch(30);
        var inicio = new CountDownLatch(1);

        List<Future<String>> tareas = new ArrayList<>();

        try {

            for (int i = 0; i < 30; i++) {

                final int puerta = i / 10 + 1;

                tareas.add(ejecutor.submit(() -> {

                    preparados.countDown();
                    inicio.await();

                    return Puerta.solicitar(
                            config,
                            puerta,
                            "ENTRAR",
                            token);
                }));
            }

            if (!preparados.await(5, TimeUnit.SECONDS)) {

                throw new IllegalStateException(
                        "No se prepararon los hilos");
            }

            System.out.println(
                    "Inicio: 10 solicitudes por puerta");

            inicio.countDown();

            int permitidas = 0;
            int rechazadas = 0;
            int errores = 0;

            for (int i = 0; i < tareas.size(); i++) {

                String respuesta = tareas.get(i).get();

                System.out.println(
                        "P" + (i / 10 + 1)
                                + ": " + respuesta);

                if (respuesta.startsWith("PERMITIDA:")) {

                    permitidas++;

                } else if (
                        respuesta.startsWith("RECHAZADA:")) {

                    rechazadas++;

                } else {

                    errores++;
                }
            }

            System.out.println(
                    "Permitidas: " + permitidas);

            System.out.println(
                    "Rechazadas: " + rechazadas);

            System.out.println(
                    "Errores: " + errores);

            System.out.println(
                    Puerta.solicitar(
                            config, 1, "ESTADO", token));

        } finally {

            inicio.countDown();
            ejecutor.shutdownNow();
        }
    }
}