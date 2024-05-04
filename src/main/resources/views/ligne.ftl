<#import "utils.ftl" as u>

<@u.page>
    <table>
        <tr>
            <th>Numéro</th>
            <th>Nom</th>
            <th></th>
        </tr>
        <#list lines as line>
            <tr>
                <td>${line.noLigne}</td>
                <td>${line.nom!}</td>
                <td>
                    <form action="/ligne/supprimer?number=${line.noLigne}" method="POST">
                        <input type="submit" value="Supprimer"/>
                    </form>
                </td>
            </tr>
        </#list>
    </table>

    <p>
        <a href="/ligne/ajout">Ajouter</a>
    </p>
</@u.page>
