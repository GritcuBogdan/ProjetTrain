<#import "utils.ftl" as u>

<@u.page>
    <table>
        <tr>
            <th>Numéro de ligne</th>
            <th>Rang</th>
            <th>Ville</th>
            <th>Chrono</th>
            <th></th>
        </tr>
        <#list arrets as arret>
            <tr>
                <td>${arret.noLigne}</td>
                <td>${arret.rang}</td>
                <td>${arret.ville!}</td>
                <td>${arret.chrono}</td>
                <td>
                    <form action="/arret/supprimer?noLigne=${arret.noLigne}&rang=${arret.rang}" method="POST">
                        <input type="submit" value="Supprimer"/>
                    </form>
                </td>
            </tr>
        </#list>
    </table>

    <p>
        <a href="/arret/ajout">Ajouter</a>
    </p>
</@u.page>
