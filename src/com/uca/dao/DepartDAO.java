package com.uca.dao;

import com.uca.entity.Depart;
import com.uca.entity.Train;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartDAO extends AbstractDAO<Depart> {

    // Singleton instance
    private static final DepartDAO INSTANCE = new DepartDAO();

    private DepartDAO() {
        super("DEPART");
    }

    public static DepartDAO getInstance() {
        return INSTANCE;
    }

    public List<Depart> getDeparturesForTrain(int trainNumber) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        List<Depart> departures = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM depart WHERE notrain = ?"
            );
            preparedStatement.setInt(1, trainNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                departures.add(getFromResultSet(resultSet));
            }
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        return departures;
    }

    @Override
    public Depart getFromResultSet(ResultSet rs) throws SQLException {
        int noLigne = rs.getInt("noligne");
        double heure = rs.getDouble("heure");
        int noTrain = rs.getInt("notrain");
        return new Depart(noLigne, heure, noTrain);
    }

    // Get the NoLigne associated with a train number
    public Integer getNoLigneForTrain(int trainNumber) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        Integer noLigne = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT DISTINCT noligne FROM depart WHERE notrain = ? LIMIT 1"
            );
            preparedStatement.setInt(1, trainNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                noLigne = resultSet.getInt("noligne");
            }
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        return noLigne;
    }

    // Add a new departure
// Add a new departure
    public void addDeparture(Depart departure) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // Get a connection from the connection pool
            connection = ConnectionPool.getConnection();

            // Create the prepared statement
            String sql = "INSERT INTO depart (noligne, heure, notrain) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(sql);

            // Set the values for the parameters
            statement.setInt(1, departure.getNoLigne());
            statement.setDouble(2, departure.getHeure());
            statement.setInt(3, departure.getNoTrain());

            // Execute the update
            statement.executeUpdate();

            // Commit the transaction (if applicable)
            connection.commit();
        } catch (SQLException e) {
            // Rollback the transaction (if applicable)
            if (connection != null) {
                connection.rollback();
            }
            // Log the exception
            e.printStackTrace();
            // Rethrow the exception to propagate it up the call stack
            throw e;
        } finally {
            // Close the statement
            if (statement != null) {
                statement.close();
            }
            // Release the connection back to the connection pool
            if (connection != null) {
                connection.close();
            }
        }
    }

    // Delete a departure
    public void deleteDeparture(int noLigne, double heure, int noTrain) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // Get a connection from the connection pool
            connection = ConnectionPool.getConnection();

            // Create the prepared statement
            String sql = "DELETE FROM depart WHERE noligne = ? AND heure = ? AND notrain = ?";
            statement = connection.prepareStatement(sql);

            // Set the values for the parameters
            statement.setInt(1, noLigne);
            statement.setDouble(2, heure);
            statement.setInt(3, noTrain);

            // Execute the update
            statement.executeUpdate();

            // Commit the transaction (if applicable)
            connection.commit();
        } catch (SQLException e) {
            // Rollback the transaction (if applicable)
            if (connection != null) {
                connection.rollback();
            }
            // Log the exception
            e.printStackTrace();
            // Rethrow the exception to propagate it up the call stack
            throw e;
        } finally {
            // Close the statement
            if (statement != null) {
                statement.close();
            }
            // Release the connection back to the connection pool
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void deleteDeparturesForTrain(int trainNumber) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // Get a connection from the connection pool
            connection = ConnectionPool.getConnection();

            // Delete all departures for the train
            String sql = "DELETE FROM depart WHERE notrain = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, trainNumber);
            statement.executeUpdate();
        } finally {

            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
    }
    public boolean isDepartureConflict(int trainNumber, double heure) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*) AS count FROM depart WHERE notrain = ? AND heure = ?"
            );
            preparedStatement.setInt(1, trainNumber);
            preparedStatement.setDouble(2, heure);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
            return false;
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
    }



}
