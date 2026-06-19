package org.mailgrupo02.sistema.conexion;

import Database.EnvLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static Conexion instancia;
    private Connection connection;
    private String url = EnvLoader.get("DB_URL");
    private String usuario = EnvLoader.get("DB_USER");
    private String contraseña = EnvLoader.get("DB_PASSWORD");

    private Conexion() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, usuario, contraseña);
        } catch (ClassNotFoundException ex) {
            throw new SQLException(ex);
        }
    }

    public static Conexion getInstancia() throws SQLException {
        if (instancia == null) {
            instancia = new Conexion();
        } else if (instancia.getConnection().isClosed()) {
            instancia = new Conexion();
        }

        return instancia;
    }

    // Alias para compatibilidad con código existente
    public static Conexion getInstance() throws SQLException {
        return getInstancia();
    }

    // Método estático para obtener conexión directamente
    public static Connection conectar() throws SQLException {
        return getInstancia().getConnection();
    }

    public Connection getConnection() {
        return connection;
    }
}