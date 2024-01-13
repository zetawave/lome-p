package me.lo.lomefree.UIUtils.Graphics;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import me.lo.lomefree.Activities.AboutActivity;
import me.lo.lomefree.Activities.KeyFileManager;
import me.lo.lomefree.Activities.KeyMakerChoiceActivity;
import me.lo.lomefree.Activities.RSAKeyManager;
import me.lo.lomefree.Activities.SpecialToolsActivity;
import me.lo.lomefree.BillingPurchaseUtils.BillingPurchaseOperation;
import me.lo.lomefree.Globals.GlobalValues;
import me.lo.lomefree.Interfaces.ItemIdentifier;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;

import static me.lo.lomefree.Globals.GlobalValues.INFO;
import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;

public class UIComponentBuilder implements ItemIdentifier, SettingsParameters
{

    public static void buildDrawer(Toolbar toolbar, final Context context, final AppCompatActivity activity, CustomSettings customSettings, BillingProcessor.IBillingHandler thisInterface)
    {

        boolean isAdFree = AdsHelpers.isAdsFree(customSettings, AD_FREE, context, activity);

        final PrimaryDrawerItem about = new PrimaryDrawerItem().withIdentifier(ABOUT_ITEM).withName(R.string.about_lome);
        PrimaryDrawerItem noAds = new PrimaryDrawerItem().withIdentifier(NO_ADS).withName(R.string.noAds);
        PrimaryDrawerItem help = new PrimaryDrawerItem().withIdentifier(HELP_ITEM).withName(R.string.Help_lome);
        PrimaryDrawerItem keyManager = new PrimaryDrawerItem().withIdentifier(KEY_MANAGER_ITEM).withName(R.string.key_manager_menu_string);
        final PrimaryDrawerItem rsaKeyManager = new PrimaryDrawerItem().withIdentifier(KEY_RSA_MANGER).withName(R.string.rsa_manager_menu);
        PrimaryDrawerItem keyCreator = new PrimaryDrawerItem().withIdentifier(KEY_CREATOR_ITEM).withName(R.string.make_key_menu_string);
        PrimaryDrawerItem specialTools = new PrimaryDrawerItem().withIdentifier(SPECIAL_TOOLS).withName(R.string.special_tools);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(R.drawable.gradient_1)
                .build();

        headerResult.addProfiles(new ProfileDrawerItem().withName("Lome").withIcon(R.drawable.partialogo));


        new DrawerBuilder()
                .withActivity(activity)
                .withAccountHeader(headerResult)
                .withMultiSelect(false)
                .withToolbar(toolbar)
                .withCloseOnClick(true)
                .withSliderBackgroundDrawable(activity.getDrawable(R.drawable.custom_shadow3))
                .addDrawerItems(new SectionDrawerItem()
                                .withName(R.string.manager)
                                .withDivider(false)
                                .withTextColor(Color.parseColor("#536DFE")),
                        keyManager.withIcon(R.drawable.ic_vpn_key_black_24dp).withSelectable(false),
                        rsaKeyManager.withIcon(R.drawable.rsamenulogo).withSelectable(false),
                        new SectionDrawerItem()
                                .withName(R.string.tools)
                                .withDivider(false)
                                .withTextColor(Color.parseColor("#536DFE")),
                        keyCreator.withIcon(R.drawable.ic_note_add_black_24dp).withSelectable(false),
                        specialTools.withIcon(R.drawable.star).withSelectable(false),
                       // fastOperations.withIcon(R.drawable.ic_fast_forward_black_24dp).withSelectable(false),
                        new SectionDrawerItem()
                                .withName(R.string.informations)
                                .withDivider(true)
                                .withTextColor(Color.parseColor("#536DFE")),
                        about.withIcon(R.drawable.ic_person_black_24dp).withSelectable(false),
                        help.withIcon(R.drawable.ic_info_black_24dp).withSelectable(false))
                        //isAdFree ? null : noAds.withIcon(R.drawable.no_ads).withSelectable(false))
                .withDrawerWidthDp(250)
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        long identifier = drawerItem.getIdentifier();

                        if(identifier == ABOUT_ITEM)
                        {
                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation(activity, view, "transition");
                            Intent intent = new Intent(activity, AboutActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ActivityCompat.startActivity(activity, intent, options.toBundle());

                        }else if(drawerItem.getIdentifier() == KEY_MANAGER_ITEM)
                        {
                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation(activity, view, "transition");
                            Intent intent = new Intent(activity, KeyFileManager.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ActivityCompat.startActivity(activity, intent, options.toBundle());
                        }else if(identifier == HELP_ITEM)
                        {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalValues.help_section_site)));
                        }else if(identifier == KEY_CREATOR_ITEM)
                        {

                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation(activity, view, "transition");
                            Intent intent = new Intent(activity, KeyMakerChoiceActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ActivityCompat.startActivity(activity, intent, options.toBundle());

                        }else if(identifier == KEY_RSA_MANGER)
                        {
                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation(activity, view, "transition");
                            Intent intent = new Intent(activity, RSAKeyManager.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ActivityCompat.startActivity(activity, intent, options.toBundle());
                        }else if(identifier == SPECIAL_TOOLS)
                        {
                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation(activity, view, "transition");
                            Intent intent = new Intent(activity, SpecialToolsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ActivityCompat.startActivity(activity, intent, options.toBundle());
                        }else if(identifier == FAST_OPERATIONS)
                        {

                            makeToast(context, "Questa Feature è in via di sviluppo!", INFO, LONG);
                        }
                        return false;
                    }
                }).build();
    }



    private static MaterialAboutCard getNewCard(String title, int logo, String[] text, String[] subText, int[] subIcon, final AppCompatActivity activity)
    {
        MaterialAboutCard.Builder cardBuilder = new MaterialAboutCard.Builder();

        cardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(title)
                .icon(logo)
                .build());

        for(int i=0; i<text.length; i++)
        {
            if(subIcon[i] == R.drawable.open_source)
                cardBuilder.addItem(new MaterialAboutActionItem.Builder()
                        .text(text[i])
                        .subText(subText[i])
                        .icon(subIcon[i])
                        .setOnClickAction(new MaterialAboutItemOnClickAction() {
                            @Override
                            public void onClick() {
                                Alerts.showOpenSourceInformationsDialog(activity);
                            }
                        })
                        .build());
            else if(subIcon[i] == R.drawable.ic_email_black_24dp)
                cardBuilder.addItem(new MaterialAboutActionItem.Builder()
                        .text(text[i])
                        .subText(subText[i])
                        .icon(subIcon[i])
                        .setOnClickAction(new MaterialAboutItemOnClickAction() {
                            @Override
                            public void onClick() {
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                                emailIntent.setData(Uri.parse("mailto: lo2me.info@gmail.com"));
                                activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.send_feedback)));
                            }
                        })
                        .build());
            else if(subIcon[i] == R.drawable.terms)
                cardBuilder.addItem(new MaterialAboutActionItem.Builder()
                        .text(text[i])
                        .subText(subText[i])
                        .icon(subIcon[i])
                        .setOnClickAction(new MaterialAboutItemOnClickAction() {
                            @Override
                            public void onClick() {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalValues.terms_section_site)));
                            }
                        })
                        .build());
            else if(subIcon[i] == R.drawable.privacyrules)
                cardBuilder.addItem(new MaterialAboutActionItem.Builder()
                        .text(text[i])
                        .subText(subText[i])
                        .icon(subIcon[i])
                        .setOnClickAction(new MaterialAboutItemOnClickAction() {
                            @Override
                            public void onClick() {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalValues.privacy_section_site)));
                            }
                        })
                        .build());
            else if(subIcon[i] == R.drawable.ic_web_black_24dp)
                cardBuilder.addItem(new MaterialAboutActionItem.Builder()
                        .text(text[i])
                        .subText(subText[i])
                        .icon(subIcon[i])
                        .setOnClickAction(new MaterialAboutItemOnClickAction() {
                            @Override
                            public void onClick() {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalValues.website)));
                            }
                        })
                        .build());

            else
                cardBuilder.addItem(new MaterialAboutActionItem.Builder()
                        .text(text[i])
                        .subText(subText[i])
                        .icon(subIcon[i])
                        .build());
        }

        return cardBuilder.build();

    }

    public static MaterialAboutList getMaterialAboutList(String [] titles, int [] logos, String [][] text, String [][] subText, int [][] subIcon, AppCompatActivity activity)
    {
        MaterialAboutList malist = new MaterialAboutList();

        for(int i=0; i<titles.length; i++)
        {
            malist.addCard(UIComponentBuilder.getNewCard(titles[i], logos[i], text[i], subText[i], subIcon[i], activity));
        }

        return malist;
    }

    public static ProgressDialog getProgressDialog(Context activity, String message)
    {
        ProgressDialog pd = new ProgressDialog(activity);
        pd.setTitle("");
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        return pd;

    }

}
