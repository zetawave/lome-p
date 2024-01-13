package me.lo.lomefree.UIUtils.Graphics;

import androidx.appcompat.app.AppCompatActivity;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.SettingsUtils.ParticularSetting;

public class UIHelpers implements SettingsParameters
{

    public static void showKeyFileManagerTapWizard(final AppCompatActivity activity, final CustomSettings customSettings, final ParticularSetting first_access_ps)
    {
        new TapTargetSequence(activity)
                .targets(
                        TapTarget.forView(activity.findViewById(R.id.makeKey), activity.getString(R.string.helper_create_key), activity.getString(R.string.helper_create_key_description)),
                        TapTarget.forView(activity.findViewById(R.id.add_new_key), activity.getString(R.string.helper_import_key), activity.getString(R.string.helper_import_key_description))
                                .dimColor(android.R.color.white)
                                .descriptionTextColor(R.color.white)
                                .outerCircleColor(R.color.primary_dark)
                                .targetCircleColor(R.color.Mygrey)
                                .textColor(android.R.color.white)
                                .titleTextColor(R.color.white)
                                .dimColor(R.color.white)
                                .cancelable(false))
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        first_access_ps.setValue(false);
                        customSettings.setOtherPreference(FIRST_KEY_MANAGER_ACCESS, first_access_ps);
                        customSettings.saveOtherPreferences(activity);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                    }
                }).start();
    }

    public static void showRSAKeyFileManagerTapWizard(final AppCompatActivity activity, final CustomSettings customSettings, final ParticularSetting first_access_ps)
    {
        new TapTargetSequence(activity)
                .targets(
                        TapTarget.forView(activity.findViewById(R.id.makeKey), activity.getString(R.string.helper_create_key), activity.getString(R.string.helper_create_key_description)),
                        TapTarget.forView(activity.findViewById(R.id.add_new_key), activity.getString(R.string.helper_import_key), activity.getString(R.string.helper_import_key_description))
                                .dimColor(android.R.color.white)
                                .descriptionTextColor(R.color.white)
                                .outerCircleColor(R.color.primary_dark)
                                .targetCircleColor(R.color.Mygrey)
                                .textColor(android.R.color.white)
                                .titleTextColor(R.color.white)
                                .dimColor(R.color.white)
                                .cancelable(false))
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        first_access_ps.setValue(false);
                        customSettings.setOtherPreference(FIRST_KEY_MANAGER_ACCESS, first_access_ps);
                        customSettings.saveOtherPreferences(activity);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                    }
                }).start();
    }

    public static void showHiderTapWizard(final AppCompatActivity activity, final CustomSettings customSettings, final ParticularSetting first_access_ps)
    {
        new TapTargetSequence(activity)
                .targets(
                        TapTarget.forView(activity.findViewById(R.id.addMedia),activity.getString(R.string.select_from_gallery),activity.getString(R.string.select_from_gallery_description)  ),
                        TapTarget.forView(activity.findViewById(R.id.addFile), activity.getString(R.string.select_from_memory), activity.getString(R.string.select_from_memory_description))
                                .dimColor(android.R.color.white)
                                .descriptionTextColor(R.color.white)
                                .outerCircleColor(R.color.primary_dark)
                                .targetCircleColor(R.color.Mygrey)
                                .textColor(android.R.color.white)
                                .titleTextColor(R.color.white)
                                .dimColor(R.color.white)
                                .cancelable(false))
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        first_access_ps.setValue(false);
                        customSettings.setOtherPreference(FIRST_HIDER_ACCESS, first_access_ps);
                        customSettings.saveOtherPreferences(activity);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                    }
                }).start();
    }

    public static void showElaborationTapWizard(final AppCompatActivity activity, final CustomSettings customSettings, final ParticularSetting first_access_ps)
    {
        new TapTargetSequence(activity)
                .targets(TapTarget.forView(activity.findViewById(R.id.qrkeyimage), activity.getString(R.string.helper_choose_key), activity.getString(R.string.helper_choose_key_description)),
                TapTarget.forView(activity.findViewById(R.id.toArchive), activity.getString(R.string.helper_zip_mode), activity.getString(R.string.helper_zip_mode_description))
                                .dimColor(android.R.color.white)
                                .descriptionTextColor(R.color.white)
                                .outerCircleColor(R.color.primary_dark)
                                .targetCircleColor(R.color.Mygrey)
                                .textColor(android.R.color.white)
                                .titleTextColor(R.color.white)
                                .dimColor(R.color.white)
                        .transparentTarget(true)
                                .cancelable(false))
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        first_access_ps.setValue(false);
                        customSettings.setOtherPreference(FIRST_ELABORATION_ACCESS, first_access_ps);
                        customSettings.saveOtherPreferences(activity);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                    }
                }).start();
    }

}
