package teq.development.seatech.Chat;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;

/**
 * Created by vibrantappz on 12/18/2017.
 */

public class myFirebaseInstantIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        HandyObject.putPrams(getApplicationContext(), AppConstants.DEVICE_TOKEN, refreshedToken);
        Log.e("tkn", "Refreshed token: " + refreshedToken);
    }
}
