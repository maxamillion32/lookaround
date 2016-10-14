package alexparunov.lookaround.events.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import alexparunov.lookaround.events.Event;
import alexparunov.lookaround.events.User;

public class FireBaseDB {

    private FirebaseUser firebaseUser;
    private DatabaseReference DBReferenceEventsUser;

    public FireBaseDB() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        DBReferenceEventsUser = FirebaseDatabase.getInstance().getReference()
                .child(DBConstants.DB_CHILD_EVENTS).child(firebaseUser.getUid());
    }

    public void insertEvent(final Activity activity, Event event) {
        if(firebaseUser == null || DBReferenceEventsUser == null) {
            return;
        }

        User user = new User();
        user.setEmail(firebaseUser.getEmail());
        if(firebaseUser.getDisplayName() != null) {
            user.setName(firebaseUser.getDisplayName());
        }
        user.setUserId(firebaseUser.getUid());

        event.setCreatedBy(user);

        DBReferenceEventsUser.push().setValue(event)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activity,"Event was successfully inserted", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
