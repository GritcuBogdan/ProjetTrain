package com.uca.dao;

import com.uca.entity.*;

import java.sql.*;
import java.util.*;

/**
 * Classe abstraite d'object d'accès aux données (Data Access Object)
 * Cette classe est paramétrée par un type d'objet
 * dont les instances sont stockée dans une seule table.
 */
public abstract class AbstractDAO<P> {

    // nom de la table 
    protected final String tableName;

    protected AbstractDAO(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Retourne un object à partir d'un tuple
     * qui contient toutes les colonnes de la table
     */
    protected abstract P getFromResultSet(ResultSet rs) throws SQLException;

    /**
     * Retourne la liste qui contient tous les objets 
     */
    public List<P> getAll() throws SQLException {
        return getSelectStar(String.format("SELECT * FROM %s" , this.tableName));
    }

    /**
     * Retourne dans une liste les objets résultat d'une requête SQL
     */
    protected List<P> getSelectStar(String query) throws SQLException {
        // on récupère une connection depuis connection pool
        Connection connection = ConnectionPool.getConnection();

        // on définit une liste où stocké les résultats
        List<P> objects = new ArrayList<>();
        try {
            // on exécute la requête SQL
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            // tant qu'il reste des résultats
            while (resultSet.next()) {
                // on convertit le tuple en object 
                P o = getFromResultSet(resultSet);
                objects.add(o);
            }
        } catch (SQLException e) {
            // en cas d'erreur, on annule la transaction
            connection.rollback();
            throw e;
        }
        // on valide la transaction
        connection.commit();
        // on rend la connection 
        ConnectionPool.releaseConnection(connection);

        return objects;
    }

}
