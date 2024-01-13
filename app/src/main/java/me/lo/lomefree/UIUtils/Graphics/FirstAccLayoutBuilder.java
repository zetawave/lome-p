package me.lo.lomefree.UIUtils.Graphics;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Objects;

import me.lo.lomefree.Interfaces.FirstAccessViews;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.SettingsUtils.ParticularSetting;

public class FirstAccLayoutBuilder implements FirstAccessViews
{

    public static void verifyFirstAccess(final CustomSettings customSettings, final String PREFERENCE_KEY, int DIALOG_ID, final Context thisContext, AppCompatActivity thisActivity)
    {
        Log.d("FIRST ACCES", "ENTERED");
        ParticularSetting first_access_ps = customSettings.getOtherPreference(PREFERENCE_KEY);
        Boolean first_access;
        if(first_access_ps != null)
            first_access = (Boolean) first_access_ps.getValue();
        else
        {
            first_access_ps = new ParticularSetting();
            first_access_ps.setPreference(PREFERENCE_KEY);
            first_access_ps.setValue(true);
            first_access = true;
        }
        Log.d("FIRST ACCESS ", String.valueOf(first_access));
        if(first_access)
        {
            final ParticularSetting finalFirst_access_ps = first_access_ps;
            Alerts.makeFirstOptionAccessDialog(DIALOG_ID, thisContext, thisActivity)
                    .setPositiveButton(thisContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finalFirst_access_ps.setValue(false);
                            customSettings.replaceOtherPreference(PREFERENCE_KEY, finalFirst_access_ps);
                            customSettings.saveOtherPreferences(thisContext);
                        }
                    }).create().show();
        }
    }

    public static View buildLayout(AppCompatActivity activity, int VIEW)
    {
        @SuppressLint("InflateParams") View viewInflate = activity.getLayoutInflater().inflate(R.layout.first_option_press_layout, null);
        ImageView first_image = viewInflate.findViewById(R.id.option_informations_image);
        TextView text = viewInflate.findViewById(R.id.option_informations);
        ScrollView scrollView = viewInflate.findViewById(R.id.information_alert_scroll);
        scrollView.setScrollbarFadingEnabled(false);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setScrollbarFadingEnabled(false);

        switch(VIEW)
        {
            case KEY_SEARCH:
                first_image.setBackgroundResource(FirstAccessInformations.IMAGES.get(KEY_SEARCH));
                text.setText(Objects.requireNonNull(FirstAccessInformations.TEXTS_AND_TITLES.get(KEY_SEARCH))[1]);
                break;
            case APP_VERSION:
                first_image.setBackgroundResource(FirstAccessInformations.IMAGES.get(APP_VERSION));
                text.setText(Objects.requireNonNull(FirstAccessInformations.TEXTS_AND_TITLES.get(APP_VERSION))[1]);
                break;
        }

        return viewInflate;
    }
}
