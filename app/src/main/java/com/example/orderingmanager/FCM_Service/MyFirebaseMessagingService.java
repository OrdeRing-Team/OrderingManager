package com.example.orderingmanager.FCM_Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.orderingmanager.ENUM_CLASS.OrderType;
import com.example.orderingmanager.R;
import com.example.orderingmanager.Splash.SplashActivity;
import com.example.orderingmanager.view.MainActivity;
import com.example.orderingmanager.view.OrderFragment.OrderListFragment;
import com.example.orderingmanager.view.login_register.AuthActivity;
import com.example.orderingmanager.view.login_register.LoginActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    final String PACKING_CHANNEL = OrderType.PACKING.toString();
    final String TABLE_CHANNEL = OrderType.TABLE.toString();
    final String WAITING_CHANNEL = OrderType.WAITING.toString();
    final String CANCEL_CHANNEL = OrderType.CANCEL.toString();

    private LocalBroadcastManager broadcastManager;

    @Override
    public void onCreate() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onNewToken(String p0) {
        super.onNewToken(p0);
    }

    public void showDataMessage(String msgTitle, String msgContent) {
        Log.i("### data msgTitle : ", msgTitle);
        Log.i("### data msgContent : ", msgContent);
        String toastText = String.format("[Data ?????????] title: %s => content: %s", msgTitle, msgContent);
        Looper.prepare();
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    /**
     * ???????????? ???????????? Toast??? ?????????
     * @param msgTitle
     * @param msgContent
     */
    public void showNotificationMessage(String msgTitle, String msgContent) {
        Log.i("### noti msgTitle : ", msgTitle);
        Log.i("### noti msgContent : ", msgContent);
        String toastText = String.format("[Notification ?????????] title: %s => content: %s", msgTitle, msgContent);
        Looper.prepare();
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    /**
     * ????????? ???????????? ?????????
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent intent2 = new Intent("testData");
        intent2.putExtra("data", remoteMessage.getData().get("channel_id"));
        broadcastManager.sendBroadcast(intent2);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("fromFCM_Channel",remoteMessage.getData().get("channel_id"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Uri sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_takeout");
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("body");
        String channel = remoteMessage.getData().get("channel_id");


        Log.e("//=======//","===================================//");
        Log.e("   Title  :" , title);
        Log.e("//=======//","===================================//");

        Log.e("//=======//","===================================//");
        Log.e("   body   :" , message);
        Log.e("//=======//","===================================//");

        Log.e("//=======//","===================================//");
        Log.e("   channel   :" , channel);
        Log.e("//=======//","===================================//");

//        switch (channel){
//            case "ORDER":
//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_order");
//                break;
//            case "TAKEOUT":
//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_takeout");
//                break;
//            case "WAITING":
//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_waiting");
//                break;
//        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_DESCRIPTION = "ChannerDescription";
            final int importance = NotificationManager.IMPORTANCE_HIGH;

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
//            if(remoteMessage.getData().get("Channel_id").equals("Channel_Waiting")){
//                Channel = "Channel_Waiting";
//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_waiting");
//            }else{
//                Channel = "CHANNELTAKEOUT";
//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_takeout");
//            }
            Log.e("//=======//","===================================//");
            Log.e("   Sound  :", sound.toString());
            Log.e("//=======//","===================================//");

            // add in API level 26

            if(channel.equals(TABLE_CHANNEL)) {
                Log.e("//=======//","===================================//");
                Log.e("CHANNELT TYPE :" , TABLE_CHANNEL);
                Log.e("//=======//","===================================//");
                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_order");

                NotificationChannel mChannel = new NotificationChannel(TABLE_CHANNEL, "???????????? ??????", importance);
                mChannel.setDescription(CHANNEL_DESCRIPTION);
                mChannel.enableLights(true);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                mChannel.setSound(sound, attributes);
                notificationManager.createNotificationChannel(mChannel);
            }else if(channel.equals(PACKING_CHANNEL)){
                Log.e("//=======//","===================================//");
                Log.e("CHANNELT TYPE :" , PACKING_CHANNEL);
                Log.e("//=======//","===================================//");

                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_takeout");

                NotificationChannel mChannel = new NotificationChannel(PACKING_CHANNEL, "???????????? ??????", importance);
                mChannel.setDescription(CHANNEL_DESCRIPTION);
                mChannel.enableLights(true);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                mChannel.setSound(sound, attributes);
                notificationManager.createNotificationChannel(mChannel);
            }
            else if(channel.equals(WAITING_CHANNEL)) {
                Log.e("//=======//","===================================//");
                Log.e("CHANNELT TYPE :" , WAITING_CHANNEL);
                Log.e("//=======//","===================================//");

                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_waiting");

                NotificationChannel mChannel = new NotificationChannel(WAITING_CHANNEL, "????????? ??????", importance);
                mChannel.setDescription(CHANNEL_DESCRIPTION);
                mChannel.enableLights(true);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                mChannel.setSound(sound, attributes);
                notificationManager.createNotificationChannel(mChannel);
            }
            else if(channel.equals(CANCEL_CHANNEL)) {
                Log.e("//=======//","===================================//");
                Log.e("CHANNELT TYPE :" , CANCEL_CHANNEL);
                Log.e("//=======//","===================================//");

                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_cancel");

                NotificationChannel mChannel = new NotificationChannel(CANCEL_CHANNEL, "???????????? ??????", importance);
                mChannel.setDescription(CHANNEL_DESCRIPTION);
                mChannel.enableLights(true);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                mChannel.setSound(sound, attributes);
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notify_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setSound(sound);


        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setContentTitle(title);
            builder.setVibrate(new long[]{500, 500});
        }
//        notificationManager.notify(Integer.parseInt(remoteMessage.getData().get("Type")), builder.build());
        notificationManager.notify((int)System.currentTimeMillis()/1000, builder.build());

    }

    private RemoteViews getCustomDesign(String title, String message) {
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.noti_title, title);
        remoteViews.setTextViewText(R.id.noti_message, message);
        remoteViews.setImageViewResource(R.id.logo, R.drawable.icon);
        return remoteViews;
    }

    public void showNotification(String title, String message) {
        //?????? ????????? ????????? ??????????????? ???????????????.
        Intent intent = new Intent(this, MainActivity.class);
        //?????? ?????? ????????? : ?????? ?????????????????????...
        String channel_id = "CHN_ID";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //?????? ???????????? ????????? ??????. ?????????????????? ?????? ????????? uri ??????
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notify_order);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.icon)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000}) //????????? ?????? ?????? : 1??? ??????, 1??? ??????, 1??? ??????
                .setOnlyAlertOnce(true) //????????? ????????? ?????????.. : ?????? ?????? ?????? ??????
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) { //??????????????? ????????? ????????? ????????? ????????? ??? ?????? ????????????
            //????????? ???????????? ??????
            builder = builder.setContent(getCustomDesign(title, message));
        } else { //????????? ?????? ???????????? ??????
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.icon); //????????? ??????????????? ????????? ?????? ????????? ????????????..
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //?????? ????????? ????????? ??????????????? ????????? ?????? ??????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "CHN_NAME", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        //?????? ?????? !
        notificationManager.notify(0, builder.build());
    }
}
