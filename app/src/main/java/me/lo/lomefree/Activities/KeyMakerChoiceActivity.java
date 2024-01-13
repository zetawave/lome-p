package me.lo.lomefree.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import me.lo.lomefree.Interfaces.DBNames;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.Keys.Entities.KeyFile;
import me.lo.lomefree.Keys.KeyUtil.KeyGenerator;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;

import static me.lo.lomefree.Globals.GlobalValues.ERROR;
import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.Globals.GlobalValues.SHORT;
import static me.lo.lomefree.Globals.GlobalValues.SUCCESS;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;

public class KeyMakerChoiceActivity extends AppCompatActivity implements DBNames, SettingsParameters {
    private CardView sprintMake;
    private CardView manualBuild;
    private EditText [] et = new EditText[2];
    private AppCompatActivity thisActivity;
    private CustomSettings customSettings;
    private Context thisContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this;
        thisActivity = this;
        setContentView(R.layout.activity_key_maker_choice);
        getCustomSettings();
        getComponents();
    }
    private void getCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(this);
        customSettings = DataManager.getCustomSettings(prefs);
    }

    private void getComponents()
    {
        sprintMake = findViewById(R.id.sprintMake);
        manualBuild = findViewById(R.id.manualBuild);


        setListeners();
    }

    private void setListeners()
    {
        sprintMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSprintMake();
            }
        });
        manualBuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manualBuildIntent = new Intent(getApplicationContext(), KeyMakerActivity.class);
                startActivity(manualBuildIntent);
            }
        });
    }


    private void startSprintMake()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.confirm_title)
                .setMessage(R.string.key_file_sprint_make_message)
                .setIcon(R.drawable.ic_question_answer_black_24dp)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            KeyFile keyFile = KeyGenerator.getRandomKeyFile(thisActivity);
                            saveKeyFileToDB(keyFile);
                            finish();
                        }catch(Exception ex)
                        {
                            makeToast(getApplicationContext(), getString(R.string.key_make_error), ERROR, LONG);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("INFO_DEBUG", "CHIAVE SPRINT NON CREATA");
                    }
                })
                .show();
    }

    private void saveKeyFileToDB(KeyFile keyfile)
    {
        List<KeyFile> temp_keyFiles;
        ArrayList<String[]> temp_key_info;
        List<KeyFile> keyFiles;
        ArrayList<String[]> key_info;

        temp_keyFiles = (List<KeyFile>) DataManager.loadPrivateObject(getApplicationContext(), KEY_FILES_DB_NAME);
        temp_key_info = (ArrayList<String[]>) DataManager.loadPrivateObject(getApplicationContext(), KEY_INFO_DB_NAME);

        keyFiles = (temp_keyFiles == null ? new ArrayList<KeyFile>() : temp_keyFiles);
        key_info = (temp_key_info == null ? new ArrayList<String[]>() : temp_key_info);

        keyFiles.add(keyfile);
        key_info.add(new String[]{keyfile.getName(), keyfile.getDescription()});

        if(DataManager.savePrivateObject(getApplicationContext(), KEY_FILES_DB_NAME, keyFiles) && DataManager.savePrivateObject(getApplicationContext(), KEY_INFO_DB_NAME, key_info))
        {
            makeToast(getApplicationContext(), getString(R.string.success_save_manager_key), SUCCESS, SHORT);
        }else
            makeToast(getApplicationContext(), getString(R.string.key_make_error), SUCCESS, SHORT);
    }
}
