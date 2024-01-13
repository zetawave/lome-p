package me.lo.lomefree.Utils.Misc;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

import me.lo.lomefree.Activities.MainActivity;
import me.lo.lomefree.Activities.ProgressActivity;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Model.Cryptography.SimmetricKeyCiphers.SimmetricCipherOperation;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;

@TargetApi(Build.VERSION_CODES.N)
public class NotificationSender implements RRCodes
{
    private static final String channelId = "channel_01";
    private static final String channelName = "Default Channel";
    private static final int importance = NotificationManager.IMPORTANCE_HIGH;


    public static void sendNotificationProgressWithoutPendingIntent(Context context, String title, String message)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.notifyicon)
                        .setContentTitle(title)
                        .setContentText(message)
                .setProgress(1, 1, true);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.notify(1, mBuilder.build());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

    }

    public static ArrayList setNotificationProgressWithPendingIntentCancel(Context context, String title, String message)
    {
        Intent intentCancel = new Intent(context, NotificationActionReceiver.class);
        intentCancel.setAction("cancel");
        intentCancel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent progressIntent = new Intent(context, ProgressActivity.class);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, progressIntent, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, DESTROY_TASK_REQUEST_CODE, intentCancel, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        ArrayList components = new ArrayList();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setAutoCancel(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSmallIcon(R.drawable.notifyicon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setContentIntent(pIntent)
                        .addAction(R.drawable.ic_delete_black_24dp, context.getString(R.string.cancel), pendingIntentCancel)
                        .setProgress(100, 0, false);

        mBuilder.setPriority(NotificationCompat.PRIORITY_LOW);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.notify(1, mBuilder.build());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mChannel.setImportance(NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        components.add(mNotificationManager);
        components.add(mBuilder);

        return components;

    }

    public static void sendNotificationProgressWithPendingIntent(Context context, String title, String message, Intent intent, int id)
    {
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context,channelId)
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.notifyicon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setProgress(1, 1, true)
                .setContentIntent(pIntent);

        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.notify(id, mBuilder.build());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    public static void sendNotification(Context context, String title, String message, boolean autocancel, CustomSettings customSettings)
    {
        //PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        if(customSettings.isNotifications()) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, channelId)
                            .setAutoCancel(autocancel)
                            .setSmallIcon(R.drawable.notifyicon)
                            .setContentTitle(title)
                            .setContentText(message);
            mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            mNotificationManager.notify(1, mBuilder.build());

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                mNotificationManager.createNotificationChannel(mChannel);
            }

        }
    }

    public static void sendNotification(Context context, String title, String message, boolean autocancel, Intent intent, CustomSettings customSettings)
    {

        if(customSettings.isNotifications()) {
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, channelId)
                            .setAutoCancel(autocancel)
                            .setSmallIcon(R.drawable.notifyicon)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setContentIntent(pIntent);
            mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            mNotificationManager.notify(1, mBuilder.build());

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }
    }

    public static void sendBigTextNotification(Context context, String title, String message, boolean autocancel, CustomSettings customSettings)
    {
        if(customSettings.isNotifications()) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, channelId)
                            .setAutoCancel(autocancel)
                            .setSmallIcon(R.drawable.notifyicon)
                            .setContentTitle(title)
                            .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            mNotificationManager.notify(1, mBuilder.build());

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }
    }

    public static void notifyFileElaborationProgress(ArrayList components, Context context, double progress)
    {
        ((NotificationCompat.Builder)components.get(1)).setProgress(100, (int)progress, false);
        ((NotificationCompat.Builder)components.get(1))
                .setContentText(context.getString(R.string.operationWorker)
                        .concat(" ")
                        .concat(String.valueOf((int)progress))
                        .concat("%"))
                .setContentTitle(String.valueOf(SimmetricCipherOperation.fileNumber)
                        .concat(" ")
                        .concat(context.getString(R.string.remains)));

        ((NotificationManager)components.get(0)).notify(1,((NotificationCompat.Builder)components.get(1)).build());
    }

    public static void notifyFileDeleteFileProgress(ArrayList components, Context context, double progress)
    {
        ((NotificationCompat.Builder)components.get(1)).setProgress(100, (int)progress, false);
        ((NotificationCompat.Builder)components.get(1))
                .setContentText(context.getString(R.string.deletingFiles))
                .setContentTitle(String.valueOf(SimmetricCipherOperation.fileNumber)
                        .concat(" ")
                        .concat(context.getString(R.string.remains)));

        ((NotificationManager)components.get(0)).notify(1,((NotificationCompat.Builder)components.get(1)).build());
    }
}
