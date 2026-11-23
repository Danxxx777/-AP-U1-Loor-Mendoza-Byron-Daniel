import java.net.ServerSocket;

public class Coordinador {
    public static void main(String[] args) throws Exception {
        try (ServerSocket servidor = new ServerSocket(5000)) {
            System.out.println("Esperando la puerta");
            try (var puerta = servidor.accept()) {
            System.out.println("Puerta Conectada");
            }
        }
    }
}
