package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBStarter {
    public static String user = "postgres";
    public static String password = "postgres";
    public static String jdbcUrl = "jdbc:postgresql://localhost:5432/";
    public static Connection conn;
    public static Connection start() throws SQLException {
        conn= DriverManager.getConnection(jdbcUrl, user, password);
        return conn;
    }
}
