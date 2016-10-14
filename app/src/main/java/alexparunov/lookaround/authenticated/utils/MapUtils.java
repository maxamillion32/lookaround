package alexparunov.lookaround.authenticated.utils;

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
  private double latitude = 0;
  private double longitude = 0;

  public MapUtils(Context context) {
    this.context = context;

    GPSTracker gpsTracker = new GPSTracker(context);

    if (gpsTracker.canGetLocation()) {
      latitude = gpsTracker.getLatitude();
      longitude = gpsTracker.getLongitude();
    } else {
      gpsTracker.showSettingAlert();
    }

    gpsTracker.stopUsingGPS(context);
  }

  public void initializeMarkers(HashMap<String, Event> eventsHashMap, GoogleMap googleMap) {
    //User location
    if (latitude != 0 && longitude != 0) {
      googleMap.addMarker(new MarkerOptions()
          .position(new LatLng(latitude, longitude))
          .title("Me"));
    }

    for (Event event : eventsHashMap.values()) {
      MarkerOptions markerOptions = new MarkerOptions();
      markerOptions.title(event.getTitle());
      double latitude = event.getCoordinates().getLatitude();
      double longitude = event.getCoordinates().getLongitude();
      markerOptions.position(new LatLng(latitude, longitude));
      markerOptions.alpha(0.9f);

      Marker marker = googleMap.addMarker(markerOptions);
      marker.setTag(event);
    }
  }

  public void initializeMapUI(GoogleMap googleMap) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      googleMap.setMyLocationEnabled(true);
    }

    UiSettings uiSettings = googleMap.getUiSettings();
    uiSettings.setZoomControlsEnabled(true);
    uiSettings.setMyLocationButtonEnabled(true);

    if (latitude == 0 && longitude == 0) {
      return;
    }

    CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
    googleMap.moveCamera(cameraPosition);
    googleMap.animateCamera(cameraPosition);

    googleMap.setOnMarkerClickListener(new OnMarkerClickListener());
    googleMap.setInfoWindowAdapter(new InfoWindowAdapter());
  }

  private class OnMarkerClickListener implements GoogleMap.OnMarkerClickListener {

    @Override
    public boolean onMarkerClick(Marker marker) {
      marker.showInfoWindow();
      return false;
    }
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
