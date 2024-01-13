package me.lo.lomefree.UIUtils.Graphics;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.provider.DocumentsContract;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import me.lo.lomefree.Globals.GlobalValues;
import me.lo.lomefree.Interfaces.ChoiceDialogModes;
import me.lo.lomefree.Interfaces.FirstAccessViews;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;

import static me.lo.lomefree.Globals.GlobalValues.ERROR;
import static me.lo.lomefree.Globals.GlobalValues.INFO;
import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.Globals.GlobalValues.SUCCESS;
import static me.lo.lomefree.Globals.GlobalValues.WARNING;


/**
 * Created by Fabrizio Amico on 23/03/18.
 */

public class Alerts implements ChoiceDialogModes, RRCodes, FirstAccessViews, SettingsParameters
{

    private static int choiceKeyType = 1;

    public static void showInfoDialog(String title, String text, Context context, int icon)
    {
        new androidx.appcompat.app.AlertDialog.Builder(context).setTitle(title).setIcon(icon).setMessage(text).create().show();
    }

// --Commented out by Inspection START (18/07/18 19.09):
    public static AlertDialog.Builder makeFirstOptionAccessDialog(final int VIEW, final Context context, AppCompatActivity activity)
    {
        AlertDialog.Builder builder = Alerts.showConfirmDialog(context, "", "");

        switch(VIEW)
        {

            case KEY_SEARCH:
                builder.setView(FirstAccLayoutBuilder.buildLayout(activity, VIEW));
                builder.setTitle(FirstAccessInformations.TEXTS_AND_TITLES.get(KEY_SEARCH)[0]);
                break;
            case APP_VERSION:
                builder.setView(FirstAccLayoutBuilder.buildLayout(activity, VIEW));
                builder.setTitle(FirstAccessInformations.TEXTS_AND_TITLES.get(APP_VERSION)[0]);
                break;
        }

        return builder;
   }
// --Commented out by Inspection STOP (18/07/18 19.09)



    public static void showErrorDialog(String title, final String message, Context context, int icon)
    {
        new androidx.appcompat.app.AlertDialog.Builder(context).setTitle(title)
                .setCancelable(false)
                .setIcon(icon)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Errore ", "Errore "+message);
                    }
                })
                .show();
    }

    public static AlertDialog.Builder showConfirmDialog(Context context, String title, String message)
    {
        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message);
    }

    public static AlertDialog.Builder showConfirmDialog(Context context, String title, String message, int icon)
    {
        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon);
    }

    public static void makeToast(Context context, String text, int mode, int lenght)
    {
        switch(mode)
        {
            case SUCCESS:
                Toasty.success(context, text, lenght).show();
                break;
            case INFO:
                Toasty.info(context, text, lenght).show();
                break;
            case WARNING:
                Toasty.warning(context, text, lenght).show();
                break;
            case ERROR:
                Toasty.error(context, text, lenght).show();
                break;
        }
    }

    public static void showMultiChoiceDialog(final AppCompatActivity activity, final CustomSettings customSettings, int mode)
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        choiceKeyType = 1;

        switch(mode)
        {
            case SAVE_DIR_CHOICE:
                final CharSequence [] SAVE_DIR_ITEMS = {activity.getString(R.string.visualize_and_manage), activity.getString(R.string.acquire_files)};
                alert.setTitle(R.string.save_dir_chooser_mode_title);
                //alert.setMessage(R.string.save_dir_choice_message);
                alert.setSingleChoiceItems(SAVE_DIR_ITEMS, 0, (dialog, which) -> {
                    if(SAVE_DIR_ITEMS[which] == activity.getString(R.string.visualize_and_manage))
                    {
                        choiceKeyType = 1;
                    }else
                        choiceKeyType = 2;
                });
                alert.setPositiveButton(R.string.open, (dialog, which) -> {
                    if(choiceKeyType == 1)
                    {
                        try {
                            String path = customSettings.getCustom_save_path()+ File.separator;
                            File file = new File(path);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(file), "resource/folder");
                            try {
                                activity.startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                makeToast(activity.getApplicationContext(), activity.getString(R.string.no_file_explorer_found),ERROR, LONG);
                            }
                        }catch(Exception ex){
                            Log.d("Intent error", ex.getMessage());
                        }
                    }else if(choiceKeyType == 2)
                    {
                        String initialPath = customSettings.getCustom_save_path();
                        DataManager.getFileAndDirsChooser(activity.getApplicationContext(), activity, initialPath);
                    }
                });
                choiceKeyType = 1;
                break;

        }

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("LOG: ", "Task Canceled");
            }
        });
        alert.show();
    }

    public static AlertDialog.Builder makeDoubleEditTextDialogWithNameFiltered(Context context, String title, String message, String hint1, String hint2, EditText [] et, int TYPE_CLASS)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);

        LinearLayout paramLayout = new LinearLayout(context);

        paramLayout.setOrientation(LinearLayout.VERTICAL);
        InputFilter[] filterArray = new InputFilter[2];
        filterArray[0] = new InputFilter.LengthFilter(35);
        filterArray[1] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        for(int i=0; i<et.length; i++)
        {
            et[i] = new EditText(context);
            et[i].setInputType(TYPE_CLASS);
            switch(i)
            {
                case 0:
                    et[i].setFilters(filterArray);
                    et[i].setHint(hint1);
                    break;
                case 1:
                    et[i].setHint(hint2);
                    break;
            }
            paramLayout.addView(et[i]);
        }

        alert.setView(paramLayout);

        return alert;
    }


    public static AlertDialog.Builder makeSingleEditTextDialog(Context context, String title, String message, String hint, EditText et, int TYPE_CLASS)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);

        LinearLayout paramLayout = new LinearLayout(context);

        paramLayout.setOrientation(LinearLayout.VERTICAL);

        //et = new EditText(context);
        et.setInputType(TYPE_CLASS);
        et.setHint(hint);
        paramLayout.addView(et);
        alert.setView(paramLayout);
        return alert;
    }

    public static void showOpenSourceInformationsDialog(AppCompatActivity activity)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.open_source_license));
        ScrollView scrollView = new ScrollView(activity);
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView []textViews  = new TextView[2];
        for(String [] informations : GlobalValues.LICENCES)
        {
            String name = informations[0];
            String licence = informations[1];

            textViews[0] = new TextView(activity);
            textViews[1] = new TextView(activity);

            textViews[0].setTextSize(20);
            textViews[0].setTextColor(Color.parseColor("#2196F3"));
            textViews[0].setText(name);

            textViews[1].setTextSize(15);
            textViews[1].setBackgroundColor(Color.parseColor("#eaeaea"));
            textViews[1].setText(licence);

            linearLayout.addView(textViews[0]);
            linearLayout.addView(textViews[1]);
        }
        scrollView.addView(linearLayout);
        builder.setView(scrollView);
        builder.setPositiveButton(activity.getString(R.string.ok), null);
        builder.create().show();
    }

    public static AlertDialog.Builder buildGenericAlertDialog(Context context, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        return builder;

    }

}
