package me.lo.lomefree.Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import me.lo.lomefree.Activities.ProgressActivity;

public class KillAppService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* Kill all tasks*/
    @Override
    public void onTaskRemoved(Intent rootIntent) {

        ProgressActivity.cancelTask=true;
        Log.d("DESTROYING", "DESTROYING LOME");
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.cancelAll();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }
}
