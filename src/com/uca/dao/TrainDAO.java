package com.uca.dao;

import com.uca.InvalidInputException;

import com.uca.entity.Train;

import java.sql.*;
import java.util.*;

/**
 * Classe permet de modifier les trains stockés dans la table train
 * Elle est un singleton, c'est à dire une classe qui n'a qu'une seule instance
 * Plus d'info : https://fr.wikipedia.org/wiki/Singleton_(patron_de_conception)
 */
public class TrainDAO extends AbstractDAO<Train> {

    // l'unique instance de ce singleton 
    private final static TrainDAO INSTANCE = new TrainDAO();
    
    protected TrainDAO() {
        super("TRAIN");
    }

    // accès à l'instance de la classe
    public static TrainDAO getInstance() {
        return INSTANCE;
    }

    // implémentation de la méthode abstraite héritée
    // elle transforme un tuple du ResultSet en un objet Train
    public Train getFromResultSet(ResultSet rs) throws SQLException {
        return new Train(rs.getInt("notrain"), rs.getString("type"));
    }

    // insère un nouveau train
    public void add(Train train) throws SQLException, InvalidInputException {
        Connection connection = ConnectionPool.getConnection();
        try {

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO train(notrain, type) VALUES (?, ?);");
            preparedStatement.setInt(1, train.getNo());
            preparedStatement.setString(2, train.getType());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            // dès qu'une exception SQL est levée, il faut annuler la transaction
            connection.rollback();

            // on récupère le code de l'état SQL
            // plus d'info : https://www.ibm.com/docs/en/i/7.4?topic=codes-listing-sqlstate-values
            String sqlState = ((SQLException)e).getSQLState();

            // dans le cas où c'est une erreur due à une violation de la clé primaire
            if (sqlState.equalsIgnoreCase("23505")) {
                String message = String.format("le train de numéro %s existe déjà", train.getNo());
                throw new InvalidInputException(message);
            }

            // sinon propager l'erreur
            // pour le moment, il n'y a pas d'autre cas à gérer
            // (pas de pb de clé étrangère, ni de sérialisabilité possible) 
            throw e;
        }
        connection.commit();
        ConnectionPool.releaseConnection(connection);
    }

    public void delete(int trainNumber) throws SQLException {
        Connection connection = null;
        PreparedStatement statementTrain = null;
        PreparedStatement statementDepart = null;
        try {
            // Get a connection from the connection pool
            connection = ConnectionPool.getConnection();

            // Start a transaction
            connection.setAutoCommit(false);

            // Delete all departures associated with the train
            DepartDAO.getInstance().deleteDeparturesForTrain(trainNumber);

            // Delete the train itself
            String sqlTrain = "DELETE FROM train WHERE notrain = ?";
            statementTrain = connection.prepareStatement(sqlTrain);
            statementTrain.setInt(1, trainNumber);
            statementTrain.executeUpdate();

            // Delete departures associated with the train
            String sqlDepart = "DELETE FROM depart WHERE notrain = ?";
            statementDepart = connection.prepareStatement(sqlDepart);
            statementDepart.setInt(1, trainNumber);
            statementDepart.executeUpdate();

            // Commit the transaction
            connection.commit();
        } catch (SQLException e) {
            // Rollback the transaction
            if (connection != null) {
                connection.rollback();
            }
            // Log the exception
            e.printStackTrace();
            // Rethrow the exception to propagate it up the call stack
            throw e;
        } finally {
            // Close the statements
            if (statementTrain != null) {
                statementTrain.close();
            }
            if (statementDepart != null) {
                statementDepart.close();
            }
            // Release the connection back to the connection pool
            if (connection != null) {
                connection.close();
            }
        }
    }

}
