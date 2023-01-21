package com.example.pigeon.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.pigeon.R;
import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity {

    MapView mapView;
    private GoogleMap map;
    private LocationManager locationManager;
    private Location currentLocation;
    private double latitude;
    private double longitude;
    private double customerLatitude;
    private double customerLongitude;
    private String apiKey;
    private static final int REQUEST_LOCATION_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Try to get the last known location
        currentLocation = null;
        try {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException ex) {
            // Handle the exception
        }

        // If no last known location is available, try to get the current location
        if (currentLocation == null) {
            try {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        // Save the location when it is received
                        currentLocation = location;
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        // Handle the change in status
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        // Handle the provider being enabled
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        // Handle the provider being disabled
                    }
                }, null);
            } catch (SecurityException ex) {
                // Handle the exception
            }
        }


        latitude = getIntent().getDoubleExtra("jobLatitude", 0);
        longitude = getIntent().getDoubleExtra("jobLongitude", 0);
        MapView mapView = findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Save a reference to the GoogleMap object
                map = googleMap;

                // Set up the map as needed
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions
                            (MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                } else {
                    // Permission has already been granted, get the location
                    getLocation();
                }
                map.setMyLocationEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setCompassEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);

                // Add the route to the map
                addRouteToMap();
            }
        });


    }

    private void addRouteToMap() {
        LatLng start = new LatLng(latitude, longitude);
        LatLng end = new LatLng(customerLatitude, customerLongitude);

        // Use the Google Maps Android API to get the route between the two locations
        List<com.google.maps.model.LatLng> route = getRouteBetweenLocations(start, end);

        // Add the points to the map as a polyline
        PolylineOptions options = new PolylineOptions()
                .color(Color.RED);

    }

    private List<com.google.maps.model.LatLng> getRouteBetweenLocations(LatLng start, LatLng end) {
        List<com.google.maps.model.LatLng> route = new ArrayList<>();

        try {
            apiKey="AIzaSyAq2SdNDDQMzP6hdmkDouPPPQtjFu0ELQg";
            // Initialize the GeoApiContext with the API key
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(apiKey)
                    .build();

            // Get the directions result
            DirectionsResult result = DirectionsApi.newRequest(context)
                    .mode(TravelMode.DRIVING)
                    .origin(String.valueOf(start))
                    .destination(String.valueOf(end))
                    .await();

            // Extract the points from the route and add them to the list
            if (result.routes.length > 0) {
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(result.routes[0].overviewPolyline.getEncodedPath());
                route.addAll(decodedPath);
            }
        } catch (ApiException ex) {
            // Handle the API exception
        } catch (InterruptedException ex) {
            // Handle the interrupted exception
        } catch (IOException ex) {
            // Handle the I/O exception
        }

        return route;
    }}


