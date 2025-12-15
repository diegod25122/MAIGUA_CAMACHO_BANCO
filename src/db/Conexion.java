package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    private static final String URL = "jdbc:mysql://uoq32epw0twk43ae:EE8xm7NMbdaSkylmk3Xt@bxo8ywhzs5wpxavrf9rd-mysql.services.clever-cloud.com:3306/bxo8ywhzs5wpxavrf9rd";
    private static final String USER = "uoq32epw0twk43ae";
    private static final String PASS = "EE8xm7NMbdaSkylmk3Xt";

    public static Connection getConexion() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Error BD: " + e.getMessage());
            return null;
        }
    }
}
