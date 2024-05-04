<#import "utils.ftl" as u>

<@u.page>
    <form action="/ligne" method="POST">
        <p>
            <label for="number">Numéro de ligne</label>
            <input type="number" name="numero" id="numero" min="0" />
        </p>
        <p>
            <label for="nom">Nom</label>
            <input type="text" name="nom" id="nom" />
        </p>
        <p>
            <input type="submit" value="Ajouter"/>
        </p>
    </form>
</@u.page>
