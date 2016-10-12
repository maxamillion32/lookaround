package alexparunov.lookaround.authenticated.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import alexparunov.lookaround.R;


public class GMapFragment extends MapFragment implements GoogleMap.OnMapLongClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private EditText etTitle;
    private EditText etDescription;

    private  String category = "";
    private String title = "";
    private String description = "";

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

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
        showInputDialog(latLng);
    }

    private void showInputDialog(LatLng latLng) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promtView = layoutInflater.inflate(R.layout.fragment_gmap_input_dialog,null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promtView);
        alertDialogBuilder.setTitle("New Event");

        etTitle = (EditText) promtView.findViewById(R.id.fragment_gmap_input_dialog_TitleET);
        etDescription = (EditText) promtView.findViewById(R.id.fragment_gmap_input_dialog_DescriptionET);
        Spinner tagsSpinner = (Spinner) promtView.findViewById(R.id.fragment_gmap_input_dialog_TagsSpinner);

        tagsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //We have Category... string here
                if(position == 0)
                    return;
                category = parent.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Create Event", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(etTitle == null || etDescription == null)
                            return;

                        title = etTitle.getText().toString().trim();
                        description = etDescription.getText().toString().trim();
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
