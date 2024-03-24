<#import "utils.ftl" as u>

  <@u.page>
    <form action="/train" method="POST">
      <p>
        <label for="no">Numéro de train</label>
        <input type="number" name="no" id="no" />
      </p>
      <p>
        <label for="type">Type</label>
        <input type="text" name="type" id="type" />
      </p>
      <p>
        <input type="submit" value="Ajouter"/>
      </p>
      </form>
  </@u.page>
