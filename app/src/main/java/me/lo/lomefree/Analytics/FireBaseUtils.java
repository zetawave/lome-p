package me.lo.lomefree.Analytics;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FireBaseUtils
{

    public static void logFireBaseEvent(String name, Bundle params, FirebaseAnalytics fbanalytics)
    {
        fbanalytics.logEvent(name, params);
    }
}
