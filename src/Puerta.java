import java.net.Socket;

public class Puerta {
    public static void main(String[] args) throws Exception {
        try (Socket conexion = new Socket("localhost", 5000)) {
            System.out.println("Conectada al coordinador");
        }
    }
}
