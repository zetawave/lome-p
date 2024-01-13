package me.lo.lomefree.Services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;


public class ServiceBinder
{
    private static boolean isBound = false;
    private static ServiceConnection mConnection;

    public static void createKillNotificationService(final Context context)
    {
        isBound = false;
        mConnection = new ServiceConnection()
        {
            public void onServiceConnected(ComponentName className,
                                           IBinder binder) {
                ((KillNotificationsService.KillBinder) binder).service.startService(new Intent(
                        context, KillNotificationsService.class));
                isBound = true;
            }

            public void onServiceDisconnected(ComponentName className) {
            }

        };
        if(!isBound)
            context.bindService(new Intent(context,
                        KillNotificationsService.class), mConnection,
                Context.BIND_AUTO_CREATE);

    }

    public static void unbindservice(final Context context)
    {
        if(isBound)
            context.unbindService(mConnection);
    }
}
