package com.uca.dao;

import com.uca.InvalidInputException;

import com.uca.entity.Arret;
import com.uca.entity.Train;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        return new Train(rs.getInt("notrain"), rs.getString("type"),rs.getBoolean("sur_reserve_track"),rs.getDouble("longitude"),rs.getDouble("latitude"),rs.getInt("noligne"));
    }

    public static double[] getLatitudeLongitudeForStop(int lineNo) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Prepare the SQL statement to retrieve latitude and longitude for the stop with index 0 of the specified line
            String sql = "SELECT latitude, longitude FROM arret WHERE noligne = ? AND rang = 0";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, lineNo);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Check if the result set has a row
            if (resultSet.next()) {
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");
                return new double[]{latitude, longitude};
            } else {
                // If no data is found, you can handle this scenario as per your application logic
                // For example, you can throw an exception or return default values
                throw new SQLException("No latitude and longitude data found for line: " + lineNo);
            }
        } finally {
            // Close the ResultSet, PreparedStatement, and release the connection
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            ConnectionPool.releaseConnection(connection);
        }
    }

    // insère un nouveau train
    public void add(Train train) throws SQLException, InvalidInputException {
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement preparedStatement = null;

        try {
            // Prepare the SQL statement to insert the train
            String sql = "INSERT INTO train(notrain, type, noligne, sur_reserve_track, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, train.getNo());
            preparedStatement.setString(2, train.getType());
            preparedStatement.setInt(3, train.getNoligne());
            preparedStatement.setBoolean(4,train.isOnReserveTrack());
            preparedStatement.setDouble(5, train.getLatitude()); // Latitude
            preparedStatement.setDouble(6,train.getLongitude()); // Longitude

            // Execute the update
            preparedStatement.executeUpdate();
        } finally {
            // Close the PreparedStatement and release the connection
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            ConnectionPool.releaseConnection(connection);
        }
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

    public void updateTrainLocation(int trainNumber, boolean onReserveTrack, double longitude, double latitude) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE train SET sur_reserve_track = ?, longitude = ?, latitude = ? WHERE notrain = ?");
            preparedStatement.setBoolean(1, onReserveTrack);
            preparedStatement.setDouble(2, longitude);
            preparedStatement.setDouble(3, latitude);
            preparedStatement.setInt(4, trainNumber);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }




}
