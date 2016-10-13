package alexparunov.lookaround.authenticated.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapUtils {

    private Context context;
    private double latitude = 0;
    private double longitude = 0;

    public MapUtils(Context context) {
        this.context = context;

        GPSTracker gpsTracker = new GPSTracker(context);

        if(gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingAlert();
        }

        gpsTracker.stopUsingGPS(context);
    }

    public void initializeMarkers(GoogleMap googleMap) {

        if(latitude != 0 && longitude != 0) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Me"));

        }
    }

    public void initializeMapUI(GoogleMap googleMap){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        CameraUpdate cameraPosition = null;

        if(latitude != 0 && longitude != 0) {
            cameraPosition = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
        }
        if(cameraPosition == null) {
            return;
        }
        googleMap.moveCamera(cameraPosition);
        googleMap.animateCamera(cameraPosition);

    }

}
