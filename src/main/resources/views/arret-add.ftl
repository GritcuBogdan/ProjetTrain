<#import "utils.ftl" as u>

<@u.page>
    <form action="/arret" method="POST">
        <p>
            <label for="noLigne">Numéro de ligne</label>
            <input type="number" name="noLigne" id="noLigne" min="0" />
        </p>
        <p>
            <label for="rang">Rang</label>
            <input type="number" name="rang" id="rang" min="0" />
        </p>
        <p>
            <label for="ville">Ville</label>
            <input type="text" name="ville" id="ville" />
        </p>
        <p>
            <label for="chrono">Chrono</label>
            <input type="number" step="0.01" name="chrono" id="chrono" min="0"/>
        </p>
        <p>
            <label for="reserver_des_pistes">Nombre de pistes réservées</label>
            <input type="number" name="reserver_des_pistes" id="reserver_des_pistes" min="0" />
        </p>
        <p>
            <label for="longitude">Longitude</label>
            <input type="number" step="0.000001" name="longitude" id="longitude" />
        </p>
        <p>
            <label for="latitude">Latitude</label>
            <input type="number" step="0.000001" name="latitude" id="latitude" />
        </p>
        <p>
            <input type="submit" value="Ajouter"/>
        </p>
    </form>
</@u.page>
