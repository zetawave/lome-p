package me.lo.lomefree.Utils.Misc;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

import me.lo.lomefree.Activities.MainActivity;
import me.lo.lomefree.Activities.ProgressActivity;
import me.lo.lomefree.Globals.GlobalValues;
import me.lo.lomefree.R;
import me.lo.lomefree.UIUtils.Graphics.Alerts;

public class NotificationActionReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context, Intent intent) {
        if(Objects.requireNonNull(intent.getAction()).equalsIgnoreCase("cancel"))
        {
                    Alerts.makeToast(context, context.getString(R.string.operation_canceled), GlobalValues.WARNING, Toast.LENGTH_LONG);
                    ProgressActivity.cancelTask = true;
        }
    }
}
