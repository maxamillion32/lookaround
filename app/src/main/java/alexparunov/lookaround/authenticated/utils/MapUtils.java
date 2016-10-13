package alexparunov.lookaround.authenticated.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapUtils {

    private Context context;
    public MapUtils(Context context) {
        this.context = context;
    }

    public void initializeMarkers(GoogleMap googleMap) {
        GPSTracker gpsTracker = new GPSTracker(context);
        double latitude = 0;
        double longitude = 0;

        if(gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingAlert();
        }

        if(latitude != 0 && longitude != 0) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Me"));

            gpsTracker.stopUsingGPS(context);
        }
    }

    public void initializeMapUI(GoogleMap googleMap){
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);

        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(new LatLng(42.0209,23.0943), 15);
        googleMap.moveCamera(cameraPosition);
        googleMap.animateCamera(cameraPosition);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

}
