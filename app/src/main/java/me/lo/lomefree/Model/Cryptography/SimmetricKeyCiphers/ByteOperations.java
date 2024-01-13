package me.lo.lomefree.Model.Cryptography.SimmetricKeyCiphers;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import me.lo.lomefree.Activities.ProgressActivity;
import me.lo.lomefree.R;
import me.lo.lomefree.Utils.Misc.NotificationSender;

import static me.lo.lomefree.Utils.Misc.NotificationSender.notifyFileElaborationProgress;

class ByteOperations
{

    public static void StreamOperation(InputStream is, OutputStream os, Context context, long length) throws IOException {
        double progress = 0;
        double count = 0;
        double newLen = (double) length;

        byte [] buffer = new byte[4096];
        int read;

        ArrayList components = NotificationSender.
                setNotificationProgressWithPendingIntentCancel(context,
                        String.valueOf(SimmetricCipherOperation.fileNumber)
                                .concat(" ")
                                .concat(context.getString(R.string.remains)),
                        context.getString(R.string.operationWorker));

        try
        {
            ((NotificationCompat.Builder)components.get(1))
                    .setProgress(100, 0, false);

            ((NotificationCompat.Builder) components.get(1)).setOngoing(true);

            while (!ProgressActivity.cancelTask && ((read = is.read(buffer)) != -1)) {
                count = count + read;
                progress = ((count/newLen) * (double)100);
                notifyFileElaborationProgress(components, context, progress);
                os.write(buffer, 0, read);
            }
            os.flush();
        }finally
        {
            if(is != null)
                is.close();
            if(os != null)
                os.close();
            SimmetricCipherOperation.fileNumber-=1;
            try {
                ((NotificationManager) components.get(0)).cancelAll();
            }catch (NullPointerException ex)
            {
                Log.d("CANCEL NOTIFICATIONS", ex.getLocalizedMessage());
            }
        }
    }
}
