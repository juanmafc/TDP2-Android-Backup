package fiubatdp2g1_hoycomo.hoycomo.service.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;

public class HoyComoFirebaseIDService extends FirebaseInstanceIdService {

    public static String GetFirebaseToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i("FIREBASE", "Refreshed token: " + refreshedToken);

        HoyComoUserProfile.setFirebaseToken( refreshedToken );

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //TODO: implement method to send the Firebase token to the NodeJs Server
        //sendRegistrationToServer(refreshedToken);
    }

}
