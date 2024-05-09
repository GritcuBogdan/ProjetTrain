package com.uca;

import com.uca.dao.*;
import com.uca.entity.Arret;
import com.uca.entity.Depart;
import com.uca.entity.Ligne;
import com.uca.entity.Train;
import com.uca.gui.*;
import freemarker.template.Configuration;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class StartServer {

    public static void main(String[] args) {

        staticFiles.location("/static/");
        port(8081);


        ConnectionPool.init();


        DBInitializer.init();


        get("/", (req, res) -> {
            try {

                List<Arret> arrets = ArretDAO.getInstance().getAllArrets();


                Map<String, Object> model = new HashMap<>();
                model.put("arrets", arrets);


                Configuration configuration = _FreeMarkerInitializer.getContext(StartServer.class);


                StringWriter writer = new StringWriter();
                configuration.getTemplate("index.ftl").process(model, writer);
                return writer.toString();
            } catch (Exception e) {

                e.printStackTrace();
                res.status(500);
                return "Internal Server Error";
            }
        });


        get("/train", (req, res) -> TrainGUI.list(TrainDAO.getInstance().getAll()));


        post("/train", (req, res) -> {
            try {

                int trainNo = Integer.parseInt(req.queryParams("no"));
                String type = req.queryParams("type");
                String isOnReserveTracksParam = req.queryParams("sur_reserve_track");
                boolean isOnReserveTracks = "true".equals(isOnReserveTracksParam);
                int lineNo = Integer.parseInt(req.queryParams("noligne"));
                double[] latLng = TrainDAO.getLatitudeLongitudeForStop(lineNo);
                double latitude = latLng[0];
                double longitude = latLng[1];
                TrainDAO.getInstance().add(new Train(trainNo, type, isOnReserveTracks, longitude, latitude,lineNo));

                res.redirect("/train");
            } catch (NumberFormatException | SQLException | InvalidInputException e) {

                e.printStackTrace();
                res.status(500);
                return "Internal Server Error";
            }
            return "";
        });



        get("/train/ajout", (req, res) -> TrainGUI.add());


        post("/train/supprimer", (req, res) -> {
            int no = Integer.parseInt(req.queryParams("no"));
            TrainDAO trainDAO = TrainDAO.getInstance();
            trainDAO.delete(no);
            res.redirect("/train");
            return "";
        });


        get("/ligne", (req, res) -> LigneGUI.list(LigneDAO.getInstance().getAll()));


        post("/ligne", (req, res) -> {
            Integer number = Integer.parseInt(req.queryParams("numero"));
            String name = req.queryParams("nom");
            LigneDAO.getInstance().add(new Ligne(number, name));
            res.redirect("/ligne");
            return "";
        });


        get("/ligne/ajout", (req, res) -> LigneGUI.add());


        post("/ligne/supprimer", (req, res) -> {
            int number = Integer.parseInt(req.queryParams("number"));
            LigneDAO.delete(number);
            res.redirect("/ligne");
            return "";
        });


        get("/arret", (req, res) -> {
            try {
                // Retrieve the noligne parameter from the request URL
                String noligneParam = req.queryParams("noligne");

                if (noligneParam != null) {
                    // Parse the noligne parameter to an integer
                    int noligne = Integer.parseInt(noligneParam);

                    // Fetch stops associated with the specified line number
                    List<Arret> arrets = ArretDAO.getInstance().getByLineNo(noligne);

                    // Pass the filtered list of stops to the ArretGUI list method
                    return ArretGUI.list(arrets);
                } else {
                    // Handle the case when the noligne parameter is missing
                    res.status(400);
                    return "No line number parameter provided";
                }
            } catch (NumberFormatException e) {
                // Handle the case when the noligne parameter cannot be parsed to an integer
                res.status(400);
                return "Invalid line number parameter";
            } catch (Exception e) {
                // Handle other exceptions
                e.printStackTrace();
                res.status(500);
                return "Internal Server Error: " + e.getMessage();
            }
        });



        post("/arret", (req, res) -> {
            try {
                int noLigne = Integer.parseInt(req.queryParams("noLigne"));
                int rang = Integer.parseInt(req.queryParams("rang"));
                String ville = req.queryParams("ville");
                double chrono = Double.parseDouble(req.queryParams("chrono"));
                double latitude = Double.parseDouble(req.queryParams("latitude"));
                double longitude = Double.parseDouble(req.queryParams("longitude"));
                int reserverDesPistes = Integer.parseInt(req.queryParams("reserver_des_pistes"));


                Arret existingStop = ArretDAO.getInstance().getArretByRank(noLigne, 0);

                if (rang == 0 && existingStop != null) {

                    ArretDAO.getInstance().add(new Arret(noLigne, 0, ville, chrono, reserverDesPistes, latitude, longitude));
                } else {

                    ArretDAO.getInstance().shiftRanksForward(noLigne, rang);


                    ArretDAO.getInstance().add(new Arret(noLigne, rang, ville, chrono, reserverDesPistes, latitude, longitude));
                }

                ArretDAO.getInstance().enforceTimeGaps(noLigne);

                res.redirect("/arret?noligne="+noLigne);
                return "";
            } catch (NumberFormatException e) {
                res.status(400);
                return "Invalid parameters";
            } catch (SQLException e) {
                res.status(500);
                return "Internal Server Error: " + e.getMessage();
            }
        });



        get("/arret/ajout", (req, res) -> ArretGUI.add());


        post("/arret/supprimer", (req, res) -> {
            try {
                int noLigne = Integer.parseInt(req.queryParams("noLigne"));
                int rang = Integer.parseInt(req.queryParams("rang"));


                ArretDAO.getInstance().delete(noLigne, rang);


                ArretDAO.getInstance().shiftRanksBack(noLigne, rang);


                res.redirect("/arret?noligne="+noLigne);
                return "";
            } catch (NumberFormatException e) {
                res.status(400);
                return "Invalid parameters";
            } catch (SQLException e) {
                res.status(500);
                return "Internal Server Error: " + e.getMessage();
            }
        });





        get("/depart", (req, res) -> {

            String trainNumberParam = req.queryParams("notrain");
            if (trainNumberParam != null) {
                try {
                    int trainNumber = Integer.parseInt(trainNumberParam);

                    List<Depart> departures = DepartDAO.getInstance().getDeparturesForTrain(trainNumber);

                    return DepartGUI.list(departures, trainNumber);
                } catch (NumberFormatException e) {

                    res.status(400);
                    return "Invalid train number";
                } catch (Exception e) {

                    e.printStackTrace();
                    res.status(500);
                    return "Internal Server Error";
                }
            } else {

                res.status(400);
                return "Missing 'notrain' parameter";
            }
        });


        get("/depart/ajout", (req, res) -> {

            int trainNumber = Integer.parseInt(req.queryParams("notrain"));


            Integer noLigne = DepartDAO.getInstance().getNoLigneForTrain(trainNumber);


            Map<String, Object> input = new HashMap<>();
            input.put("trainNumber", trainNumber);
            input.put("noligne", noLigne);


            return DepartGUI.addForm(input,trainNumber,"");
        });

        post("/depart/ajout", (req, res) -> {
            try {
                // Retrieve the parameters from the form
                String trainNumberParam = req.queryParams("notrain");
                String heureParam = req.queryParams("heure");
                String noligneParam = req.queryParams("noligne");


                if (trainNumberParam != null && heureParam != null) {
                    int trainNumber = Integer.parseInt(trainNumberParam);
                    double heure = Double.parseDouble(heureParam);
                    int noligne = Integer.parseInt(noligneParam);


                    boolean isDepartureConflict = DepartDAO.getInstance().isDepartureConflict(trainNumber, heure);
                    if (isDepartureConflict) {

                        res.status(400);
                        return "Train " + trainNumber + " is already scheduled to depart at " + heure + " hours.";
                    } else {

                        Integer noLigneInteger = DepartDAO.getInstance().getNoLigneForTrain(trainNumber);
                        if (noLigneInteger != null) {
                            int noLigne = noLigneInteger;



                            DepartDAO.getInstance().addDeparture(new Depart(noLigne, heure, trainNumber));


                            res.redirect("/depart?notrain=" + trainNumber);
                        } else {

                            res.status(404);
                            return "NoLigne not found for train number: " + trainNumber;
                        }
                    }
                } else {

                    res.status(400);
                    return "Train number or heure parameter is missing";
                }
            } catch (NumberFormatException e) {

                res.status(400);
                return "Invalid train number or heure parameter";
            } catch (Exception e) {

                e.printStackTrace();

                res.status(500);
                return "Internal Server Error: " + e.getMessage();
            }
            return "";
        });


        post("/depart/supprimer", (req, res) -> {
            try {

                int noLigne = Integer.parseInt(req.queryParams("noligne"));
                double heure = Double.parseDouble(req.queryParams("heure"));
                int noTrain = Integer.parseInt(req.queryParams("notrain"));


                DepartDAO.getInstance().deleteDeparture(noLigne, heure, noTrain);


                res.redirect("/depart?notrain=" + noTrain);
            } catch (NumberFormatException e) {

                res.status(400);
                return "Invalid parameters";
            } catch (SQLException e) {

                res.status(500);
                return "Internal Server Error: " + e.getMessage();
            }
            return "";
        });


    }
}
