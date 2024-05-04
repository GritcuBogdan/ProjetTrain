package com.uca;

import com.uca.InvalidInputException;
import com.uca.dao.*;
import com.uca.entity.*;
import com.uca.gui.*;
import freemarker.template.Configuration;
import spark.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class StartServer {

    public static void main(String[] args) {
        // Spark configuration
        staticFiles.location("/static/");
        port(8081);

        // Initialize connection pool
        ConnectionPool.init();

        // Initialize the database if needed
        DBInitializer.init();

        // Routes definition
        get("/", (req, res) -> IndexGUI.getIndex());

        // List all trains
        get("/train", (req, res) -> TrainGUI.list(TrainDAO.getInstance().getAll()));

        // Add a new train
        post("/train", (req, res) -> {
            Integer no = Integer.parseInt(req.queryParams("no"));
            String type = req.queryParams("type");
            TrainDAO.getInstance().add(new Train(no, type));
            res.redirect("/train");
            return "";
        });

        // Page for adding a new train
        get("/train/ajout", (req, res) -> TrainGUI.add());

        // Delete a train
        post("/train/supprimer", (req, res) -> {
            int no = Integer.parseInt(req.queryParams("no"));
            TrainDAO trainDAO = TrainDAO.getInstance(); // Create an instance of TrainDAO
            trainDAO.delete(no); // Call the delete method on the instance
            res.redirect("/train");
            return "";
        });

        // List all lines
        get("/ligne", (req, res) -> LigneGUI.list(LigneDAO.getInstance().getAll()));

        // Add a new line
        post("/ligne", (req, res) -> {
            Integer number = Integer.parseInt(req.queryParams("numero")); // Change to "number" from "noligne"
            String name = req.queryParams("nom"); // Change to "name" from "nom"
            LigneDAO.getInstance().add(new Ligne(number, name));
            res.redirect("/ligne");
            return "";
        });

        // Page for adding a new line
        get("/ligne/ajout", (req, res) -> LigneGUI.add());

        // Delete a line
        post("/ligne/supprimer", (req, res) -> {
            int number = Integer.parseInt(req.queryParams("number"));
            LigneDAO.delete(number);
            res.redirect("/ligne");
            return "";
        });

        // List all stops
        get("/arret", (req, res) -> ArretGUI.list(ArretDAO.getInstance().getAll()));

        // Add a new stop
        post("/arret", (req, res) -> {
            int noLigne = Integer.parseInt(req.queryParams("noLigne"));
            int rang = Integer.parseInt(req.queryParams("rang"));
            String ville = req.queryParams("ville");
            double chrono = Double.parseDouble(req.queryParams("chrono"));
            ArretDAO.getInstance().add(new Arret(noLigne, rang, ville, chrono));
            res.redirect("/arret");

            return "";
        });

        // Page for adding a new stop
        get("/arret/ajout", (req, res) -> ArretGUI.add());

        // Delete a stop
        post("/arret/supprimer", (req, res) -> {
            int noLigne = Integer.parseInt(req.queryParams("noLigne"));
            int rang = Integer.parseInt(req.queryParams("rang"));
            ArretDAO.getInstance().delete(noLigne, rang);
            res.redirect("/arret");
            return "";
        });

        // Exception handling for InvalidInputException
        exception(InvalidInputException.class, (exception, req, res) -> {
            res.status(400);
            res.body("Invalid input: " + exception.getMessage());
        });




        // List departures for a specific train
        get("/depart", (req, res) -> {
            // Extract the train number from the query parameter
            String trainNumberParam = req.queryParams("notrain");
            if (trainNumberParam != null) {
                try {
                    int trainNumber = Integer.parseInt(trainNumberParam);
                    // Retrieve departures for the specified train number
                    List<Depart> departures = DepartDAO.getInstance().getDeparturesForTrain(trainNumber);
                    // Render the HTML page displaying the list of departures
                    return DepartGUI.list(departures, trainNumber);
                } catch (NumberFormatException e) {
                    // Handle the case where the train number cannot be parsed as an integer
                    res.status(400);
                    return "Invalid train number";
                } catch (Exception e) {
                    // Handle other exceptions
                    e.printStackTrace();
                    res.status(500);
                    return "Internal Server Error";
                }
            } else {
                // Handle the case where the 'notrain' query parameter is missing
                res.status(400);
                return "Missing 'notrain' parameter";
            }
        });

// Form to add a departure for a specific train
        // Route for displaying the form to add a new departure
        get("/depart/ajout", (req, res) -> {
            // Retrieve the train number from the query parameters
            int trainNumber = Integer.parseInt(req.queryParams("notrain"));

            // Retrieve the NoLigne for the train number
            Integer noLigne = DepartDAO.getInstance().getNoLigneForTrain(trainNumber);

            // Create a Map to pass data to the FreeMarker template
            Map<String, Object> input = new HashMap<>();
            input.put("trainNumber", trainNumber);
            input.put("noligne", noLigne);

            // Render the FreeMarker template for adding a new departure
            return DepartGUI.addForm(input,trainNumber,"");
        });

        post("/depart/ajout", (req, res) -> {
            try {
                // Retrieve the parameters from the form
                String trainNumberParam = req.queryParams("notrain");
                String heureParam = req.queryParams("heure");
                String noLigneParam = req.queryParams("noligne");

                // Parse the parameters
                if (trainNumberParam != null && heureParam != null && noLigneParam != null) {
                    int trainNumber = Integer.parseInt(trainNumberParam);
                    double heure = Double.parseDouble(heureParam);
                    int noLigne = Integer.parseInt(noLigneParam);

                    // Add the departure with the retrieved NoLigne
                    DepartDAO.getInstance().addDeparture(new Depart(noLigne, heure, trainNumber));

                    // Redirect to the /train page
                    res.redirect("/depart?notrain="+trainNumber);
                } else {
                    // Handle the case where either train number, heure, or noligne parameter is missing
                    res.status(400);
                    return "Train number, heure, or noligne parameter is missing";
                }
            } catch (NumberFormatException e) {
                // Handle the case where train number, heure, or noligne parameter cannot be parsed
                res.status(400);
                return "Invalid train number, heure, or noligne parameter";
            } catch (Exception e) {
                // Log the exception
                e.printStackTrace();
                // Return 500 Internal Server Error
                res.status(500);
                return "Internal Server Error: " + e.getMessage();
            }
            return "";
        });










//        // List departures for a specific train
//        get("/depart", (req, res) -> {
//            // Extract the train number from the query parameter
//            String trainNumberParam = req.queryParams("notrain");
//            if (trainNumberParam != null) {
//                try {
//                    int trainNumber = Integer.parseInt(trainNumberParam);
//                    // Retrieve departures for the specified train number
//                    List<Depart> departures = DepartDAO.getInstance().getDeparturesForTrain(trainNumber);
//                    // Render the HTML page displaying the list of departures
//                    return DepartGUI.list(departures, trainNumber);
//                } catch (NumberFormatException e) {
//                    // Handle the case where the train number cannot be parsed as an integer
//                    res.status(400);
//                    return "Invalid train number";
//                } catch (Exception e) {
//                    // Handle other exceptions
//                    e.printStackTrace();
//                    res.status(500);
//                    return "Internal Server Error";
//                }
//            } else {
//                // Handle the case where the 'notrain' query parameter is missing
//                res.status(400);
//                return "Missing 'notrain' parameter";
//            }
//        });
//
//// Form to add a departure for a specific train
//        get("/depart/ajout", (req, res) -> {
//            int trainNumber = Integer.parseInt(req.queryParams("notrain"));
//            return DepartGUI.addForm(trainNumber);
//        });
//
//// Handle submission of the form to add a departure
//        // Handle submission of the form to add a departure
//        post("/depart/ajout", (req, res) -> {
//            try {
//                // Retrieve the parameters from the form
//                String trainNumberParam = req.queryParams("notrain");
//                String heureParam = req.queryParams("heure");
//
//                // Parse the parameters
//                if (trainNumberParam != null && heureParam != null) {
//                    int trainNumber = Integer.parseInt(trainNumberParam);
//                    double heure = Double.parseDouble(heureParam);
//
//                    // Retrieve NoLigne associated with the train number
//                    Integer noLigneInteger = DepartDAO.getInstance().getNoLigneForTrain(trainNumber);
//                    if (noLigneInteger != null) {
//                        int noLigne = noLigneInteger;
//
//                        // Add the departure with the retrieved NoLigne
//                        DepartDAO.getInstance().addDeparture(new Depart(noLigne, heure, trainNumber));
//
//                        // Redirect to the departures page for the specified train number
//                        res.redirect("/depart?notrain=" + trainNumber);
//                    } else {
//                        // Handle the case where NoLigne is not found for the train number
//                        res.status(404);
//                        return "NoLigne not found for train number: " + trainNumber;
//                    }
//                } else {
//                    // Handle the case where either train number or heure parameter is null
//                    res.status(400);
//                    return "Train number or heure parameter is missing";
//                }
//            } catch (NumberFormatException e) {
//                // Handle the case where train number or heure parameter cannot be parsed
//                res.status(400);
//                return "Invalid train number or heure parameter";
//            } catch (Exception e) {
//                // Log the exception
//                e.printStackTrace();
//                // Return 500 Internal Server Error
//                res.status(500);
//                return "Internal Server Error: " + e.getMessage();
//            }
//            return "";
//        });
//
//

        // Handle deletion of a departure
        post("/depart/supprimer", (req, res) -> {
            try {
                // Retrieve the parameters from the request
                int noLigne = Integer.parseInt(req.queryParams("noligne"));
                double heure = Double.parseDouble(req.queryParams("heure"));
                int noTrain = Integer.parseInt(req.queryParams("notrain"));

                // Delete the departure
                DepartDAO.getInstance().deleteDeparture(noLigne, heure, noTrain);

                // Redirect to the departures page for the specified train number
                res.redirect("/depart?notrain=" + noTrain);
            } catch (NumberFormatException e) {
                // Handle the case where parameters cannot be parsed
                res.status(400);
                return "Invalid parameters";
            } catch (SQLException e) {
                // Handle any database-related errors
                res.status(500);
                return "Internal Server Error: " + e.getMessage();
            }
            return "";
        });


    }
}
