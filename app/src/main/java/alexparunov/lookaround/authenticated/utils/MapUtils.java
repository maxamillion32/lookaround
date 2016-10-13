package alexparunov.lookaround.authenticated.utils;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapUtils {

    public MapUtils() {}

    public void initializeMarkers(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(42.0209, 23.0943))
                .title("Marker"));
    }

    public void initializeMapUI(GoogleMap googleMap){
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(new LatLng(42.0209,23.0943), 15);
        googleMap.moveCamera(cameraPosition);
        googleMap.animateCamera(cameraPosition);

    }
}
