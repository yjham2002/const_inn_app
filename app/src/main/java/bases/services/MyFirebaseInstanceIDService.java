package bases.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bases.Configs;
import bases.Constants;
import comm.SimpleCall;
import comm.model.UserModel;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        UserModel userModel = UserModel.getFromPreference();
        if(userModel == null){
            userModel = new UserModel();
        }

        userModel.setUserNo(1);
        userModel.setMessageToken(refreshedToken);
        userModel.saveAsPreference();

        sendRegistrationToServer(userModel, refreshedToken);
    }

    private void sendRegistrationToServer(UserModel userModel, String token) {
        Map<String, Object> params = new HashMap<>();
        params.put("pushKey", token);
        SimpleCall.getHttpJson(Configs.BASE_URL + "/web/user/update/pushKey/" + userModel.getUserNo(), params, new SimpleCall.CallBack() {
            @Override
            public void handle(JSONObject jsonObject) {
                Log.e("userInfo", jsonObject.toString());
            }
        }, null);
    }
}
