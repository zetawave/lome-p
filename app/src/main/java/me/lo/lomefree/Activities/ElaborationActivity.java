package me.lo.lomefree.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;

import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;

import com.nononsenseapps.filepicker.Utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.lo.lomefree.Globals.GlobalValues;
import me.lo.lomefree.Interfaces.ChooserModes;
import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Interfaces.KeyModality;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Interfaces.SettingsParameters;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.SettingsUtils.ParticularSetting;
import me.lo.lomefree.UIUtils.Graphics.Alerts;
import me.lo.lomefree.UIUtils.Graphics.UIComponentBuilder;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Files.Entities.FileBox;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;
import me.lo.lomefree.Keys.Entities.KeyFile;
import me.lo.lomefree.Utils.Misc.PermissionChecker;
import me.lo.lomefree.Utils.Files.FDManager.QRCodeUtils;
import mehdi.sakout.fancybuttons.FancyButton;

import static me.lo.lomefree.Misc.Auxiliary.TextEditing.getHtmlSpan;
import static me.lo.lomefree.UIUtils.Graphics.Alerts.makeToast;
import static me.lo.lomefree.Globals.GlobalValues.ERROR;
import static me.lo.lomefree.Globals.GlobalValues.LONG;
import static me.lo.lomefree.Globals.GlobalValues.N_KEY_MODALITY;
import static me.lo.lomefree.Globals.GlobalValues.SUCCESS;
import static me.lo.lomefree.Globals.GlobalValues.WARNING;
import static me.lo.lomefree.UIUtils.Graphics.UIHelpers.showElaborationTapWizard;
import static me.lo.lomefree.UIUtils.UISecurity.WindowSecurity.initSecurityFlags;

public class ElaborationActivity extends AppCompatActivity implements KeyModality, Extensions, ChooserModes, RRCodes, SettingsParameters
{
    private final CardView []  keyCard = new CardView[N_KEY_MODALITY];
    private Button backButton, viewPaths;
    private EditText archive_name;
    private FancyButton play;
    private TextView file_number;
    private TextView total_size;
    private TextView pathToSave;
    private TextView deletion;
    private FileBox box;
    private List<KeyFile> keyfiles;
    private String key = null;
    private static boolean encrypt_mode = false;
    private Toolbar toolbar;
    private boolean toArchiveFlag = false;
    private int keyMode = -1;
    private CustomSettings customSettings;
    private final EditText [] etenc = new EditText[2];
    private EditText etdec;
    private Switch toArchive;
    private Context thisContext;
    private ProgressDialog pd;
    private String infoPaths;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this;
        getCustomSettings();
        initSecurityFlags(customSettings, getWindow());
        setContentView(R.layout.activity_elaboration);
        verifyFirstAccess();
        getViewComponents();
        acquireKeyCard();
        setListeners();
        retrieve_files();
    }

    private void getCustomSettings()
    {
        SharedPreferences prefs = DataManager.getSharedPreferences(this);
        customSettings = DataManager.getCustomSettings(prefs);
    }


    private void getViewComponents()
    {
        backButton = findViewById(R.id.back_button);
        //modify_path = findViewById(R.id.modify_path);
        play = findViewById(R.id.proceed);
        total_size = findViewById(R.id.total_size);
        file_number = findViewById(R.id.file_number);
        deletion = findViewById(R.id.deletion);
        toolbar = findViewById(R.id.elaboration_toolbar);
        pathToSave = findViewById(R.id.current_path_to_save);
        toArchive = findViewById(R.id.toArchive);
        archive_name = findViewById(R.id.archive_name);
        viewPaths = findViewById(R.id.viewPaths);

    }

    private void verifyFirstAccess()
    {

        ParticularSetting first_access_ps = customSettings.getOtherPreference(FIRST_ELABORATION_ACCESS);
        Boolean first_access;
        if(first_access_ps != null)
            first_access = (Boolean) first_access_ps.getValue();
        else {
            first_access_ps = new ParticularSetting();
            first_access_ps.setPreference(FIRST_ELABORATION_ACCESS);
            first_access_ps.setValue(true);
            customSettings.setOtherPreference(FIRST_ELABORATION_ACCESS, first_access_ps);
            first_access = true;
        }
        if(first_access)
        {
            showElaborationTapWizard(this, customSettings, first_access_ps);
        }

    }

    private void acquireKeyCard()
    {
        keyCard[MODE_QRCODE] = findViewById(R.id.qrcode_key);
        keyCard[MODE_KEYFILE] = findViewById(R.id.key_file);
        keyCard[MODE_PASSWORD] = findViewById(R.id.password);
    }

    private void setListeners()
    {
        setButtonListeners();
        setCardListeners();
    }

    private void setButtonListeners()
    {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finishAndRemoveTask();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showConfirmDialog();

            }
        });

        toArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toArchiveFlag = toArchive.isChecked();
                archive_name.setVisibility((toArchiveFlag ? View.VISIBLE : View.INVISIBLE));
            }
        });

        viewPaths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPathsOfFiles();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void showPathsOfFiles()
    {


        pd = UIComponentBuilder.getProgressDialog(thisContext, getString(R.string.load_all_paths));
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cancelAsyncOperation();
            }
        });
        pd.show();

        executor.execute(() -> {
            if (box.getAllFiles().length > 0) {
                File[] files = box.getAllFiles();
                File storageDir = new File(GlobalValues.sdCardPath);
                infoPaths = "";
                for (File instance : files)
                    infoPaths = infoPaths.concat(storageDir.toURI().relativize(instance.toURI()).getPath()).concat("\n\n");
            }

            handler.post(() -> {
                pd.dismiss();
                postInfoLoading();
            });
        });
    }

    private void postInfoLoading()
    {
        AlertDialog.Builder builder = Alerts.showConfirmDialog(thisContext,getString(R.string.selected_files), infoPaths, R.drawable.infovalues);
        builder.setPositiveButton(R.string.ok, null);
        builder.create().show();
    }

    private void cancelAsyncOperation()
    {
        executor.shutdownNow();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finishAndRemoveTask();
    }


    private void startOperation()
    {
        Intent start_task = new Intent(getApplicationContext(), ProgressActivity.class);
        start_task.putExtra("FileBox", box);
        start_task.putExtra("KeyMode", keyMode);
        start_task.putExtra("to_archive", toArchiveFlag);
        switch(keyMode)
        {
            case MODE_PASSWORD:
            case MODE_KEYFILE:
            case MODE_QRCODE:
                start_task.putExtra("KEY", key);
                break;

            case MODE_KEYFILE_MULTI:
                start_task.putExtra("MULTIKEY", (Serializable) keyfiles);
                break;

        }
        start_task.putExtra("OP_TYPE", (encrypt_mode ? "ENC" : "DEC"));
        startActivityForResult(start_task, PROGRESS_ACTIVITY_REQUEST);
    }

    private void showConfirmDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_title);
        builder.setMessage(R.string.secure_proceed_confirmation);
        builder.setIcon(R.drawable.ic_question_answer_black_24dp);
        builder.setPositiveButton(R.string.start_elaboration, (dialog, which) -> {
            if(keyMode != -1) {
                if (isSuitableForStart())
                    startOperation();
                else
                    makeToast(getApplicationContext(), getString(R.string.insert_key_warning), WARNING, LONG);
            }else
                makeToast(getApplicationContext(), getString(R.string.insert_key_warning), WARNING, LONG);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> Log.d(getString(R.string.debug_elaboration_title), getString(R.string.debug_elaboration_message1)));
        builder.create();
        builder.show();
    }


    private boolean isSuitableForStart()
    {
        setArchive_name();
        return ((key != null && !key.equals("")) || (keyfiles != null && keyfiles.size() >= 2));
    }

    private void setArchive_name()
    {
        if(box.getArchiveName() == null || box.getArchiveName().equals("")) {
            if (String.valueOf(archive_name.getText()).equals(""))
                box.setArchiveName(FileBox.DEFAULT_ARCHIVE_NAME);
            else
                box.setArchiveName(String.valueOf(archive_name.getText()));
        }
    }

    private void setCardListeners()
    {
        for(int i=0; i<keyCard.length; i++)
        {
            switch(i)
            {
                case MODE_QRCODE:
                    keyCard[i].setOnClickListener(v -> {
                        Intent qrScanner = new Intent(getApplicationContext(), QRScanner.class);
                        startActivityForResult(qrScanner, QR_KEY_REQUEST_CODE);
                    });
                    break;
                case MODE_KEYFILE:
                    keyCard[i].setOnClickListener(v -> keyFileChoice(getString(R.string.key_file_options), getString(R.string.key_file_options_message), ElaborationActivity.this));
                    break;
                case MODE_PASSWORD:
                    keyCard[i].setOnClickListener(v -> passwordInput());
                    break;
            }
        }
    }

    private void passwordInput()
    {

        AlertDialog.Builder builder;
        etdec = new EditText(this);

        if(encrypt_mode)
            builder = Alerts.makeDoubleEditTextDialogWithNameFiltered(this, getString(R.string.elaboration_pass_title_doublepass), getString(R.string.elaboration_pass_message_doublepass), getString(R.string.elaboration_pass_hint_doublepass), getString(R.string.elaboration_pass_rehint_doublepass), etenc, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        else
            builder = Alerts.makeSingleEditTextDialog(this, getString(R.string.elaboration_pass_title_singlepass), getString(R.string.elaboration_pass_message_singlepass), getString(R.string.elaboration_pass_hint_singlepass), etdec,InputType.TYPE_CLASS_TEXT |  InputType.TYPE_TEXT_VARIATION_PASSWORD);

        builder.setIcon(R.drawable.ic_vpn_key_black_24dp);
        builder.setPositiveButton(getString(R.string.confirm_title), (dialog, which) -> {
            String pass;
            pass = String.valueOf(encrypt_mode ? String.valueOf(etenc[0].getText()) : String.valueOf(etdec.getText()));

            if(pass != null && !pass.equals("") && (!encrypt_mode || pass.equals(String.valueOf(etenc[1].getText()))))
            {
                setCardDefaultBColor();
                changeCardBColor(MODE_PASSWORD);
                keyMode = MODE_PASSWORD;
                key = pass;
            }else {
                setCardDefaultBColor();
                key = null;
                makeToast(getApplicationContext(), getString(R.string.password_doesent_match), WARNING, LONG);
            }

        }).setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
            setCardDefaultBColor();
            key = null;
            Log.d(getString(R.string.debug_pass_input_cancel), getString(R.string.cancel));
        }).show();
    }

    private void keyFileChoice(@SuppressWarnings("SameParameterValue") String title, final String message, Context context)
    {

        new androidx.appcompat.app.AlertDialog.Builder(context).setTitle(title)
                .setIcon(R.drawable.ic_question_answer_black_24dp)
                .setMessage(message)
                .setPositiveButton("Manager", (dialog, which) -> {
                    Intent keyManager = new Intent(getApplicationContext(), KeyFileManager.class);
                    if(encrypt_mode)
                        keyManager.putExtra("ENCRYPT_MODE", true);
                    keyManager.putExtra("VIEW_MODE", false);
                    startActivityForResult(keyManager, KEY_FILE_REQUEST_CODE);

                })
                .setNegativeButton(getString(R.string.explore_option), (dialog, which) -> startKeyChooser())
                .setNeutralButton(getString(R.string.cancel), (dialog, which) -> Log.d("Annullato", "Scelta key file: ANNULLATO"))
                .create()
                .show();
    }

    private void startKeyChooser()
    {
        if(PermissionChecker.verifyStoragePermissionGranted(this))
        {
            DataManager.getSingleFileChooser(getApplicationContext(), this);
        }
    }


    private void changeCardBColor(int MODE)
    {
        keyCard[MODE].getChildAt(0).setBackgroundResource(R.drawable.textview_border4);
    }

    private void setCardDefaultBColor()
    {
        for(CardView card : keyCard)
            card.getChildAt(0).setBackgroundColor(Color.parseColor("#eaeaea"));
    }


    private void retrieve_files()
    {
        try {
            box = (FileBox) getIntent().getSerializableExtra("FileBox");

            if(box.getFileNumber() == 0)
            {
                makeToast(getApplicationContext(),getString(R.string.elaboration_empty_directory), WARNING, LONG);
                finish();
            }
            setInformation();
            evaluateFileTypes(box);

        }catch(Exception ex)
        {
            makeToast(getApplicationContext(),getString(R.string.elaboration_pick_file_error)+ex.getMessage(), ERROR, LONG);
            finish();
        }

    }


    private void setInformation()
    {

        String number = getHtmlSpan("Files:", ""+box.getFileNumber(), "white", "#f4b342");
        String total = getHtmlSpan(getString(R.string.total_file_count), FileUtils.byteCountToDisplaySize(box.getTotalSizeInByte()), "white", "#f4b342");
        String path = getHtmlSpan(getString(R.string.save_dir_text), customSettings.getCustom_save_path(), "white", "#f4b342");
        String deletionMode = getHtmlSpan(getString(R.string.delete_at_the_end), (customSettings.isRemove_file() ? getString(R.string.delete_activated) : getString(R.string.delete_deactivated)),"white", "#f4b342");

        file_number.setText(Html.fromHtml(number), TextView.BufferType.SPANNABLE);
        total_size.setText(Html.fromHtml(total), TextView.BufferType.SPANNABLE);
        pathToSave.setText(Html.fromHtml(path), TextView.BufferType.SPANNABLE);
        deletion.setText(Html.fromHtml(deletionMode), TextView.BufferType.SPANNABLE);
    }

    private void evaluateFileTypes(FileBox box)
    {
        if(!box.isMixed())
        {
            if(box.isLome())
            {
                setModality(false, true);
                toolbar.setTitle(R.string.decipher);
                toArchive.setEnabled(false);
            }
            else if(box.isOther())
            {
                // Da cifrare
                setModality(true, false);
                toolbar.setTitle(R.string.cipher);
                toArchive.setEnabled(true);
            }
        }else
        {
            showChoiceDialog();
        }
    }


    private void showChoiceDialog()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setIcon(R.drawable.ic_info_black_24dp)
                .setTitle(R.string.choice_for_double_operation)
                .setMessage(R.string.choice_for_double_operation_message)
                .setPositiveButton(R.string.encrypt_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setModality(true, false);
                        box.onlyOtherFile();
                        setInformation();
                        toolbar.setTitle(R.string.cipher);
                        toArchive.setEnabled(true);

                    }
                })
                .setNegativeButton(R.string.decrypt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setModality(false, true);
                        box.onlyLomeFile();
                        setInformation();
                        toolbar.setTitle(R.string.decipher);
                        toArchive.setEnabled(false);
                    }
                })
                .setCancelable(false)
                .create()
        ;
        alert.show();
    }


    private void setModality(boolean enc_mode, boolean dec_mode)
    {
        encrypt_mode = enc_mode;
        boolean decrypt_mode = dec_mode;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        key = null;
        setCardDefaultBColor();
        switch (requestCode)
        {
            case KEY_FILE_REQUEST_CODE:
               // setCardDefaultBColor();
                if (resultCode == KeyFileManager.SINGLE_KEY_FILE_RESULT_CODE && data != null && data.getExtras() != null) {
                    try {
                        KeyFile keyfile = (KeyFile) data.getExtras().getSerializable("KEY_FILES");
                        this.key = keyfile != null ? keyfile.getValue() : null;
                        changeCardBColor(MODE_KEYFILE);
                        keyMode = MODE_KEYFILE;
                    } catch (Exception ex) {
                        makeToast(ElaborationActivity.this, getString(R.string.read_key_file_error) + ex.getMessage(), ERROR, LONG);
                    }
                } else if (resultCode == KeyFileManager.MULTI_KEY_FILE_RESULT_CODE && data != null && data.getExtras() != null) {
                    keyfiles = (List<KeyFile>) data.getExtras().getSerializable("KEY_FILES");
                    makeToast(getApplicationContext(), getString(R.string.automatic_key_file_search), SUCCESS, LONG);
                    changeCardBColor(MODE_KEYFILE);
                    keyMode = MODE_KEYFILE_MULTI;
                }
                break;
            case QR_KEY_REQUEST_CODE:
               // setCardDefaultBColor();
                if (resultCode == QR_KEY_RESULT_CODE && data != null && data.getExtras() != null) {
                    String key = data.getExtras().getString("QR_KEY");
                    if (key != null) {
                        changeCardBColor(MODE_QRCODE);
                        keyMode = MODE_QRCODE;
                        this.key = key;
                    }
                }
            case PROGRESS_ACTIVITY_REQUEST:
                if(resultCode == PROGRESS_ACTIVITY_MULTIPLE_ERRORS)
                {
                    String log = data.getStringExtra("BOX_LOG");
                    Alerts.showInfoDialog(getString(R.string.error_log), getString(R.string.errors_after_operation)+"\n\n"+log, this, R.drawable.ic_warning_black_24dp);
                }
                break;

            case SINGLE_FILE_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK)
                {
                    ArrayList<String> keyPath = new ArrayList<>();
                    List<Uri> files = Utils.getSelectedFilesFromResult(data);
                    for(Uri u: files)
                        keyPath.add(Utils.getFileForUri(u).getAbsolutePath());

                    String keyFilePath = keyPath.get(0);

                    try {

                        if (keyFilePath.endsWith(KEY_QR_EXT))
                            key = QRCodeUtils.readQrCodeFromFile(keyFilePath);
                        else if(keyFilePath.endsWith(KEY_FILE_EXT))
                        {
                            key = FileManager.readFile(keyFilePath);
                            changeCardBColor(MODE_KEYFILE);
                            keyMode = MODE_KEYFILE;
                        }else
                            makeToast(getApplicationContext(), getString(R.string.wrong_extension_key_file), WARNING, LONG);
                    }catch(Exception ex)
                    {
                        makeToast(getApplicationContext(), getString(R.string.read_key_file_error)+" "+ex.getMessage(), ERROR, LONG);
                    }
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
