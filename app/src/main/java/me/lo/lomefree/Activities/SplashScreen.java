package me.lo.lomefree.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import me.lo.lomefree.R;

import static me.lo.lomefree.Globals.GlobalValues.SPLASH_SLEEP_TIME;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        runSplashThread();

    }

    private void runSplashThread()
    {
        Thread splash = new Thread(){

            @Override
            public void run()
            {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            YoYo.with(Techniques.Tada)
                                    .duration(1000)
                                    .repeat(0)
                                    .playOn(findViewById(R.id.splashLogo));
                        }
                    });

                    sleep(SPLASH_SLEEP_TIME);
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        splash.start();
    }
}
