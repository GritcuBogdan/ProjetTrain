<#import "utils.ftl" as u>

<@u.page>
    <table>
        <tr>
            <th>Train Number</th>
            <th>Departure Time</th>
            <th>Actions</th>
        </tr>
        <#list departures as departure>
            <tr>
                <td>${departure.noTrain}</td>
                <td>${departure.heure}</td>
                <td>
                    <form action="/depart/supprimer" method="POST">
                        <input type="hidden" name="notrain" value="${departure.noTrain}" />
                        <input type="hidden" name="heure" value="${departure.heure}" />
                        <input type="hidden" name="noligne" value="${departure.noLigne}" />
                        <input type="submit" value="Delete"/>
                    </form>
                </td>
            </tr>
        </#list>
    </table>

    <p>
        <a href="/depart/ajout?notrain=${trainNumber}">Add Departure</a>
    </p>
</@u.page>
