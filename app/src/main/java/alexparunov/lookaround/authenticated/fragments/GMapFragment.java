package alexparunov.lookaround.authenticated.fragments;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import alexparunov.lookaround.R;
import alexparunov.lookaround.authenticated.utils.MapUtils;
import alexparunov.lookaround.events.Coordinates;
import alexparunov.lookaround.events.Event;
import alexparunov.lookaround.events.Time;
import alexparunov.lookaround.events.utils.DBConstants;
import alexparunov.lookaround.events.utils.FireBaseDB;

public class GMapFragment extends MapFragment {

  boolean isStartTimeSet = false;
  boolean isEndTimeSet = false;
  FireBaseDB fireBaseDB;
  DatabaseReference databaseReference;
  private EditText etTitle;
  private EditText etDescription;
  private TextView tvTimeStart;
  private TextView tvTimeEnd;
  private String tag = "";
  private String title = "";
  private String description = "";
  private Time timeStart = new Time();
  private Time timeEnd = new Time();

  OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
    @Override
    public void onMapReady(final GoogleMap googleMap) {
      final MapUtils mapUtils = new MapUtils(getContext());
      mapUtils.initializeMapUI(googleMap);

      if (databaseReference != null) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
          HashMap<String, Event> eventsHashMap = new HashMap<>();

          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
              if (!eventsHashMap.containsKey(eventSnapshot.getKey())) {
                eventsHashMap.put(eventSnapshot.getKey(), eventSnapshot.getValue(Event.class));
              }
            }
            mapUtils.initializeMarkers(eventsHashMap, googleMap);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
          @Override
          public void onMapLongClick(LatLng latLng) {
            showInputDialog(latLng);
          }
        });
      }
    }
  };

  public GMapFragment() {
    getMapAsync(onMapReadyCallback);
  }

  @Override
  public void onActivityCreated(Bundle bundle) {
    super.onActivityCreated(bundle);
    fireBaseDB = new FireBaseDB();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    if (firebaseUser != null) {
      databaseReference = FirebaseDatabase.getInstance().getReference()
          .child(DBConstants.DB_CHILD_EVENTS).child(firebaseUser.getUid());
    }
  }

  private void showInputDialog(final LatLng latLng) {
    isStartTimeSet = false;
    isEndTimeSet = false;

    LayoutInflater layoutInflater = LayoutInflater.from(getContext());
    View promtView = layoutInflater.inflate(R.layout.fragment_gmap_input_dialog, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
    alertDialogBuilder.setView(promtView);
    alertDialogBuilder.setTitle("New Event");

    etTitle = (EditText) promtView.findViewById(R.id.fragment_gmap_input_dialog_TitleET);
    etDescription = (EditText) promtView.findViewById(R.id.fragment_gmap_input_dialog_DescriptionET);
    Spinner tagsSpinner = (Spinner) promtView.findViewById(R.id.fragment_gmap_input_dialog_TagsSpinner);
    tvTimeStart = (TextView) promtView.findViewById(R.id.fragment_gmap_input_dialog_TimeStartTV);
    tvTimeEnd = (TextView) promtView.findViewById(R.id.fragment_gmap_input_dialog_TimeEndTV);

    if (etTitle == null || etDescription == null || tagsSpinner == null || tvTimeStart == null || tvTimeEnd == null)
      return;

    tagsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //We have Category... string here
        if (position != 0)
          tag = parent.getSelectedItem().toString().trim();
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
        }, hour, minutes, true);

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
            tvTimeEnd.setText("Ending time - " + timeEnd.toString());
          }
        }, hour + 1, minutes, true);

        timePicker.setTitle("End Time");
        timePicker.show();
      }
    });

    alertDialogBuilder.setCancelable(false)
        .setPositiveButton("Create Event", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

            if (!timeStart.isBefore(timeEnd) && isStartTimeSet && isEndTimeSet) {
              Toast.makeText(getContext(), "Starting time should be before ending time", Toast.LENGTH_SHORT).show();
              return;
            }

            title = etTitle.getText().toString().trim();
            description = etDescription.getText().toString().trim();

            if (title.isEmpty()) {
              Toast.makeText(getContext(), "Title is required", Toast.LENGTH_SHORT).show();
              return;
            }

            if (!isStartTimeSet) {
              Toast.makeText(getContext(), "Starting time is required", Toast.LENGTH_SHORT).show();
              return;
            }

            Event event = new Event(new Coordinates(latLng.latitude, latLng.longitude),
                timeStart, timeEnd, title,
                description, tag, new Date());

            if (fireBaseDB != null) {
              fireBaseDB.insertEvent(getActivity(), event);
              getMapAsync(onMapReadyCallback);
            }
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
