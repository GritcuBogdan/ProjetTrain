<#import "utils.ftl" as u>

<@u.page>
  <table>
    <thead>
    <tr>
      <th>Type</th>
      <th>Numéro</th>
      <th>Voie de réserve</th>
      <th>Longitude</th>
      <th>Latitude</th>
      <th>Numéro de ligne</th>
      <th></th>
      <th></th> <!-- Add a column for the link to the departures page -->
    </tr>
    </thead>
    <tbody>
    <#list trains as train>
      <tr>
        <td>${train.getType()}</td>
        <td>${train.getNo()}</td>
        <td>${train.isOnReserveTrack()?string('Oui', 'Non')}</td>
        <td>${train.getLongitude()}</td>
        <td>${train.getLatitude()}</td>
        <td>${train.getNoligne()}</td> <!-- Add the noligne column -->
        <td>
          <form action="/train/supprimer?no=${train.getNo()?c}" method="POST">
            <input type="submit" value="Supprimer" class="button">
          </form>
        </td>
        <td>
          <a href="/depart/ajout?notrain=${train.getNo()}" class="button">Ajouter un départ</a>
          <a href="/depart?notrain=${train.getNo()}" class="button">Voir les départs</a>
        </td>
      </tr>
    </#list>
    </tbody>
  </table>

  <p>
    <a href="/train/ajout" class="button">Ajouter</a>
  </p>
</@u.page>
