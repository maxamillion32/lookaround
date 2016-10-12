package alexparunov.lookaround.authenticated.fragments;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import java.util.Calendar;

import alexparunov.lookaround.R;
import alexparunov.lookaround.events.Time;


public class GMapFragment extends MapFragment implements GoogleMap.OnMapLongClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private EditText etTitle;
    private EditText etDescription;
    private TextView tvTimeStart;
    private TextView tvTimeEnd;

    private String category = "";
    private String title = "";
    private String description = "";
    private Time timeStart = new Time();
    private Time timeEnd = new Time();
    boolean isStartTimeSet = false;
    boolean isEndTimeSet = false;

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
        tvTimeStart = (TextView) promtView.findViewById(R.id.fragment_gmap_input_dialog_TimeStartTV);
        tvTimeEnd = (TextView) promtView.findViewById(R.id.fragment_gmap_input_dialog_TimeEndTV);

        if(etTitle==null||etDescription==null||tagsSpinner==null||tvTimeStart==null||tvTimeEnd==null)
            return;

        tagsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //We have Category... string here
                if(position != 0)
                    category = parent.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                final int minutes = currentTime.get(Calendar.MINUTE);

                TimePickerDialog timePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeStart.setHours(hourOfDay);
                        timeStart.setMinutes(minute);
                        isStartTimeSet = true;
                        tvTimeStart.setText("Starting time - " + timeStart.toString());
                    }
                },hour,minutes,true);

                timePicker.setTitle("Start Time");
                timePicker.show();
            }
        });

        tvTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minutes = currentTime.get(Calendar.MINUTE);

                TimePickerDialog timePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeEnd.setHours(hourOfDay);
                        timeEnd.setMinutes(minute);
                        isEndTimeSet = true;
                        tvTimeEnd.setText("Ending time - "+ timeEnd.toString());
                    }
                },hour,minutes,true);

                timePicker.setTitle("End Time");
                timePicker.show();
            }
        });

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Create Event", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(!timeStart.isBefore(timeEnd) && isStartTimeSet && isEndTimeSet) {
                            Toast.makeText(getContext(),"Starting time should be before ending time",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        isStartTimeSet = false;
                        isEndTimeSet = false;
                        title = etTitle.getText().toString().trim();
                        description = etDescription.getText().toString().trim();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isStartTimeSet = false;
                        isEndTimeSet = false;

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
