package me.lo.lomefree.BillingPurchaseUtils;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;

import me.lo.lomefree.R;

public class BillingPurchaseOperation
{
    public static BillingProcessor bp;

    public static void initializeBillingProcessor(Context thisContext, AppCompatActivity thisActivity, BillingProcessor.IBillingHandler thisInterface)
    {
        bp = new BillingProcessor(thisContext, thisContext.getString(R.string.publicLicenseAppKey), thisInterface);
        bp.initialize();
        Log.d("BILLING ","Billing Initialization Process");
    }

}
