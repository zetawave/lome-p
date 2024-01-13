package me.lo.lomefree.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

import me.lo.lomefree.R;


/**
 * Created by mwss on 28/03/18.
 */

public class IntroActivity extends AppIntro2
{
    private boolean notfirst = false;
    private int slidesColor;
    private String [] TITLES;
    private String [] DESCRIPTIONS;
    private int [] IMAGES;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initalizeStrings();
        addSlides();
        setBarColor(Color.parseColor("#1976D2"));
        showSkipButton(false);
        setProgressButtonEnabled(true);
        setVibrate(true);
        setVibrateIntensity(25);
        getIntentExtras();
    }

    private void initalizeStrings()
    {
        slidesColor = Color.parseColor("#1976D2");
        TITLES = new String[]{getString(R.string.Slide1_title1), getString(R.string.Slide2_title2), getString(R.string.Slide4_title4), getString(R.string.Slide5_title5)};
        DESCRIPTIONS = new String[]{getString(R.string.Slide1_description1), getString(R.string.Slide2_description2), getString(R.string.Slide4_description4), getString(R.string.Slide5_description5)};
        //TITLES = new String[]{getString(R.string.Slide1_title1), getString(R.string.Slide2_title2), getString(R.string.Slide3_title3), getString(R.string.Slide4_title4), getString(R.string.Slide5_title5)};
        //DESCRIPTIONS = new String[]{getString(R.string.Slide1_description1), getString(R.string.Slide2_description2), getString(R.string.Slide3_description3), getString(R.string.Slide4_description4), getString(R.string.Slide5_description5)};
        IMAGES = new int[]{R.drawable.currentlogo, R.drawable.slide, R.drawable.share, R.drawable.lockapp};
    }

    private void getIntentExtras()
    {
        this.notfirst = getIntent().getBooleanExtra("notfirst", false);
    }

    private void addSlides()
    {
        for(int i = 0; i< IMAGES.length; i++)
            addSlide(AppIntro2Fragment.newInstance(TITLES[i], DESCRIPTIONS[i], IMAGES[i], slidesColor));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        if(!this.notfirst)
        {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}

