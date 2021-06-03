package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBStarter {
    private static final String user = "brinpow";
    private static final String password = "qwerty123";
    private static final String jdbcUrl = "jdbc:postgresql://localhost:5432/";
    static Connection conn;
    public static Connection start() throws SQLException {
        conn= DriverManager.getConnection(jdbcUrl, user, password);
        return conn;
    }
}
