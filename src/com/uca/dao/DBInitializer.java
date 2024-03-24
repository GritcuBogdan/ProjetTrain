package com.uca.dao;

import java.sql.*;

public class DBInitializer {

    /**
     * cette méthode permet d'initialiser la BD en créant les tables manquantes
     */
    public static void init(){
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement statement;
        
        try {
            if (!tableExists(connection, "TRAIN")) {
                    statement = connection.prepareStatement("CREATE TABLE TRAIN (NoTrain integer, Type text,primary key(noTrain));");
                    statement.executeUpdate();
            }

            //TODO : créer le code pour les autres tables
            
            connection.commit();
            ConnectionPool.releaseConnection(connection);
            
        } catch (SQLException e){
            System.out.println(e.toString());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
            e.printStackTrace();
            throw new RuntimeException("!!!! impossible d'initialiser la base !!!!");
        }
    }

    /**
     * teste si une table existe dans la base de données 
     */
    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName.toLowerCase(), new String[]{"TABLE"});
        return resultSet.next();
    }
}
