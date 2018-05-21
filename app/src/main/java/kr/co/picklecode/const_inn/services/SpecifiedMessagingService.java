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
import android.view.View;
import android.widget.RemoteViews;

import java.util.Map;

import bases.Configs;
import bases.Constants;
import bases.services.MyFirebaseMessagingService;
import comm.model.UserModel;
import kr.co.picklecode.const_inn.IntroActivity;
import kr.co.picklecode.const_inn.MainActivity;
import kr.co.picklecode.const_inn.R;
import kr.co.picklecode.const_inn.receivers.GlobalReceiver;

/**
 * Created by HP on 2018-04-18.
 */

public class SpecifiedMessagingService extends MyFirebaseMessagingService {

    @Override
    protected void sendNotification(String title, String body, Map<String, String> extra) {
        Intent intent = new Intent(this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.icon_const_fav)
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

        RemoteViews contentViews = new RemoteViews(getPackageName(), R.layout.notification_layout_empr);

        contentViews.setTextViewText(R.id.noti_guide, extra.get("notiGuide"));
        contentViews.setTextViewText(R.id.noti_class, extra.get("notiClass"));
        contentViews.setTextViewText(R.id.noti_msg, extra.get("notiBox"));

        boolean isRedirect = extra.get("isRedirect") != null && extra.get("isRedirect").equals("true");

        if(isRedirect){
            contentViews.setViewVisibility(R.id.noti_class, View.GONE);
            contentViews.setViewVisibility(R.id.noti_msg, View.GONE);
            contentViews.setViewVisibility(R.id.brace1, View.GONE);
            contentViews.setViewVisibility(R.id.brace2, View.GONE);
        }

        int yesCode = isRedirect ? Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_REDIRECT : Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_YES;

        Intent noti_yes_intent = new Intent(this, GlobalReceiver.class);
        noti_yes_intent.putExtra("articleNumber", extra.get("articleNumber"));
        noti_yes_intent.putExtra("isYes", true);
        noti_yes_intent.putExtra("isRedirect", isRedirect);
        PendingIntent noti_yes_pIntent = PendingIntent.getBroadcast(this, yesCode, noti_yes_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent noti_no_intent = new Intent(this, GlobalReceiver.class);
        noti_no_intent.putExtra("articleNumber", extra.get("articleNumber"));
        PendingIntent noti_no_pIntent = PendingIntent.getBroadcast(this, Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_NO, noti_no_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent noti_close_intent = new Intent(this, GlobalReceiver.class);
        noti_close_intent.putExtra("articleNumber", extra.get("articleNumber"));
        PendingIntent noti_close_pIntent = PendingIntent.getBroadcast(this, Constants.INTENT_NOTIFICATION.REQ_CODE_ACTION_CLOSE, noti_close_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        contentViews.setOnClickPendingIntent(R.id.noti_yes, noti_yes_pIntent);
        contentViews.setOnClickPendingIntent(R.id.noti_no, noti_no_pIntent);
        contentViews.setOnClickPendingIntent(R.id.noti_close, noti_close_pIntent);

        notificationBuilder.setCustomBigContentView(contentViews);
        Notification notification = notificationBuilder.build();

        if(UserModel.isSatisfied()) notificationManager.notify(Configs.NOTIFICATION_ID /* ID of notification */, notification);
    }

}
