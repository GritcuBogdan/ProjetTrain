package com.uca.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class ConnectionPool {

    private final static int INITIAL_POOL_SIZE = 10;
    private final static String URL = "jdbc:postgresql://localhost/train";
    private final static String USER = "postgres";
    private final static String PASSWORD = "postgres";

    private static List<Connection> connectionPool = new ArrayList<>(INITIAL_POOL_SIZE);
    private static List<Connection> usedConnections = new ArrayList<>();

    
    public static void init() {
        try {
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                connectionPool.add(createConnection(URL, USER, PASSWORD));
            }
        } catch (SQLException e) {
            System.err.println("Erreur à l'initialisation des connections.");
            throw new RuntimeException(e);
        }
    }
    
    public static Connection getConnection() {
        Connection connection = connectionPool
          .remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }
    
    public static boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }
    
    private static Connection createConnection(
      String url, String user, String password) 
      throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        //connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return connection;
    }

    public int getSize() {
        return connectionPool.size() + usedConnections.size();
    }
}
