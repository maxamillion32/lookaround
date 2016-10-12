package alexparunov.lookaround.authenticated.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import alexparunov.lookaround.R;


public class GMapFragment extends MapFragment implements GoogleMap.OnMapLongClickListener {

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

            map.setOnMapLongClickListener(this);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        showInputDialog();
    }

    private void showInputDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promtView = layoutInflater.inflate(R.layout.fragment_gmap_input_dialog,null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promtView);
        alertDialogBuilder.setTitle("New Event");

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Create Event", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
