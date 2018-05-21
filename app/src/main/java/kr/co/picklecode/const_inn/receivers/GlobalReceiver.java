package kr.co.picklecode.const_inn.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bases.Configs;
import bases.Constants;
import bases.callbacks.SimpleCallback;
import comm.SimpleCall;
import comm.model.UserModel;
import kr.co.picklecode.const_inn.MainActivity;

/**
 * Created by HP on 2018-05-10.
 */

public class GlobalReceiver extends BroadcastReceiver {

    public static final String TAG = "GlobalReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive [" + intent.getExtras().toString() + "]");

        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(intent.getExtras().getBoolean("isYes") && UserModel.isSatisfied()){
            if(intent.getExtras().getBoolean("isRedirect")){
                final Intent broadCast = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
                broadCast.putExtra("action", "gotoRecruit");
                context.sendBroadcast(broadCast);
                Log.e(TAG, "broadcast sent");
                final Intent intent1 = new Intent(context, MainActivity.class);
                intent1.putExtra("isRedirect", true);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                mNotificationManager.cancel(Configs.NOTIFICATION_ID);
            }else{
                Map<String, Object> params = new HashMap<>();
                params.put("searchId", intent.getExtras().getString("articleNumber"));
                SimpleCall.getHttpJson(Configs.BASE_URL + "/web/user/apply/" + UserModel.getFromPreference().getUserNo(), params, new SimpleCall.CallBack() {
                    @Override
                    public void handle(JSONObject jsonObject) {
                        mNotificationManager.cancel(Configs.NOTIFICATION_ID);
                    }
                }, new SimpleCallback() {
                    @Override
                    public void callback() {
                        mNotificationManager.cancel(Configs.NOTIFICATION_ID);
                    }
                });
            }
        }else{
            mNotificationManager.cancel(Configs.NOTIFICATION_ID);
        }
    }

}
