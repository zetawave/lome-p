package me.lo.lomefree.Model.Shredding;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.RandomAccessFile;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

import at.grabner.circleprogress.CircleProgressView;
import me.lo.lomefree.Activities.ShredderActivity;
import me.lo.lomefree.Interfaces.ShredAlgos;
import me.lo.lomefree.Interfaces.ShredSchemes;
import me.lo.lomefree.Model.RandomUtils.RandNGenerator;
import me.lo.lomefree.R;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;


public class ShreddingOperation implements ShredAlgos, ShredSchemes
{
    private CircleProgressView circleProgressView;
    private String algorithm;
    private File [] files;
    private int fileCount = 0;
    private Context context;
    private AppCompatActivity activity;

    public ShreddingOperation(CircleProgressView circleProgressView, String algorithm, File[] files, Context context, AppCompatActivity activity)
    {
        this.circleProgressView = circleProgressView;
        this.algorithm = algorithm;
        this.files = files;
        this.context = context;
        this.activity = activity;
        initShredOperation();
    }


    private void initShredOperation()
    {
        fileCount = 0;

        setInitialViewFiled();
        switch(algorithm)
        {
            case VSITR:
                VSITRStart();
                break;
            case GUTMANN:
                gutmannStart();
                break;
        }
    }

    private long getTotalSize()
    {
        long totalLength = 0;

        for(File file : files)
            totalLength+=file.length();
        return totalLength;
    }

    private void VSITRStart()
    {
        try
        {
            final int VSITR_PASSES = 7;
            final byte[] ZERO_ARRAY = new byte[4096];
            final byte[] ONE_ARRAY = new byte[4096];
            long totalLength = 0;
            double count = 0;
            long cycledLength;

            Arrays.fill(ZERO_ARRAY, (byte) 0);
            Arrays.fill(ONE_ARRAY, (byte) 1);
            totalLength = getTotalSize();
            cycledLength = totalLength * VSITR_PASSES;

            for(File file : files) {
                if(!ShredderActivity.cancel_task) {
                    long length = file.length();
                    double progress;

                    byte[] toWrite;

                    SecureRandom secRand = new SecureRandom();
                    RandomAccessFile raf = new RandomAccessFile(file, "rws");

                    for (int i = 0; i < VSITR_PASSES; i++) {
                        raf.seek(0);
                        raf.getFilePointer();
                        long pos = 0;
                        byte[] data = new byte[4096];
                        switch (i) {
                            case 0:
                            case 2:
                            case 4:
                                while (pos < length && !ShredderActivity.cancel_task) {
                                    raf.write(ZERO_ARRAY);
                                    pos += data.length;
                                    count += ZERO_ARRAY.length;
                                    progress = ((count / (double) cycledLength) * (double) 100);
                                    setProgress((float) progress);
                                }
                                break;
                            case 1:
                            case 3:
                            case 5:
                                while (pos < length && !ShredderActivity.cancel_task) {
                                    raf.write(ONE_ARRAY);
                                    pos += data.length;
                                    count += ONE_ARRAY.length;
                                    progress = ((count / (double) cycledLength) * (double) 100);
                                    setProgress((float) progress);
                                }
                                break;
                            case 6:
                                while (pos < length && !ShredderActivity.cancel_task) {
                                    toWrite = RandNGenerator.generateRandomBytes(data, secRand);
                                    raf.write(toWrite);
                                    pos += data.length;
                                    count += toWrite.length;
                                    progress = ((count / (double) cycledLength) * (double) 100);
                                    setProgress((float) progress);
                                }
                        }
                    }
                    raf.close();
                    if(!ShredderActivity.cancel_task)
                        FileManager.deleteFile(file);
                    setIncrementalViewField();
                }

            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void gutmannStart()
    {
        try{
            final int GUTMANN_PASSES = 35;
            long totalLength = 0;
            double count = 0;
            long cycledLength;
            SecureRandom secRand = new SecureRandom();
            totalLength = getTotalSize();
            cycledLength = totalLength * GUTMANN_PASSES;

            for(File file : files) {
                if(!ShredderActivity.cancel_task) {
                    long length = file.length();
                    double progress;

                    byte[] toWrite;
                    RandomAccessFile raf = new RandomAccessFile(file, "rws");

                    for (int i = 1; i < GUTMANN_PASSES + 1; i++) {
                        raf.seek(0);
                        raf.getFilePointer();
                        long pos = 0;
                        byte[] data = new byte[4096];
                        switch (i) {
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 32:
                            case 33:
                            case 34:
                            case 35:
                                while (pos < length  && !ShredderActivity.cancel_task) {
                                    toWrite = RandNGenerator.generateRandomBytes(data, secRand);
                                    raf.write(toWrite);
                                    pos += data.length;
                                    count += toWrite.length;
                                    progress = ((count / (double) cycledLength) * (double) 100);
                                    setProgress((float) progress);
                                }
                                break;

                            default:
                                while (pos < length && !ShredderActivity.cancel_task) {
                                    byte[] intToByte = new byte[4096];
                                    Arrays.fill(intToByte, Objects.requireNonNull(GUTTMAN_SCHEME.get(i)).byteValue());
                                    raf.write(intToByte);
                                    pos += data.length;
                                    count += intToByte.length;
                                    progress = ((count / (double) cycledLength) * (double) 100);
                                    setProgress((float) progress);
                                }
                                break;
                        }
                    }
                    raf.close();
                    if(!ShredderActivity.cancel_task)
                        FileManager.deleteFile(file);
                    setIncrementalViewField();
                }
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
            Log.d("Exception", Arrays.toString(ex.getStackTrace()));
        }
    }

    private void setInitialViewFiled()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ShredderActivity.numFiles.setText(context.getString(R.string.numFileSelected)
                        .concat(": ")
                        .concat(String.valueOf(files.length)));
                ShredderActivity.algorithm.setText(context.getString(R.string.shredAlgo)
                        .concat(": ")
                        .concat(algorithm));
                ShredderActivity.completedFiles.setText(context.getString(R.string.completedFiles)
                        .concat(": ")
                        .concat(String.valueOf((fileCount)))
                        .concat("/")
                        .concat(String.valueOf(files.length)));
            }
        });
    }

    private void setIncrementalViewField()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ShredderActivity.completedFiles.setText(context.getString(R.string.completedFiles)
                        .concat(": ")
                        .concat(String.valueOf((fileCount+=1)))
                        .concat("/")
                        .concat(String.valueOf(files.length)));
            }
        });
    }

    private void setProgress(float progress)
    {
        circleProgressView.setValue(progress);
    }

    public byte[] toByteArray(int value) {
        return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value};
    }
}

