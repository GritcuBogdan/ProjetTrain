package com.uca;

import com.uca.dao.DBInitializer;
import com.uca.dao.ConnectionPool;
import com.uca.dao.TrainDAO;
import com.uca.gui.IndexGUI;
import com.uca.gui.TrainGUI;

import com.uca.entity.Train;

import com.google.gson.Gson;

import static spark.Spark.*;
import spark.*;

public class StartServer {

    public static void main(String[] args) {
        //Configuration de Spark
        staticFiles.location("/static/");
        port(8081);

        // initialisation du pool de connections
        ConnectionPool.init();
        
        // Création de la base de données, si besoin
        DBInitializer.init();

        /**
         * Définition des routes
         */

        // index de l'application
        get("/", (req, res) -> {
                return IndexGUI.getIndex();
            });

        // page listant les trains
        get("/train", (req, res) -> {
                return TrainGUI.list(TrainDAO.getTrains());
            });

        post("/train", (req, res) -> {
                Integer no = 0;
                String type = "";

                try {
                    no = new Integer(req.queryParams("no"));
                    type = req.queryParams("type");
                } catch (Exception e) {
                    throw e;
                }

                System.out.println(no + " "+ type);
                TrainDAO.add(new Train(no, type));

                return TrainGUI.list(TrainDAO.getTrains());
            });

        get("/train/ajout", (req, res) -> {
                return TrainGUI.add();
            });

        post("/train/supprimer", (req, res) -> {
                int no = Integer.parseInt(req.queryParams("no"));
                TrainDAO.delete(no);
                res.redirect("/train");
                return "";
            });
    }
}
