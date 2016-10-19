package alexparunov.lookaround.authenticated.utils;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import alexparunov.lookaround.R;
import alexparunov.lookaround.events.Event;

public class MapUtils {

  private Context context;
  private boolean isMyMarkerAdded = false;

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

    CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13);
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
}
