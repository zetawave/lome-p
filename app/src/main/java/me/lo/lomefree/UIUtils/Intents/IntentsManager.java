package me.lo.lomefree.UIUtils.Intents;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import me.lo.lomefree.Activities.ElaborationActivity;
import me.lo.lomefree.Activities.KeyFileManager;
import me.lo.lomefree.R;
import me.lo.lomefree.UIUtils.Graphics.Alerts;
import me.lo.lomefree.Utils.Files.Entities.FileBox;

public class IntentsManager
{
    private static final int SIMM_KEY = 1;

    public static void createNewUriKeyIntent(boolean viewMode, String uri, Context context)
    {
        Intent newKeyIntent = new Intent(context, KeyFileManager.class);
        newKeyIntent.putExtra("NEW_KEY", uri);
        newKeyIntent.putExtra("VIEW_MODE", viewMode);
        context.startActivity(newKeyIntent);
    }
/*
    public static void createNewUriKeyIntent(boolean viewMode, String uri, Context context, AppCompatActivity activity, int REQUEST_CODE, int TYPE)
    {
        Intent newKeyIntent = new Intent(context, (TYPE == SIMM_KEY ? KeyFileManager.class : RSAKeyManager.class));
        newKeyIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        newKeyIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        newKeyIntent.putExtra("NEW_KEY", uri);
        newKeyIntent.putExtra("VIEW_MODE", viewMode);
        activity.startActivityForResult(newKeyIntent, REQUEST_CODE);
    }

*/

    public static void createFileElaborationIntent(FileBox box, Context context)
    {
        try {
            Intent elaboration = new Intent(context, ElaborationActivity.class);
            elaboration.putExtra("FileBox", box);
            context.startActivity(elaboration);
        }catch(Exception ex)
        {
            Alerts.showErrorDialog(context.getString(R.string.unattended_error), context.getString(R.string.undefined_error1)+ex.getMessage(), context, R.drawable.ic_error_black_24dp);
        }
    }

    public static void createFileElaborationIntent(FileBox box, Context context, AppCompatActivity activity, int REQUEST_CODE)
    {
        try {
            Intent elaboration = new Intent(context, ElaborationActivity.class);
            elaboration.putExtra("FileBox", box);
            activity.startActivityForResult(elaboration, REQUEST_CODE);
        }catch(Exception ex)
        {
            Alerts.showErrorDialog(context.getString(R.string.unattended_error), context.getString(R.string.undefined_error1)+ex.getMessage(), context, R.drawable.ic_error_black_24dp);
        }
    }
}
