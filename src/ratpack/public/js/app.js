L.mapbox.accessToken = "pk.eyJ1IjoidXJpczc3IiwiYSI6InRuYTZRa3MifQ._Bo-JRcA7QVGocCJvdSoJg";
var map = L.mapbox.map("map", "uris77.jdp8mk3h");
var featureLayer = L.mapbox.featureLayer().addTo(map);

$.getJSON("/api/locations").then(function(it) {
    featureLayer.setGeoJSON(it);
});
