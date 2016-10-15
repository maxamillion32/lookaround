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
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
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
import alexparunov.lookaround.events.DateTime;
import alexparunov.lookaround.events.Event;
import alexparunov.lookaround.events.utils.DBConstants;
import alexparunov.lookaround.events.utils.FireBaseDB;

public class GMapFragment extends MapFragment {

  boolean isStartDateTimeSet = false;
  boolean isEndDateTimeSet = false;
  FireBaseDB fireBaseDB;
  DatabaseReference databaseReference;
  private EditText etTitle;
  private EditText etDescription;
  private TextView tvDateTimeStart;
  private TextView tvDateTimeEnd;
  private String tag = "";
  private String title = "";
  private String description = "";
  private DateTime dateTimeStart = new DateTime();
  private DateTime dateTimeEnd = new DateTime();

  OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
    @Override
    public void onMapReady(final GoogleMap googleMap) {
      fireBaseDB = new FireBaseDB();
      FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
      FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
      if (firebaseUser != null) {
        databaseReference = FirebaseDatabase.getInstance().getReference()
            .child(DBConstants.DB_CHILD_EVENTS);
      }

      final MapUtils mapUtils = new MapUtils(getContext());
      mapUtils.initializeMapUI(googleMap);

      if (databaseReference != null) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
          HashMap<String, Event> eventsHashMap = new HashMap<>();

          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
              for (DataSnapshot eventSnapshot : userSnapshot.getChildren()) {
                if (!eventsHashMap.containsKey(eventSnapshot.getKey())) {
                  eventsHashMap.put(eventSnapshot.getKey(), eventSnapshot.getValue(Event.class));
                }
              }
              mapUtils.initializeMarkers(eventsHashMap, googleMap);
            }
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

  private void showInputDialog(final LatLng latLng) {
    isStartDateTimeSet = false;
    isEndDateTimeSet = false;

    LayoutInflater layoutInflater = LayoutInflater.from(getContext());
    View promtView = layoutInflater.inflate(R.layout.fragment_gmap_input_dialog, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
    alertDialogBuilder.setView(promtView);
    alertDialogBuilder.setTitle("New Event");

    etTitle = (EditText) promtView.findViewById(R.id.fragment_gmap_input_dialog_TitleET);
    etDescription = (EditText) promtView.findViewById(R.id.fragment_gmap_input_dialog_DescriptionET);
    Spinner tagsSpinner = (Spinner) promtView.findViewById(R.id.fragment_gmap_input_dialog_TagsSpinner);
    tvDateTimeStart = (TextView) promtView.findViewById(R.id.fragment_gmap_input_dialog_DateTimeStartTV);
    tvDateTimeEnd = (TextView) promtView.findViewById(R.id.fragment_gmap_input_dialog_DateTimeEndTV);

    if (etTitle == null || etDescription == null || tagsSpinner == null || tvDateTimeStart == null || tvDateTimeEnd == null)
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

    tvDateTimeStart.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Calendar currentTime = Calendar.getInstance();
        final int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        final int minutes = currentTime.get(Calendar.MINUTE);
        int date = currentTime.get(Calendar.DAY_OF_MONTH);
        int month = currentTime.get(Calendar.MONTH);
        int year = currentTime.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTimeStart.setDate(dayOfMonth);
                dateTimeStart.setMonth(monthOfYear);

                TimePickerDialog timePicker = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {
                      @Override
                      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateTimeStart.setHours(hourOfDay);
                        dateTimeStart.setMinutes(minute);
                        isStartDateTimeSet = true;
                        tvDateTimeStart.setText("Starting date and time - " + dateTimeStart.toString());
                      }
                    }, hour, minutes, true);

                timePicker.setTitle("Starting Time");
                timePicker.show();
              }
            }, year, month, date);

        datePickerDialog.setTitle("Starting Date");
        datePickerDialog.show();
      }
    });

    tvDateTimeEnd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Calendar currentTime = Calendar.getInstance();
        final int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        final int minutes = currentTime.get(Calendar.MINUTE);
        int date = currentTime.get(Calendar.DAY_OF_MONTH);
        int month = currentTime.get(Calendar.MONTH);
        int year = currentTime.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTimeEnd.setDate(dayOfMonth);
                dateTimeEnd.setMonth(monthOfYear);

                TimePickerDialog timePicker = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {
                      @Override
                      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateTimeEnd.setHours(hourOfDay);
                        dateTimeEnd.setMinutes(minute);
                        isEndDateTimeSet = true;
                        tvDateTimeEnd.setText("Ending date and time - " + dateTimeStart.toString());
                      }
                    }, hour, minutes, true);

                timePicker.setTitle("Ending Time");
                timePicker.show();
              }
            }, year, month, date);

        datePickerDialog.setTitle("Ending Date");
        datePickerDialog.show();
      }
    });

    alertDialogBuilder.setCancelable(false)
        .setPositiveButton("Create Event", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

            if (!dateTimeStart.isBefore(dateTimeEnd) && isStartDateTimeSet && isEndDateTimeSet) {
              Toast.makeText(getContext(), "Starting date and time should be before ending date and time", Toast.LENGTH_SHORT).show();
              return;
            }

            title = etTitle.getText().toString().trim();
            description = etDescription.getText().toString().trim();

            if (title.isEmpty()) {
              Toast.makeText(getContext(), "Title is required", Toast.LENGTH_SHORT).show();
              return;
            }

            if (!isStartDateTimeSet) {
              Toast.makeText(getContext(), "Starting date and time is required", Toast.LENGTH_SHORT).show();
              return;
            }

            Event event = new Event(new Coordinates(latLng.latitude, latLng.longitude),
                dateTimeStart, dateTimeEnd, title,
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
