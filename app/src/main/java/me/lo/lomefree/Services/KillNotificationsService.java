package me.lo.lomefree.Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import me.lo.lomefree.Interfaces.NotificationId;

public class KillNotificationsService extends Service implements NotificationId
{

    public class KillBinder extends Binder {
        public final Service service;

        KillBinder(Service service) {
            this.service = service;
        }

    }

    private final IBinder mBinder = new KillBinder(this);

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }
    @Override
    public void onCreate() {
        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert mNM != null;
        mNM.cancel(NOTIFY_PROGRESS_UNDETERMINATE);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(this.getClass().getName(), "UNBIND");
        return true; }
}

