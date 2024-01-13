package me.lo.lomefree.Activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;

import me.lo.lomefree.R;
import me.lo.lomefree.UIUtils.Graphics.UIComponentBuilder;

import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;


public class AboutActivity extends MaterialAboutActivity
{

    public static String [] TITLES;
    public static int LOGOS[];
    public static String  [][] TEXTS;
    public static String [][] SUBTEXTS;
    public static int [][] SUBICON;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }


    @Override
    protected boolean shouldAnimate() {
        return true;
    }


    @NonNull
    @Override
    protected MaterialAboutList getList() {
        return getMaterialAboutList(this);
    }


    @NonNull
    protected MaterialAboutList getMaterialAboutList(@NonNull Context context)
    {
        try {
            return UIComponentBuilder.getMaterialAboutList(TITLES, LOGOS, TEXTS, SUBTEXTS, SUBICON, this);
        }catch(Exception ex)
        {
            postToastMessage( getString(R.string.about_activity_error_unattended)+" "+ex.getMessage());
        }
        return null;
    }

    private void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                makeToast(getApplicationContext(),message, me.lo.lomefree.Globals.GlobalValues.ERROR, me.lo.lomefree.Globals.GlobalValues.LONG);
            }
        });
    }

}
