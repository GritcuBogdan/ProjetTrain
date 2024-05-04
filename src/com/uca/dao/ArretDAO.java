package com.uca.dao;

import com.uca.InvalidInputException;
import com.uca.entity.Arret;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArretDAO extends AbstractDAO<Arret> {

    // L'unique instance de ce singleton
    private final static ArretDAO INSTANCE = new ArretDAO();

    protected ArretDAO() {
        super("ARRET");
    }

    // Accès à l'instance de la classe
    public static ArretDAO getInstance() {
        return INSTANCE;
    }

    // Implémentation de la méthode abstraite héritée
    // Transforme un tuple du ResultSet en un objet Arret
    public Arret getFromResultSet(ResultSet rs) throws SQLException {
        return new Arret(rs.getInt("NoLigne"), rs.getInt("Rang"), rs.getString("Ville"), rs.getDouble("Chrono"));
    }

    // Ajoute un nouvel arrêt à la ligne
    public void add(Arret arret) throws SQLException, InvalidInputException {
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ARRET(NoLigne, Rang, Ville, Chrono) VALUES (?, ?, ?, ?);");
            preparedStatement.setInt(1, arret.getNoLigne());
            preparedStatement.setInt(2, arret.getRang());
            preparedStatement.setString(3, arret.getVille());
            preparedStatement.setDouble(4, arret.getChrono());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            String sqlState = e.getSQLState();
            if (sqlState.equalsIgnoreCase("23505")) {
                String message = String.format("L'arrêt pour la ligne %d au rang %d existe déjà", arret.getNoLigne(), arret.getRang());
                throw new InvalidInputException(message);
            }
            throw e;
        }
        connection.commit();
        ConnectionPool.releaseConnection(connection);
    }

    // Supprime un arrêt selon son numéro de ligne et son rang
    public void delete(int noLigne, int rang) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ARRET WHERE NoLigne = ? AND Rang = ?");
            preparedStatement.setInt(1, noLigne);
            preparedStatement.setInt(2, rang);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        connection.commit();
        ConnectionPool.releaseConnection(connection);
    }

    // Retourne la liste des arrêts pour une ligne donnée
    public List<Arret> getArretsPourLigne(int numeroLigne) {
        List<Arret> arrets = new ArrayList<>();
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ARRET WHERE NoLigne = ? ORDER BY Rang;");
            preparedStatement.setInt(1, numeroLigne);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                arrets.add(getFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        return arrets;
    }

    public void enforceTimeGaps(int noLigne) throws SQLException {
        List<Arret> arrets = getArretsPourLigne(noLigne);
        for (int i = 0; i < arrets.size() - 1; i++) {
            Arret currentStop = arrets.get(i);
            Arret nextStop = arrets.get(i + 1);
            double timeGap = nextStop.getChrono() - currentStop.getChrono();
            if (timeGap < 15.0) {
                adjustTimeGap(nextStop, 15.0 - timeGap);
            }
        }
    }

    // Adjust the time gap between stops
    private void adjustTimeGap(Arret stop, double timeDifference) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE ARRET SET Chrono = Chrono + ? WHERE NoLigne = ? AND Rang >= ?");
            statement.setDouble(1, timeDifference);
            statement.setInt(2, stop.getNoLigne());
            statement.setInt(3, stop.getRang());
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
    }
}
