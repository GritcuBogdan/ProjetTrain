package com.uca.dao;

import com.uca.InvalidInputException;
import com.uca.entity.Arret;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArretDAO extends AbstractDAO<Arret> {


    private final static ArretDAO INSTANCE = new ArretDAO();

    protected ArretDAO() {
        super("ARRET");
    }


    public static ArretDAO getInstance() {
        return INSTANCE;
    }


    public Arret getFromResultSet(ResultSet rs) throws SQLException {
        return new Arret(rs.getInt("NoLigne"), rs.getInt("Rang"), rs.getString("Ville"), rs.getDouble("Chrono"),rs.getInt("reserver_des_pistes"),rs.getDouble("longitude"),rs.getDouble("latitude"));
    }

    public void add(Arret arret) throws SQLException, InvalidInputException {
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ARRET(NoLigne, Rang, Ville, Chrono,reserver_des_pistes, Latitude, Longitude) VALUES (?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, arret.getNoLigne());
            preparedStatement.setInt(2, arret.getRang());
            preparedStatement.setString(3, arret.getVille());
            preparedStatement.setDouble(4, arret.getChrono());
            preparedStatement.setDouble(5, arret.getReserveDesPistes());
            preparedStatement.setDouble(6, arret.getLatitude());
            preparedStatement.setDouble(7,arret.getLongitude());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            String sqlState = e.getSQLState();
            if (sqlState.equalsIgnoreCase("23505")) {
                String message = String.format("L'arrêt pour la ligne %d au rang %d existe déjà", arret.getNoLigne(), arret.getRang());
                throw new InvalidInputException(message);
            }
            throw e;
        } finally {
            connection.commit();
            ConnectionPool.releaseConnection(connection);
        }
    }



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


    public void shiftRanksForward(int noLigne, int startRang) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE ARRET SET Rang = Rang + 1 WHERE NoLigne = ? AND Rang > ?");
            statement.setInt(1, noLigne);
            statement.setInt(2, startRang);
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
    }


    public void shiftRanksBack(int noLigne, int startRang) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE ARRET SET Rang = Rang - 1 WHERE NoLigne = ? AND Rang > ?");
            statement.setInt(1, noLigne);
            statement.setInt(2, startRang);
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
    }

    public void ensureStopRanks() throws SQLException {

        List<Integer> lines = getAllLineNumbers();
        for (int line : lines) {
            List<Arret> stops = getArretsPourLigne(line);
            int totalStops = stops.size();


            for (int i = 0; i < stops.size(); i++) {
                int currentRank = stops.get(i).getRang();
                int newRank = Math.min(Math.max(0, i), totalStops - 1);
                if (currentRank != newRank) {
                    updateRankForStop(line, currentRank, newRank);
                }
            }
        }
    }


    private List<Integer> getAllLineNumbers() throws SQLException {
        List<Integer> lines = new ArrayList<>();
        Connection connection = ConnectionPool.getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT NoLigne FROM ARRET");
            while (resultSet.next()) {
                lines.add(resultSet.getInt("NoLigne"));
            }
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        return lines;
    }

    // Update rank for a stop
    private void updateRankForStop(int noLigne, int oldRank, int newRank) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE ARRET SET Rang = ? WHERE NoLigne = ? AND Rang = ?");
            statement.setInt(1, newRank);
            statement.setInt(2, noLigne);
            statement.setInt(3, oldRank);
            statement.executeUpdate();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
    }

    public Arret getArretByRank(int noLigne, int rang) {
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ARRET WHERE NoLigne = ? AND Rang = ?");
            preparedStatement.setInt(1, noLigne);
            preparedStatement.setInt(2, rang);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
        return null;
    }

    public void enforceTimeGaps(int noLigne) throws SQLException {
        List<Arret> arrets = getArretsPourLigne(noLigne);
        for (int i = 0; i < arrets.size() - 1; i++) {
            Arret currentStop = arrets.get(i);
            Arret nextStop = arrets.get(i + 1);
            double timeGap = nextStop.getChrono() - currentStop.getChrono();
            if (timeGap < 0.15) {
                adjustTimeGap(nextStop, 0.15 - timeGap);
            }
        }
    }


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

    public Arret getHighestRankedStop(int lineNumber) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Arret WHERE noLigne = ? ORDER BY rang DESC LIMIT 1");
            preparedStatement.setInt(1, lineNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Arret(resultSet.getInt("noLigne"), resultSet.getInt("rang"), resultSet.getString("ville"), resultSet.getDouble("chrono"),resultSet.getInt("reserver_des_pistes"),resultSet.getDouble("longitude"),resultSet.getDouble("latitude"));
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    public List<Arret> getAllArrets() throws SQLException {
        List<Arret> arrets = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionPool.getConnection();
            String sql = "SELECT * FROM arret";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int noLigne = resultSet.getInt("noligne");
                int rang = resultSet.getInt("rang");
                String ville = resultSet.getString("ville");
                double chrono = resultSet.getDouble("chrono");
                int reserveDesPistes = resultSet.getInt("reserver_des_pistes");
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");

//
//                if (noLigne == 1 && rang == 0) {
//                    Arret existingArret = getArretByRank(noLigne, rang);
//                    if (existingArret != null) {
//
//                        continue;
//                    }
//                }


                Arret arret = new Arret(noLigne, rang, ville, chrono, reserveDesPistes, latitude, longitude);
                arrets.add(arret);
            }
        } finally {
            // Close resources
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return arrets;
    }

    public List<Arret> getByLineNo(int lineNo) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        List<Arret> arrets = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sql = "SELECT * FROM arret WHERE noligne = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, lineNo);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int noLigne = resultSet.getInt("noLigne");
                int rang = resultSet.getInt("rang");
                String ville = resultSet.getString("ville");
                double chrono = resultSet.getDouble("chrono");
                int reserveDesPistes = resultSet.getInt("reserver_des_pistes");
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");

                Arret arret = new Arret(noLigne, rang, ville, chrono, reserveDesPistes, latitude, longitude);
                arrets.add(arret);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            ConnectionPool.releaseConnection(connection);
        }

        return arrets;
    }

}
