<#import "utils.ftl" as u>

<@u.page>
    <table>
        <tr>
            <th>Numéro de ligne</th>
            <th>Rang</th>
            <th>Ville</th>
            <th>Chrono</th>
            <th>Nombre de pistes réservées</th>
            <th>Longitude</th>
            <th>Latitude</th>
            <th></th>
        </tr>
        <#list arrets as arret>
            <tr>
                <td>${arret.noLigne}</td>
                <td>${arret.rang}</td>
                <td>${arret.ville!}</td>
                <td>${arret.chrono}</td>
                <td>${arret.reserveDesPistes}</td>
                <td>${arret.longitude}</td>
                <td>${arret.latitude}</td>
                <td>
                    <form action="/arret/supprimer?noLigne=${arret.noLigne}&rang=${arret.rang}" method="POST">
                        <input type="submit" value="Supprimer"/>
                    </form>
                </td>
            </tr>
            <tr>
                <td colspan="8">
                    <a href="/arret/ajout?noLigne=${arret.noLigne}&rang=${arret.rang}" class="add-link">Ajouter un arrêt avant ce point</a>
                </td>
            </tr>
        </#list>
    </table>

    <p>
        <a href="/arret/ajout">Ajouter</a>
    </p>
</@u.page>
