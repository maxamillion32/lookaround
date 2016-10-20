package alexparunov.lookaround.events.utils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.Calendar;

import alexparunov.lookaround.events.DateTime;
import alexparunov.lookaround.events.Event;
import alexparunov.lookaround.events.User;

public class FireBaseDB {

  private FirebaseUser firebaseUser;
  private DatabaseReference DBReferenceEventsUser;
  private DatabaseReference DBReference;

  public FireBaseDB() {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    firebaseUser = firebaseAuth.getCurrentUser();
    DBReferenceEventsUser = FirebaseDatabase.getInstance().getReference()
        .child(DBConstants.DB_CHILD_EVENTS).child(firebaseUser.getUid());
    DBReference = FirebaseDatabase.getInstance().getReference()
        .child(DBConstants.DB_CHILD_EVENTS);
  }

  public void insertEvent(final Activity activity, Event event) {
    if (firebaseUser == null || DBReferenceEventsUser == null) {
      return;
    }
    User user = new User();
    user.setEmail(firebaseUser.getEmail());
    if (firebaseUser.getDisplayName() != null) {
      user.setName(firebaseUser.getDisplayName());
    }
    user.setUserId(firebaseUser.getUid());

    event.setCreatedBy(user);

    DBReferenceEventsUser.push().setValue(event)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
              Toast.makeText(activity, "Event was successfully created!", Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(activity, "Error occured while creating event. Please check Internet connection.", Toast.LENGTH_SHORT).show();
            }
          }
        });
  }

  public void removeEvents(String userId, DataSnapshot userSnapshot, DataSnapshot eventSnapshot) {
    //Remove own finished events
    if (userId != null && userSnapshot != null && eventSnapshot != null) {
      if (userId.equals(userSnapshot.getKey())) {
        Event event = eventSnapshot.getValue(Event.class);
        Calendar nCal = Calendar.getInstance();
        DateTime now = new DateTime(nCal.get(Calendar.MONTH), nCal.get(Calendar.DAY_OF_MONTH),
            nCal.get(Calendar.HOUR_OF_DAY), nCal.get(Calendar.MINUTE));
        DateTime end = event.getEndTime();
        //End date does not exist
        if (end.getDate() == 0) {
          DateTime start = event.getStartTime();
          DateTime nextDay = new DateTime(start.getMonth(), start.getDate() + 1,
              start.getHours(), start.getMinutes());
          if (nextDay.isBefore(now)) {
            DBReference.child(userId).child(eventSnapshot.getKey()).removeValue();
          }
        } else {
          if (end.isBefore(now)) {
            DBReference.child(userId).child(eventSnapshot.getKey()).removeValue();
          }
        }
      }
    }
  }

}
