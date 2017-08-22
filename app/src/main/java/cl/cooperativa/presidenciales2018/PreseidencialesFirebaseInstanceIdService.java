package cl.cooperativa.presidenciales2018;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by innova6 on 21-08-2017.
 */

public class PreseidencialesFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "InstanceIdService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.w(TAG,"TokenRefresh: "+token);
    }
}
