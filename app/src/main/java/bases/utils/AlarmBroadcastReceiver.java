package bases.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import bases.Constants;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static boolean isLaunched = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        isLaunched = true;

        final Intent activityIntent1 = new Intent(Constants.ACTIVITY_INTENT_FILTER);
        activityIntent1.putExtra("action", "refresh");
        context.sendBroadcast(activityIntent1);

        PreferenceUtil.setBoolean(Constants.PREFERENCE.IS_ALARM_SET, false);
        Log.e("alarmCall", "Stopping media");
    }
}