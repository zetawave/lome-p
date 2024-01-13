package me.lo.lomefree.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Keys.KeyUtil.KeyUtils;
import me.lo.lomefree.R;

import static me.lo.lomefree.Globals.GlobalValues.INFO;
import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.Globals.GlobalValues.WARNING;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.Globals.GlobalValues.SUCCESS;

public class QRScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler, RRCodes
{
    private ZXingScannerView mScannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(0);
        mScannerView.setFlash(false);
        mScannerView.setBorderColor(Color.parseColor("#2196F3"));

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


    @Override
    public void handleResult(Result rawResult)
    {
        String result;
        if(getIntent().getIntExtra("verify_qr", 88) == VERIFY_KEY_REQUEST)
        {
            result = rawResult.getText();
            makeToast(this, result, INFO, LONG);
            if(KeyUtils.verifyKeyStringLenght(result))
                makeToast(this, getString(R.string.key_valid), SUCCESS, Toast.LENGTH_LONG);
            else
                makeToast(this, getString(R.string.parameter_wrong_keyfile), WARNING, Toast.LENGTH_LONG);
        }else {
            Intent thisIntent = getIntent();
            result = rawResult.getText();
            thisIntent.putExtra("QR_KEY", (result == null ? "" : result));
            setResult(QR_KEY_RESULT_CODE, thisIntent);
            //makeToast(this, rawResult.getText(), WARNING, Toast.LENGTH_LONG);
        }
        mScannerView.stopCamera();
        finish();
    }
}
