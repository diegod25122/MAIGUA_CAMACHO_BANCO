package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/banco_db";
    private static final String USER = "root";
    private static final String PASS = "root"; // cambia esto

    public static Connection getConexion() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Error BD: " + e.getMessage());
            return null;
        }
    }
}
