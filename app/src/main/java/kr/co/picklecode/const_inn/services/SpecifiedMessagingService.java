package kr.co.picklecode.const_inn.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import bases.Constants;
import bases.services.MyFirebaseMessagingService;
import kr.co.picklecode.const_inn.IntroActivity;
import kr.co.picklecode.const_inn.MainActivity;
import kr.co.picklecode.const_inn.R;

/**
 * Created by HP on 2018-04-18.
 */

public class SpecifiedMessagingService extends MyFirebaseMessagingService {

    @Override
    protected void sendNotification(String title, String body) {
        Intent intent = new Intent(this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        RemoteViews contentiew = new RemoteViews(getPackageName(), R.layout.notification_layout_empr);
//        Intent noti_yes_intent = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
//        PendingIntent noti_yes_pIntent = PendingIntent.getActivity(this, Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_YES, noti_yes_intent, PendingIntent.FLAG_ONE_SHOT);
//        Intent noti_no_intent = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
//        PendingIntent noti_no_pIntent = PendingIntent.getActivity(this, Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_NO, noti_no_intent, PendingIntent.FLAG_ONE_SHOT);
//        contentiew.setOnClickPendingIntent(R.id.noti_yes, noti_yes_pIntent);
//        contentiew.setOnClickPendingIntent(R.id.noti_no, noti_no_pIntent);
        notificationBuilder.setCustomBigContentView(contentiew);
        Notification notification = notificationBuilder.build();

        notificationManager.notify(0 /* ID of notification */, notification);
    }

}
