package com.uca;

import com.uca.InvalidInputException;

import com.uca.dao.*;
import com.uca.gui.*;
import com.uca.gui.*;

import com.uca.entity.*;

import com.google.gson.Gson;

import static spark.Spark.*;
import spark.*;

public class StartServer {

    /**
     * Classe principale du serveur Spark
     * pour plus d'info : http://sparkjava.com/documentation
     */
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
                return TrainGUI.list(TrainDAO.getInstance().getAll());
            });

        // requête pour l'ajout d'un nouveau train
        post("/train", (req, res) -> {
                Integer no = 0;
                String type = "";

                try {
                    no = new Integer(req.queryParams("no"));
                    type = req.queryParams("type");
                } catch (Exception e) {
                    // en cas d'erreur de lecture des paramètre de la requête HTTP,
                    // on retourne une erreur HTTP 400 
                    halt(400, "un paramètre est manquant ou n'est pas au bon format");
                }

                TrainDAO.getInstance().add(new Train(no, type));

                return TrainGUI.list(TrainDAO.getInstance().getAll());
            });

        // page du formulaire pour l'ajout d'un nouveau train
        get("/train/ajout", (req, res) -> {
                return TrainGUI.add();
            });

        // requête pour la suppression d'un train 
        post("/train/supprimer", (req, res) -> {
                int no = Integer.parseInt(req.queryParams("no"));
                TrainDAO.delete(no);
                // une fois le train supprimé,
                // on redirige le client sur la page listant les trains avec une redirection temporaire
                res.redirect("/train");
                return "";
            });

        // gestion des exceptions InvalidInputException
        // Cette exception est levée quand un paramètre d'une requête est invalide
        exception(InvalidInputException.class, (exception, req, res) -> {
                // On retourne le code HTTP 400 signifiant une erreur dans la requête du client
                // plus d'info : https://fr.wikipedia.org/wiki/Liste_des_codes_HTTP
                res.status(400);

                // On précise ici le corps de la page retournée
                // avec le message stocké dans l'exception
                res.body("Invalid input: " + exception.getMessage());
            });
    }
}
