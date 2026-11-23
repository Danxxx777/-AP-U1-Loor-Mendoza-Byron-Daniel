import java.util.Properties;
import java.io.*;
import java.net.ServerSocket;

public class Coordinador {

    private static int dentro = 0;

    public static void main(String[] args) throws Exception {

        var config = new Properties();
        try (var archivo = new FileInputStream("config.properties")) {
            config.load(archivo);
        }
        int puerto = Integer.parseInt(config.getProperty("puerto"));

        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Esperando la puerta");
            while (true) {
                var puerta = servidor.accept();

                Thread hilo = new Thread(() -> {
                    try (puerta) {
                        var entrada = new BufferedReader(
                                new InputStreamReader(puerta.getInputStream()));

                        var salida = new PrintWriter(
                                puerta.getOutputStream(), true);

                        String mensaje = entrada.readLine();
                        String respuesta = procesar(mensaje, config);

                        System.out.println(mensaje + " -> " + respuesta);
                        salida.println(respuesta);

                    } catch (IOException e) {
                        System.out.println("Error con una puerta: "
                                + e.getMessage());
                    }
                });

                hilo.start();
            }
        }
    }
    private static synchronized String procesar(String mensaje, Properties config) {
        if (mensaje == null)
            return "Rechazada: Mensaje vacio";
        String[] partes = mensaje.split(",", -1);

        //System.out.println("Partes: " + partes.length);
        //System.out.println("Token Config: [" + config.getProperty("token") + "]");

        if (partes.length !=2 || !partes[1].equals(config.getProperty("token"))){
            return "Rechazada: Token Invalido";
        }

        int aforo = Integer.parseInt(config.getProperty("aforo"));
        String orden = partes[0].trim().toUpperCase();

        if (orden.equals("ENTRAR")){
            if(dentro >= aforo)
                return "Aforo Completo";
            dentro++;
        } else if (orden.equals("SALIR")) {
            if(dentro==0)
                return "Rechazada: Nadie dentro";
            dentro--;
        } else if (!orden.equals("ESTADO")) {
            return "Rechazada: Orden desconocida";
        }
        return "OK: Dentro " + dentro + ", aforo " + aforo;
    }
}