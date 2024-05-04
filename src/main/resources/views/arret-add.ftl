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
            <input type="submit" value="Ajouter"/>
        </p>
    </form>
</@u.page>
