package alexparunov.lookaround.authenticated.fragments;

import android.app.ActionBar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class GMapFragment extends MapFragment {

    OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            initializeMarkers(googleMap);
            initializeMapUI(googleMap);
        }
    };

    public GMapFragment() {
        getMapAsync(onMapReadyCallback);
    }

    private void initializeMarkers(GoogleMap map) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(42.0209, 23.0943))
                    .title("Marker"));
    }

    private void initializeMapUI(GoogleMap map) {
        if(map != null) {
            UiSettings uiSettings = map.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);

            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(new LatLng(42.0209,23.0943), 15);
            map.moveCamera(cameraPosition);
            map.animateCamera(cameraPosition);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Events Nearby");
        }
    }
}
