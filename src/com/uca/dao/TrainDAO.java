package com.uca.dao;

import com.uca.entity.Train;

import java.sql.*;
import java.util.*;


public class TrainDAO {

    public static List<Train> getTrains() throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        List<Train> trains = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM train;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Train t = new Train(resultSet.getInt("notrain"), resultSet.getString("type"));
                trains.add(t);
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        connection.commit();
        ConnectionPool.releaseConnection(connection);

        return trains;
    }

    public static void add(Train train) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        try {

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO train(notrain, type) VALUES (?, ?);");
            preparedStatement.setInt(1, train.getNo());
            preparedStatement.setString(2, train.getType());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        connection.commit();
        ConnectionPool.releaseConnection(connection);
    }

    public static void delete(int no) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        try {

            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM train WHERE notrain =?;");
            preparedStatement.setInt(1, no);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        connection.commit();
        ConnectionPool.releaseConnection(connection);
    }

}
