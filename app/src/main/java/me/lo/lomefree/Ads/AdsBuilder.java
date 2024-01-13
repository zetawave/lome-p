package me.lo.lomefree.Ads;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class AdsBuilder
{
    public static void loadAd(final AdView adView, AppCompatActivity activity, final String className)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded()
                    {
                        Log.d("ADS", "Il banner "+className+" è stato creato");

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d("ADS", "Il banner "+className+" non è stato creato: "+loadAdError.toString());
                    }

                    @Override
                    public void onAdClosed() {
                        Log.d("ADS", "Il banner "+className+" è stato chiuso");
                    }
                });
            }
        });
    }


}
