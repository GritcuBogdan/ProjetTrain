<#import "utils.ftl" as u>

<@u.page>
  <!-- Navbar -->
  <nav class="navbar">
    <div class="container">
      <a href="#" class="navbar-brand" style="color: white">Train manager</a>
      <ul class="navbar-nav">
        <li class="nav-item"><a href="/train" class="nav-link">Train</a></li>
        <li class="nav-item"><a href="/ligne" class="nav-link">Ligne</a></li>
        <li class="nav-item"><a href="/arret" class="nav-link">Arretes</a></li>
      </ul>
    </div>
  </nav>

  <!-- Map container -->
  <div id="map"></div>
  <style>
    #map {
      height: 400px;
      width: 100%;
    }
  </style>

  <script>
    // Initialize the map
    function initMap() {
      // Set the initial center and zoom level
      var map = new google.maps.Map(document.getElementById('map'), {
        center: { lat: 46.60335434795353, lng: 1.468611619609276 }, // France coordinates
        zoom: 6 // Zoom level for France
      });
      var addmark = new google.maps.Marker({
        position: {lat: 46.12,lng: 3.42 },
        map: map,
        title: "Vichy"
      });

      // Add markers for stations
      fetch('/arret')
              .then(response => response.json())
              .then(arrets => {
                arrets.forEach(arret => {
                  // Create marker
                  var marker = new google.maps.Marker({
                    position: { lat: arret.latitude, lng: arret.longitude },
                    map: map,
                    title: arret.ville
                  });
                });
              })
              .catch(error => {
                console.error('Error fetching station data:', error);
              });
    }
  </script>

  <!-- Load the Google Maps JavaScript API -->
  <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCEb4K1nC2O5SCHDwb5mALNLcHYs9bQ8-Y&callback=initMap" async defer></script>
</@u.page>
