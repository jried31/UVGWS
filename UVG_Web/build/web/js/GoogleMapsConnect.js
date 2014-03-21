
var map;
var directionsDisplay;
var directionsService;
var stepDisplay;
var markerArray = [];
var geocoder;
var start_loc_lat;
var start_loc_lng;
var end_loc_lat;
var end_loc_lng;

function initialize() {
    
  // Google Maps Geocoder to convert address to LatLng
  geocoder = new google.maps.Geocoder();
  var latlng = new google.maps.LatLng(-34.397, 150.644);
  /*var mapOptions = {
    zoom: 8,
    center: latlng
  }*/
  
  // Instantiate a directions service.
  directionsService = new google.maps.DirectionsService();

  // Create a map and center it on UCLA.
  var mapOptions = {
    center: new google.maps.LatLng(34.06892, -118.44518),
    zoom: 8
  };
  map = new google.maps.Map(document.getElementById("map-canvas"),
      mapOptions);

  // Create a renderer for directions and bind it to the map.
  var rendererOptions = {
    map: map
  }
  directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);
  /*directionsDisplay = new google.maps.DirectionsRenderer();
  directionsDisplay.setMap(map);*/

  detectBrowser();

  // Instantiate an info window to hold step text.
  stepDisplay = new google.maps.InfoWindow();

  directionsDisplay.setPanel(document.getElementById("directions-panel"));

  //var control = document.getElementById('control');
  //control.style.display = 'inline-block';
  //map.controls[google.maps.ControlPosition.TOP_CENTER].push(control);
}

function detectBrowser() {
  var useragent = navigator.userAgent;
  var mapdiv = document.getElementById("map-canvas");

  if (useragent.indexOf('iPhone') != -1 || useragent.indexOf('Android') != -1 ) {
    mapdiv.style.width = '100%';
    mapdiv.style.height = '300px';
  } else {
    mapdiv.style.width = '100%';
    mapdiv.style.height = '600px';
  }
}

function calcRoute() {
  // First, clear out any existing markerArray
  // from previous calculations.
  for (i = 0; i < markerArray.length; i++) {
    markerArray[i].setMap(null);
  }

  // Now, clear the array itself.
  markerArray = [];

    // Create a DirectionsRequest
  var selectedMode = document.getElementById('mode').value;
  if (selectedMode == "JOGGING" || selectedMode == "RUNNING") {
      selectedMode = "WALKING";
  }
  var start = document.getElementById('start').value;
  var end = document.getElementById('end').value;

  // Add waypoints to request.
  var waypts = [];
  var x = document.getElementById('waypoints').value;
//  var x = "";
  if (x != "") {
      waypts.push({
          location: document.getElementById('waypoints').value,
          stopover: true
      });
  }
  /*var checkboxArray = document.getElementById('waypoints');
  for (var i = 0; i < checkboxArray.length; i++) {
    if (checkboxArray.options[i].selected == true) {
      waypts.push({
          location:checkboxArray[i].value,
          stopover:true});
    }
  }*/

    var request = {
        origin: start,
        destination: end,
        waypoints: waypts,
        optimizeWaypoints: true,
        // Note that Javascript allows us to access the constant
        // using square brackets and a string value as its
        // "property."
        travelMode: google.maps.TravelMode[selectedMode]
    };

  // Route the directions and pass the response to a
  // function to create markers for each step.
  directionsService.route(request, function(response, status) {
    if (status == google.maps.DirectionsStatus.OK) {
      var warnings = document.getElementById("warnings_panel");
      //warnings.innerHTML = "" + response.routes[0].warnings + "";
      //warnings.innerHTML = '<b>' + response.routes[0].warnings + '</b>';
      directionsDisplay.setDirections(response);
      showSteps(response);

      // Display Waypoints
      /*
       * var route = response.routes[0];
      var summaryPanel = document.getElementById('waypoints_routes_panel');
      summaryPanel.innerHTML = '';
      // For each route, display summary information.
      for (var i = 0; i < route.legs.length; i++) {
          var routeSegment = i + 1;
          summaryPanel.innerHTML += '<b>Route Segment: ' + routeSegment + '</b><br>';
          summaryPanel.innerHTML += route.legs[i].start_address + ' to ';
          summaryPanel.innerHTML += route.legs[i].end_address + '<br>';
          summaryPanel.innerHTML += route.legs[i].distance.text + '<br><br>';
      }
      */
    }
  });

  // Make the directions and waypoints section visible
  var dirs = document.getElementById('directions-panel');
  dirs.style.display = 'initial';
  //var ways = document.getElementById('waypoints_routes_panel');
  //ways.style.display = 'initial';
  
  
//  window.location.assign("newjsp.jsp?start_lat="+start_loc_lat+"&"+
//                                     "start_lng="+start_loc_lng+"&"+
//                                     "end_lat="+end_loc_lat+"&"+
//                                     "end_lng="+end_loc_lng);

}

function showSteps(directionResult) {
    // For each step, place a marker, and add the text to the marker's
    // info window. Also attach the marker to an array so we
    // can keep track of it and remove it when calculating new
    // routes.
    var myRoute = directionResult.routes[0].legs[0];

    for (var i = 0; i < myRoute.steps.length; i++) {
        var marker = new google.maps.Marker({
          position: myRoute.steps[i].start_point,
          map: map
        });
        attachInstructionText(marker, myRoute.steps[i].instructions);
        markerArray[i] = marker;
    }
}

function attachInstructionText(marker, text) {
    google.maps.event.addListener(marker, 'click', function() {
      // Open an info window when the marker is clicked on,
      // containing the text of the step.
      stepDisplay.setContent(text);
      stepDisplay.open(map, marker);
    });
}

// Google Maps Geocoding Service
function codeAddress() {
  var start_address = document.getElementById('start').value;
  geocoder.geocode( { 'address': start_address}, function(results, status) {
    if (status == google.maps.GeocoderStatus.OK) {
//        var uv = document.getElementById('uvcalc');
//        uv.innerHTML += 'Start Latitude: ' + results[0].geometry.location.lat() + '<br>';
//        uv.innerHTML += 'Start Longitude: ' + results[0].geometry.location.lng() + '<br>';
        start_loc_lat = results[0].geometry.location.lat();
        start_loc_lng = results[0].geometry.location.lng();
        //document.getElementById('start_lat').innerHTML = results[0].geometry.location.lat();
        //document.getElementById('start_lat_new').value = results[0].geometry.location.lat();
        document.getElementsByName('start_lat')[0].setAttribute('value', results[0].geometry.location.lat());
        document.getElementsByName('start_lng')[0].setAttribute('value', results[0].geometry.location.lng());
      /*
      map.setCenter(results[0].geometry.location);
      var marker = new google.maps.Marker({
          map: map,
          position: results[0].geometry.location
      });
      */
      
    } else {
      alert('codeAddress(): Geocode was not successful with Start location for the following reason: ' + status);
    }
  });
  var end_address = document.getElementById('end').value;
  geocoder.geocode( { 'address': end_address}, function(results, status) {
    if (status == google.maps.GeocoderStatus.OK) {
//        var uv = document.getElementById('uvcalc');
//        uv.innerHTML += 'End Latitude: ' + results[0].geometry.location.lat() + '<br>';
//        uv.innerHTML += 'End Longitude: ' + results[0].geometry.location.lng();
        end_loc_lat = results[0].geometry.location.lat();
        end_loc_lng = results[0].geometry.location.lng();
        document.getElementsByName('end_lat')[0].setAttribute('value', results[0].geometry.location.lat());
        document.getElementsByName('end_lng')[0].setAttribute('value', results[0].geometry.location.lng());
    } else {
      alert('codeAddress(): Geocode was not successful with End location for the following reason: ' + status);
    }
  });
//  window.location.assign("newjsp.jsp?start_lat="+start_loc_lat+"&"+
//                                     "start_lng"+start_loc_lng+"&"+
//                                     "end_lat"+end_loc_lat+"&"+
//                                     "end_lng"+end_loc_lng);

}

function indexWrapper() {
    codeAddress();
}

function responseWrapper() {
    calcRoute();
}

google.maps.event.addDomListener(window, 'load', initialize);
