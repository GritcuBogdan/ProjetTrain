<#import "utils.ftl" as u>

<@u.page>
    <form action="/depart/ajout" method="POST">
        <p>
            <label for="notrain">Train Number</label>
            <input type="number" name="notrain" id="notrain" value="${trainNumber}" readonly />
        </p>
        <p>
            <label for="heure">Departure Time</label>
            <input type="text" name="heure" id="heure" min="0" />
        </p>
        <p>
            <label for="noligne">Line Number</label>
            <input type="number" name="noligne" id="noligne" min="0" />
        </p>
        <p>
            <input type="submit" value="Add Departure"/>
        </p>
    </form>
</@u.page>
