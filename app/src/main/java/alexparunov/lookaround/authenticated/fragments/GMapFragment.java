package alexparunov.lookaround.authenticated.fragments;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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

import android.util.Log;

import java.util.HashMap;

import alexparunov.lookaround.R;
import alexparunov.lookaround.authenticated.utils.MapUtils;
import alexparunov.lookaround.events.Event;
import alexparunov.lookaround.events.utils.DBConstants;
import alexparunov.lookaround.events.utils.FireBaseDB;

public class GMapFragment extends MapFragment {

  private FireBaseDB fireBaseDB;
  private DatabaseReference databaseReference;
  private String userId;
  private Place mPlace;
  private GMapFragment gMapFragment;

  OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
    @Override
    public void onMapReady(final GoogleMap googleMap) {
      fireBaseDB = new FireBaseDB();
      FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
      FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
      PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
          getFragmentManager().findFragmentById(R.id.acivity_auth_autocomplete_fragment);

      autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
        @Override
        public void onPlaceSelected(Place place) {
          Log.d("GMAP", place.getName() + ": selected");
          mPlace = place;
          getMapAsync(onMapReadyCallback);
        }

        @Override
        public void onError(Status status) {

        }
      });
      if (firebaseUser != null) {
        databaseReference = FirebaseDatabase.getInstance().getReference()
            .child(DBConstants.DB_CHILD_EVENTS);
        userId = firebaseUser.getUid();
      }

      final MapUtils mapUtils = new MapUtils(getContext());
      mapUtils.initializeMapUI(mPlace, googleMap);

      if (databaseReference != null) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
          HashMap<String, Event> eventsHashMap = new HashMap<>();

          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
              for (DataSnapshot eventSnapshot : userSnapshot.getChildren()) {
                if (fireBaseDB != null) {
                  fireBaseDB.removeEvents(userId, userSnapshot, eventSnapshot);
                }
                if (!eventsHashMap.containsKey(eventSnapshot.getKey())) {
                  eventsHashMap.put(eventSnapshot.getKey(), eventSnapshot.getValue(Event.class));
                }
              }
              mapUtils.initializeMarkers(mPlace, eventsHashMap, googleMap);
            }
          }
          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
          @Override
          public void onMapLongClick(LatLng latLng) {
            mapUtils.showInputDialog(getActivity(), gMapFragment,
                onMapReadyCallback, fireBaseDB, latLng);
          }
        });
      }
    }
  };

  public GMapFragment() {
    getMapAsync(onMapReadyCallback);
    gMapFragment = this;
  }
}
