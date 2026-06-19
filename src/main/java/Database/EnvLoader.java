package Database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {
    private static Properties props = null;

    private static void cargar() {
        props = new Properties();
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("Error al cargar .env: " + e.getMessage());
        }
    }

    public static String get(String key) {
        if (props == null) {
            cargar();
        }
        String val = props.getProperty(key);
        if (val == null) {
            val = System.getenv(key);
        }
        return val;
    }
}
