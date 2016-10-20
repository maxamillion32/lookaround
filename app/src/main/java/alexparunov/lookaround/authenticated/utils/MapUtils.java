package alexparunov.lookaround.authenticated.utils;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
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
import alexparunov.lookaround.authenticated.fragments.GMapFragment;
import alexparunov.lookaround.events.Coordinates;
import alexparunov.lookaround.events.DateTime;
import alexparunov.lookaround.events.Event;
import alexparunov.lookaround.events.utils.FireBaseDB;

public class MapUtils {

  private Context context;
  private boolean isMyMarkerAdded = false;
  private boolean isStartDateTimeSet;
  private boolean isEndDateTimeSet;
  private EditText etTitle;
  private EditText etDescription;
  private TextView tvDateTimeStart;
  private TextView tvDateTimeEnd;
  private String tag = "";
  private String title = "";
  private String description = "";
  private DateTime dateTimeStart = new DateTime();
  private DateTime dateTimeEnd = new DateTime();

  public MapUtils(Context context) {
    this.context = context;
  }

  public void initializeMarkers(Place place, HashMap<String, Event> eventsHashMap, GoogleMap googleMap) {
    if (context == null) {
      return;
    }

    double latitude;
    double longitude;
    CharSequence title;

    if (place == null) {
      GPSTracker gpsTracker = new GPSTracker(context);

      if (!gpsTracker.canGetLocation()) {
        gpsTracker.showSettingAlert();
        return;
      }

      latitude = gpsTracker.getLatitude();
      longitude = gpsTracker.getLongitude();
      gpsTracker.stopUsingGPS(context);
      title = "Me";
    } else {
      latitude = place.getLatLng().latitude;
      longitude = place.getLatLng().longitude;
      title = place.getName();
    }

    if (!isMyMarkerAdded) {
      googleMap.addMarker(new MarkerOptions()
          .position(new LatLng(latitude, longitude))
          .title(title.toString()));
      isMyMarkerAdded = true;
    }

    for (Event event : eventsHashMap.values()) {
      if (distance(latitude, longitude,
          event.getCoordinates().getLatitude(), event.getCoordinates().getLongitude()) < 60) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(event.getTitle());
        double latitude1 = event.getCoordinates().getLatitude();
        double longitude1 = event.getCoordinates().getLongitude();
        markerOptions.position(new LatLng(latitude1, longitude1));
        markerOptions.alpha(0.9f);

        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(event);
      }
    }
  }

  public void initializeMapUI(Place place, GoogleMap googleMap) {
    if (context == null) {
      return;
    }

    double latitude;
    double longitude;

    if (place == null) {
      GPSTracker gpsTracker = new GPSTracker(context);
      if (!gpsTracker.canGetLocation()) {
        gpsTracker.showSettingAlert();
        return;
      }
      latitude = gpsTracker.getLatitude();
      longitude = gpsTracker.getLongitude();
      gpsTracker.stopUsingGPS(context);
    } else {
      latitude = place.getLatLng().latitude;
      longitude = place.getLatLng().longitude;
    }
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      googleMap.setMyLocationEnabled(true);
    }

    UiSettings uiSettings = googleMap.getUiSettings();
    uiSettings.setZoomControlsEnabled(true);
    uiSettings.setMyLocationButtonEnabled(true);

    CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
    googleMap.moveCamera(cameraPosition);
    googleMap.animateCamera(cameraPosition);

    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
      }
    });
    googleMap.setInfoWindowAdapter(new InfoWindowAdapter());
  }

  //The distance between two points in km using Haversine Formula
  private double distance(double lat1, double long1, double lat2, double long2) {
    double radius = 6378.1;
    lat1 = Math.toRadians(lat1);
    long1 = Math.toRadians(long1);
    lat2 = Math.toRadians(lat2);
    long2 = Math.toRadians(long2);

    double temp = Math.sqrt(Math.sin((lat2 - lat1) / 2) * Math.sin((lat2 - lat1) / 2) +
        Math.cos(lat1) * Math.cos(lat2) *
            Math.sin((long2 - long1) / 2) * Math.sin((long2 - long1) / 2));
    return 2 * radius * Math.asin(temp);
  }

  private double[] getLatLong(double lat1, double long1, double dist) {
    double radius = 6378.1;
    double brg = 1.57; //bearing is 90 degrees in radians

    double result[] = new double[2];
    lat1 = Math.toRadians(lat1);
    long1 = Math.toRadians(long1);

    double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist / radius) +
        Math.cos(lat1) * Math.sin(dist / radius) * Math.cos(brg));

    double long2 = long1 + Math.atan2(Math.sin(brg) * Math.sin(dist / radius) * Math.cos(lat1),
        Math.cos(dist / radius) - Math.sin(lat1) * Math.sin(lat2));
    lat2 = Math.toDegrees(lat2);
    long2 = Math.toDegrees(long2);
    result[0] = lat2;
    result[1] = long2;
    return result;
  }

  private class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private View infoWindowView;

    InfoWindowAdapter() {
      if (context != null) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        infoWindowView = layoutInflater.inflate(R.layout.fragment_gmap_info_window, null);
      }
    }

    @Override
    public View getInfoWindow(Marker marker) {
      Event event = (Event) marker.getTag();
      if (event == null) {
        return null;
      }
      if (infoWindowView == null) {
        return null;
      }

      final TextView tvTitle = (TextView) infoWindowView.findViewById(R.id.fragment_gmap_info_window_TitleTV);
      final TextView tvDescription = (TextView) infoWindowView.findViewById(R.id.fragment_gmap_info_window_DescriptionTV);
      final TextView tvTimes = (TextView) infoWindowView.findViewById(R.id.fragment_gmap_info_window_TimesTV);

      if (tvTitle == null || tvDescription == null || tvTimes == null) {
        return null;
      }

      final String title = event.getTitle();
      final String description = event.getDescription();
      String times = event.getStartTime().toString() + " - ";
      if (event.getEndTime().getHours() != 0 && event.getEndTime().getMinutes() != 0) {
        times = event.getStartTime().toString() + " - " + event.getEndTime().toString();
      }

      tvTitle.setText(title);
      tvDescription.setText(description);
      tvTimes.setText(times);

      return infoWindowView;
    }

    @Override
    public View getInfoContents(Marker marker) {
      if (marker != null && marker.isInfoWindowShown()) {
        marker.hideInfoWindow();
        marker.showInfoWindow();
      }
      return null;
    }
  }

  public void showInputDialog(final Activity activity,
                              final GMapFragment gMapFragment,
                              final OnMapReadyCallback onMapReadyCallback,
                              final FireBaseDB fireBaseDB,
                              final LatLng latLng) {
    if (context == null || activity == null || gMapFragment == null ||
        onMapReadyCallback == null || latLng == null) {
      return;
    }
    isStartDateTimeSet = false;
    isEndDateTimeSet = false;

    LayoutInflater layoutInflater = LayoutInflater.from(context);
    View promtView = layoutInflater.inflate(R.layout.fragment_gmap_input_dialog, null);
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    alertDialogBuilder.setView(promtView);
    alertDialogBuilder.setTitle("New Event");

    etTitle = (EditText) promtView.findViewById(R.id.fragment_gmap_input_dialog_TitleET);
    etDescription = (EditText) promtView.findViewById(R.id.fragment_gmap_input_dialog_DescriptionET);
    Spinner tagsSpinner = (Spinner) promtView.findViewById(R.id.fragment_gmap_input_dialog_TagsSpinner);
    tvDateTimeStart = (TextView) promtView.findViewById(R.id.fragment_gmap_input_dialog_DateTimeStartTV);
    tvDateTimeEnd = (TextView) promtView.findViewById(R.id.fragment_gmap_input_dialog_DateTimeEndTV);

    if (etTitle == null || etDescription == null || tagsSpinner == null
        || tvDateTimeStart == null || tvDateTimeEnd == null) {
      return;
    }

    tagsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //We have Category... string here
        if (position != 0)
          tag = parent.getSelectedItem().toString().trim().toLowerCase();
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTimeStart.setDate(dayOfMonth);
                dateTimeStart.setMonth(monthOfYear);

                TimePickerDialog timePicker = new TimePickerDialog(activity,
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTimeEnd.setDate(dayOfMonth);
                dateTimeEnd.setMonth(monthOfYear);

                TimePickerDialog timePicker = new TimePickerDialog(activity,
                    new TimePickerDialog.OnTimeSetListener() {
                      @Override
                      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateTimeEnd.setHours(hourOfDay);
                        dateTimeEnd.setMinutes(minute);
                        isEndDateTimeSet = true;
                        tvDateTimeEnd.setText("Ending date and time - " + dateTimeEnd.toString());
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
            Calendar nCal = Calendar.getInstance();
            DateTime now = new DateTime(nCal.get(Calendar.MONTH), nCal.get(Calendar.DAY_OF_MONTH),
                nCal.get(Calendar.HOUR_OF_DAY), nCal.get(Calendar.MINUTE));

            if (dateTimeStart.isBefore(now)) {
              Toast.makeText(context, "Starting date and time is outdated", Toast.LENGTH_SHORT).show();
              return;
            }
            if (!dateTimeStart.isBefore(dateTimeEnd) && isStartDateTimeSet && isEndDateTimeSet) {
              Toast.makeText(context, "Starting date and time should be before ending date and time", Toast.LENGTH_SHORT).show();
              return;
            }

            title = etTitle.getText().toString().trim();
            description = etDescription.getText().toString().trim();

            if (title.isEmpty()) {
              Toast.makeText(context, "Title is required", Toast.LENGTH_SHORT).show();
              return;
            }

            if (!isStartDateTimeSet) {
              Toast.makeText(context, "Starting date and time is required", Toast.LENGTH_SHORT).show();
              return;
            }

            Event event = new Event(new Coordinates(latLng.latitude, latLng.longitude),
                dateTimeStart, dateTimeEnd, title,
                description, tag, new Date());

            if (fireBaseDB != null) {
              fireBaseDB.insertEvent(activity, event);
              gMapFragment.getMapAsync(onMapReadyCallback);
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
