package com.garanin.weather.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnectionManager {
    private Connection connection;
    private final String user = "postgres";
    private final String password = "123";

    public DataBaseConnectionManager(String jdbcURL) throws SQLException, ClassNotFoundException {

        Class.forName("org.postgresql.Driver");
        this.connection = DriverManager.getConnection(jdbcURL, user, password);
        connection = null;
        try {
            connection = DriverManager.getConnection(jdbcURL, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
