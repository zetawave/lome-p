package me.lo.lomefree.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.kevalpatel.passcodeview.PinView;

import com.kevalpatel.passcodeview.authenticator.PasscodeViewPinAuthenticator;
import com.kevalpatel.passcodeview.indicators.CircleIndicator;
import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;
import com.kevalpatel.passcodeview.keys.KeyNamesBuilder;
import com.kevalpatel.passcodeview.keys.RoundKey;

import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.R;
import me.lo.lomefree.UIUtils.Graphics.Alerts;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;

import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setBoolPref;
import static me.lo.lomefree.Utils.Files.FDManager.DataManager.setStringPref;
import static me.lo.lomefree.Utils.Misc.MyEncoder.decodeB64;
import static me.lo.lomefree.Utils.Misc.MyEncoder.encodeB64;

public class LockActivity extends Activity implements SettingsParameters, RRCodes
{
    private SharedPreferences prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        initializeCustomSettings();
        setPinView();
    }


    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finishAndRemoveTask();

    }

    private void initializeCustomSettings()
    {
        prefs = DataManager.getSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefsEditor = prefs.edit();

        if(!prefs.contains(FIRST_ACCESS_PREF))
            setBoolPref(FIRST_ACCESS_PREF, true, prefsEditor);
        if(!prefs.contains(PROFILE_IMAGE_PREF))
            setStringPref(PROFILE_IMAGE_PREF, "null", prefsEditor);
    }


    private int [] getCorrectPin()
    {
        String encoded_cpin = encodeB64(CORRECT_PIN);
        String encoded_dvalue = encodeB64("none");
        String temp_pin = prefs.getString(encoded_cpin, encoded_dvalue);
        String decoded_pin = decodeB64(temp_pin);
        Log.d("PIN ", decoded_pin);
        if(decoded_pin.equals("none"))
        {
            Alerts.showErrorDialog(getString(R.string.critic_error_tile), getString(R.string.pin_critic_error_message), this, R.drawable.ic_error_black_24dp);
            finishAndRemoveTask();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        else
        {
            //temp_pin = temp_pin;
            char [] pin = decoded_pin.toCharArray();
            int [] correctPin = new int[decoded_pin.length()];

            for(int i=0; i<correctPin.length; i++)
                correctPin[i] = Integer.parseInt(String.valueOf(pin[i]));
            return correctPin;
        }
        return null;
    }


    private void setPinView()
    {
        PinView pinView;
        pinView = findViewById(R.id.mpin_view);

        int [] correctPin = getCorrectPin();

        assert correctPin != null;

        pinView.setPinAuthenticator(new PasscodeViewPinAuthenticator(correctPin));

        pinView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        pinView.setKey(new RoundKey.Builder(pinView)
                .setKeyPadding(R.dimen.key_padding)
                .setKeyStrokeColorResource(R.color.primary)
                .setKeyStrokeWidth(R.dimen.key_stroke_width)
                .setKeyTextColorResource(R.color.primary)
                .setKeyTextSize(R.dimen.key_text_size));


        pinView.setIndicator(new CircleIndicator.Builder(pinView)
                .setIndicatorRadius(R.dimen.indicator_radius)
                .setIndicatorFilledColorResource(R.color.primary)
                .setIndicatorStrokeColorResource(R.color.primary)
                .setIndicatorStrokeWidth(R.dimen.indicator_stroke_width));

        pinView.setKeyNames(new KeyNamesBuilder().setKeyOne("1")
                .setKeyTwo("2")
                .setKeyThree("3")
        .setKeyFour("4")
        .setKeyFive("5")
        .setKeySix("6")
        .setKeySeven("7")
        .setKeyEight("8")
        .setKeyNine("9")
        .setKeyZero("0"));

        pinView.setDefaults();

        pinView.setAuthenticationListener(new AuthenticationListener() {
            @Override
            public void onAuthenticationSuccessful()
            {
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void onAuthenticationFailed()
            {
                setResult(BAD_PIN_RESULT);
            }
        });
    }

}

