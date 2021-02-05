package com.example.admin.printqr;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String productID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        productID = getIntent().getStringExtra("ProductID");


        findViewById(R.id.reportBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportItem(productID);
            }
        });

    }







    String reportID,userID;
    private void reportItem(String productID) {

        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Reports");

        reportID = reportsRef.push().getKey();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap map = new HashMap();

        map.put("productID",productID);
        map.put("reporter", userID);
        map.put("locationLat",mLastLocation.getLatitude());
        map.put("locationLong",mLastLocation.getLongitude());


        reportsRef.updateChildren(map);
    }


    private static final int REQUEST_CODE = 101;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;

        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }






    Marker myLocationMarker;
    LatLng myLatLng;
    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {

            for (Location location : locationResult.getLocations()){

                if (getBaseContext() != null) {

                    mLastLocation = location;
                    myLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if(myLocationMarker != null){

                        myLocationMarker.remove();
                    }

                    myLocationMarker = mMap.addMarker(new MarkerOptions().position(myLatLng).title("Your Location"));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                }

            }
        }
    };
}