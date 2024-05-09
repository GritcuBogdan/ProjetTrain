package com.uca.dao;

import com.uca.InvalidInputException;
import com.uca.entity.Arret;
import com.uca.entity.Ligne;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe permet de modifier les lignes stockées dans la table ligne
 * Elle est un singleton, c'est à dire une classe qui n'a qu'une seule instance
 */
public class LigneDAO extends AbstractDAO<Ligne> {

    // L'unique instance de ce singleton
    private final static LigneDAO INSTANCE = new LigneDAO();

    protected LigneDAO() {
        super("LIGNE");
    }

    // Accès à l'instance de la classe
    public static LigneDAO getInstance() {
        return INSTANCE;
    }

    // Implémentation de la méthode abstraite héritée
    // Elle transforme un tuple du ResultSet en un objet Ligne
    public Ligne getFromResultSet(ResultSet rs) throws SQLException {
        return new Ligne(rs.getInt("NoLigne"), rs.getString("Nom"));
    }
    // Insère une nouvelle ligne
    public void add(Ligne ligne) throws SQLException, InvalidInputException {
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Ligne(NoLigne, Nom) VALUES (?, ?);");
            preparedStatement.setInt(1, ligne.getNoLigne());
            preparedStatement.setString(2, ligne.getNom());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // En cas d'erreur, on annule la transaction
            connection.rollback();
            // On récupère le code de l'état SQL
            String sqlState = ((SQLException) e).getSQLState();
            // Dans le cas où c'est une erreur due à une violation de la clé primaire
            if (sqlState.equalsIgnoreCase("23505")) {
                String message = String.format("La ligne de numéro %s existe déjà", ligne.getNoLigne());
                throw new InvalidInputException(message);
            }
            // On propage l'erreur
            throw e;
        }
        connection.commit();
        ConnectionPool.releaseConnection(connection);
    }

    // Supprime une ligne
    public static void delete(int number) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement deleteArretsStatement = null;
        PreparedStatement deleteLigneStatement = null;
        try {

            connection.setAutoCommit(false);


            deleteArretsStatement = connection.prepareStatement("DELETE FROM arret WHERE NoLigne = ?");
            deleteArretsStatement.setInt(1, number);
            deleteArretsStatement.executeUpdate();


            deleteLigneStatement = connection.prepareStatement("DELETE FROM Ligne WHERE NoLigne = ?");
            deleteLigneStatement.setInt(1, number);
            deleteLigneStatement.executeUpdate();


            connection.commit();
        } catch (SQLException e) {

            if (connection != null) {
                connection.rollback();
            }

            throw e;
        } finally {

            if (deleteArretsStatement != null) {
                deleteArretsStatement.close();
            }
            if (deleteLigneStatement != null) {
                deleteLigneStatement.close();
            }
            ConnectionPool.releaseConnection(connection);
        }
    }


    // Retourne toutes les lignes
    public List<Ligne> getAll() throws SQLException {
        List<Ligne> lignes = new ArrayList<>();
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Ligne;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                lignes.add(getFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            // En cas d'erreur, on propage l'exception
            throw e;
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        return lignes;
    }
    public Arret getTerminalStation(int lineNumber) throws SQLException {
        return ArretDAO.getInstance().getHighestRankedStop(lineNumber);
    }

}
