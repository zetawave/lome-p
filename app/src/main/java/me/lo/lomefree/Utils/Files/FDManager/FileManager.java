package me.lo.lomefree.Utils.Files.FDManager;



import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import at.grabner.circleprogress.CircleProgressView;
import me.lo.lomefree.Activities.ShredderActivity;
import me.lo.lomefree.Globals.GlobalValues;
import me.lo.lomefree.Interfaces.Extensions;
import me.lo.lomefree.Interfaces.HiderPaths;
import me.lo.lomefree.Interfaces.RRCodes;
import me.lo.lomefree.Model.Cryptography.SimmetricKeyCiphers.SimmetricCipherOperation;
import me.lo.lomefree.Model.RandomUtils.RandNGenerator;
import me.lo.lomefree.Model.Shredding.ShreddingOperation;
import me.lo.lomefree.R;
import me.lo.lomefree.SettingsUtils.CustomSettings;
import me.lo.lomefree.Utils.Files.Entities.FileBox;
import me.lo.lomefree.Utils.Files.Entities.HiderItem;
import me.lo.lomefree.Utils.Misc.NotificationSender;

import static me.lo.lomefree.Globals.GlobalValues.GalleryLomePath;
import static me.lo.lomefree.Utils.Files.FDManager.Analyzers.analyzeFilesFromDir;
import static me.lo.lomefree.Utils.Misc.NotificationSender.notifyFileDeleteFileProgress;
import static me.lo.lomefree.Utils.Misc.NotificationSender.notifyFileElaborationProgress;

public class FileManager implements Extensions, RRCodes, HiderPaths
{

    private  static final ArrayList<String> names = new ArrayList<>();
    private  static final ArrayList<File> files = new ArrayList<>();
    private  static CustomSettings customSettings;
    private static ArrayList components = new ArrayList();
    private static Context thisContext;

    private static void initializeCustomSettings(Context context) {
        SharedPreferences prefs = DataManager.getSharedPreferences(context.getApplicationContext());
        customSettings = DataManager.getCustomSettings(prefs);
    }

    public static FileBox getFileBoxFromArrayOfPaths(ArrayList<String> paths)
    {
        FileBox box = new FileBox(paths.size());
        box.setOriginalPaths(paths);

        for (Object path : paths.toArray())
        {
            File instance = new File((String) path);
            box.putFile(instance.getName(), instance);

            if(instance.getName().endsWith(FILE_EXT) || instance.getName().endsWith(ZIP_EXT))
                box.setLome(true);
            else
                box.setOther(true);

        }
        box.setMixed(box.isLome() && box.isOther());
        box.setTotalSizeInByte();
        return box;
    }

    public static FileBox getFileBoxFromArrayOfPathsWithMirrorMode(ArrayList<String> paths, boolean mirror)
    {
        FileBox box = new FileBox(paths.size());
        box.setOriginalPaths(paths);
        for (Object path : paths.toArray())
        {
            File instance = new File((String) path);

            if(instance.getName().endsWith(FILE_EXT) || instance.getName().endsWith(ZIP_EXT)) {
                box.setLome(true);
                box.putFile(instance.getName(), instance);
            }
            else if(instance.isDirectory())
            {
                FileBox dirBox = GetAllFileFromDir(instance.getAbsolutePath(), mirror);
                File [] dirBoxFiles = dirBox.getAllFiles();
                for(File f : dirBoxFiles)
                {
                    if(f != null) {
                        box.putFile(f.getName(), f);
                        if (f.getName().endsWith(FILE_EXT) || f.getName().endsWith(ZIP_EXT))
                            box.setLome(true);
                        else
                            box.setOther(true);
                    }
                }
            }
            else {
                box.setOther(true);
                box.putFile(instance.getName(), instance);
            }
        }
        box.setMixed(box.isLome() && box.isOther());
        box.setTotalSizeInByte();
        return box;
    }

    public static FileBox getFileBoxFromArrayOfPaths(String [] paths)
    {
        FileBox box = new FileBox(paths.length);
        for (String path : paths)
        {
            File instance = new File(path);
            box.putFile(instance.getName(), instance);

            if(instance.getName().endsWith(FILE_EXT) || instance.getName().endsWith(ZIP_EXT))
                box.setLome(true);
            else
                box.setOther(true);

        }
        box.setMixed(box.isLome() && box.isOther());
        box.setTotalSizeInByte();
        return box;
    }

    public static FileBox getFileBoxFromPath(String path)
    {
        File selected = new File(path);
        FileBox box = new FileBox(1);
        box.putFile(selected.getName(), selected);
        if(selected.getName().endsWith(FILE_EXT) || selected.getName().endsWith(ZIP_EXT))
            box.setLome(true);
        else
            box.setOther(true);
        box.setTotalSizeInByte();

        return box;
    }

    private static FileBox GetAllFileFromDir(String dir, boolean mirror)
    {
        getAllFileFromDir(dir, mirror);


        FileBox box = new FileBox(files.size());
        for(int i=0; i<files.size(); i++)
        {
            box.putFile(names.get(i), files.get(i));
            if(files.get(i).getName().endsWith(FILE_EXT) || files.get(i).getName().endsWith(ZIP_EXT))
                box.setLome(true);
            else
                box.setOther(true);
        }

        box.setMixed((box.isLome() && box.isOther()));
        box.setTotalSizeInByte();

        files.clear();
        names.clear();

        return box;
    }

    private static void getAllFileFromDir(String dir, boolean mirror)
    {
        File [] elements = new File(dir).listFiles();
        if(elements != null) {
            for (File element : elements) {
                if (element.isDirectory() && mirror)
                    getAllFileFromDir(element.getAbsolutePath(), mirror);
                else if (!element.isDirectory()) {
                    files.add(element);
                    names.add(element.getName());
                }
            }
        }
    }

    public static void verifyPath(String path)
    {
        File parent =  new File(Objects.requireNonNull(new File(path).getParent()));
        if(!parent.exists())
            parent.mkdirs();
    }

    public static void moveFileInHideDirectory(File file, String newPath) throws IOException
    {
        String hidePath = newPath.substring(0, newPath.lastIndexOf("/"));
        if(!new File(hidePath).exists())
            new File(hidePath).mkdirs();

        moveAndOverwrite(file, new File(newPath));
        //file.delete();
    }


    private static void moveAndOverwrite(File source, File dest) throws IOException {
        // Backup the src
        FileUtils.copyFile(source, dest);
        if (!source.delete()) {
            throw new IOException("Failed to delete " + source.getName());
        }

    }

    public static void restoreFileFromHideDirectory(String destination, HiderItem hiderItem) throws IOException {
        if(!new File(new File(destination).getParent()).exists())
            new File(new File(destination).getParent()).mkdirs();

        moveAndOverwrite(new File(hiderItem.getNewFilepath()), new File(destination));
        if(new File(hiderItem.getNewParentPath()).list().length <= 0)
            new File(hiderItem.getNewParentPath()).delete();
    }

    public static ArrayList<String> mirrorSearch(String initialPath, String [] extensions)
    {
        ArrayList<String> keyFound = new ArrayList<>();
        File [] files = new File(initialPath).listFiles();
        if(files != null && files.length > 0)
        {
            for(File file : files)
            {
                if(file.isDirectory())
                {
                    analyzeFilesFromDir(file.getAbsolutePath(), extensions, keyFound);
                }else
                {
                    for(String extension : extensions)
                        if(file.getName().endsWith(extension))
                            keyFound.add(file.getAbsolutePath());
                }
            }
            return keyFound;
        }else
            return null;
    }


    public static String [] getSpecialInformations(String initialPath, String [] extensions)
    {
        Analyzers.count = 0L;
        Analyzers.paths = "";

        File [] files = new File(initialPath).listFiles();

        if(files != null && files.length > 0)
        {

            for(File file : files)
            {

                if(file.isDirectory())
                {
                    analyzeFilesFromDir(file.getAbsolutePath(), extensions);
                }else
                {
                    for(String extension : extensions)
                        if(file.getName().endsWith(extension)) {
                            Analyzers.count += 1L;
                            Analyzers.paths = Analyzers.paths.concat(file.getAbsolutePath()+"\n\n");
                            //paths = paths.concat(file.getName().concat("\n").concat(file.getParent()).concat("\n\n"));

                        }
                }
            }
            return new String[]{""+Analyzers.count, Analyzers.paths};
        }else
            return new String[]{"0", ""};

    }




    private static boolean isLomeFile(String name)
    {
        return name.endsWith(ZIP_EXT) || name.endsWith(FILE_EXT);
    }

    private static boolean isKeyFile(String name)
    {
        return name.endsWith(KEY_FILE_EXT);
    }

    public static boolean isImageOrVideoFile(File file)
    {
        String mimeType = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
        return mimeType != null && (mimeType.startsWith("video") || mimeType.startsWith("image"));
    }

    public static boolean isImage(File file)
    {
        String mimeType = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
        return mimeType != null && (mimeType.startsWith("image"));
    }

    public static boolean isVideo(File file)
    {
        String mimeType = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
        return mimeType != null && (mimeType.startsWith("video"));
    }

    public static void copyFileInGallery(File file, AppCompatActivity activity) throws IOException {
        String lomeGalleryPath = GalleryLomePath;
        //if(customSettings == null)
        initializeCustomSettings(activity.getApplicationContext());
        if(new File(lomeGalleryPath).isDirectory())
            lomeGalleryPath = lomeGalleryPath.concat(File.separator);
        else
        {
            new File(lomeGalleryPath).mkdir();
            lomeGalleryPath = lomeGalleryPath.concat(File.separator);
        }
        File mediaFile = new File(lomeGalleryPath.concat(file.getName()));
        if(customSettings.isMove_imvid())
            FileManager.moveAndOverwrite(file, mediaFile);
        else
            FileUtils.copyFile(file, mediaFile);
        DataManager.refreshGallery(mediaFile.getPath(), activity.getApplicationContext());
    }

    public static HashMap<String, Object> getDataMapFromLink(Context context, Intent intent)
    {

        initializeCustomSettings(context);
        HashMap<String, Object> dataMap = new HashMap<>();
        FileBox box;
        final ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        final ArrayList<String> paths = new ArrayList<>();
        box = new FileBox(uris.size());

        for (int i = 0; i < uris.size(); i++)
        {
            Uri uri = uris.get(i);
            String name;
            String extension;
            name = UriDataResolver.getFileNameFromUri(uri, context.getContentResolver().query(uri, null, null, null, null));
            extension = UriDataResolver.getFileExtensionFromUri(uri, context.getContentResolver());
            String filename;
            if(!(extension == null) && !extension.equals("null") && !name.contains(".".concat(extension)))
                filename = name + "." + extension;
            else
                filename = name;

            if(isLomeFile(filename) && !box.isLome())
            {
                box.setLome(true);
            }else if(!isLomeFile(filename) && !box.isOther())
                box.setOther(true);

            paths.add(customSettings.getSharePath() + File.separator + filename);
            box.putFile(filename, new File(paths.get(i)));
        }
        box.setOriginalPaths(paths);
        box.setMixed(box.isLome() && box.isOther());
        box.setTotalSizeInByte();

        dataMap.put("FileBox", box);
        dataMap.put("uris", uris);
        dataMap.put("paths", paths);

        return dataMap;
    }

    public static String readFile(String path) throws IOException {
        return FileUtils.readFileToString(new File(path), "UTF-8");
    }


    private static  String verifyName(String initialname, String path, int count)
    {
        Log.d("EXISTS ", ""+new File(path).exists());

        if(new File(path).exists()) {
            String temp_path = path.substring(0, path.lastIndexOf("/"));
            String extensions = initialname.substring(initialname.lastIndexOf("."));
            String newName = initialname.substring(0, initialname.lastIndexOf("."));
            newName = newName.concat("".concat(String.valueOf(count)));
            String newpath = temp_path.concat(File.separator).concat(newName).concat(extensions);
            count+=1;
            Log.d("EXISTS ", newpath);
            return verifyName(initialname, newpath, count);
        }else
            return path;


    }

    public static void writeTextToFile(String text, String path) throws IOException
    {
        byte [] data = text.getBytes();
        String newPath = verifyName(new File(path).getName(),path, 1);
        Log.d("NEWPATH", newPath);
        FileUtils.writeByteArrayToFile(new File(newPath), data);
    }

    public static String readTextFromFile(String path) throws IOException {
        byte [] data = FileUtils.readFileToByteArray(new File(path));
        return new String(data);
    }

    public static void deleteFile(File file)
    {
        String parent = file.getParent();

        boolean success = file.delete();
        String parentName = new File(parent).getName();

        while(!parentName.equals(GlobalValues.defaultLomePathName) && new File(parent).isDirectory() && new File(parent).list().length <= 0)
        {
            String newParent = new File(parent).getParent();
            new File(parent).delete();
            parent = newParent;
        }

    }

    public static ArrayList<String> searchEncryptedFileInStorage(String initialPath)
    {
        return FileManager.mirrorSearch(initialPath, new String[]{FILE_EXT});
    }

    public static void saveImageBitmap(Bitmap bitmap, String path) throws IOException {
        String newPath = verifyName(new File(path).getName(),path, 1);
        try (FileOutputStream out = new FileOutputStream(new File(newPath))) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
    }

    public static void saveTempImageBitmap(Bitmap bitmap, String path) throws IOException {
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(new File(path));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }

    public static void secureDeleteFile(File file, int cycle, Context context) throws IOException
    {
        if(context != null) {
            components = NotificationSender.
                    setNotificationProgressWithPendingIntentCancel(context,
                            String.valueOf(SimmetricCipherOperation.fileNumber)
                                    .concat(" ")
                                    .concat(context.getString(R.string.remains)),
                            context.getString(R.string.operationWorker));
            thisContext = context;
        }
        if(file.exists())
        {
            if(context != null)
                initializeCustomSettings(context);

            if(context != null && customSettings.isVsitr_algorithm())
            {
                deleteWithVSITRAlgorithm(file);
            }else
            {
                deleteWithRandomOverrides(file, cycle);
            }
            if(context != null && components != null)
                ((NotificationManager) components.get(0)).cancelAll();
        }
    }

    public static void secureDeleteFile(File [] files, Context context, String algorithm, CircleProgressView circleProgressView, AppCompatActivity activity) throws IOException
    {
        new ShreddingOperation(circleProgressView, algorithm, files, context, activity);
    }

    @SuppressWarnings("SameReturnValue")
    public static boolean deleteWithRandomOverrides(File file, int cycle) throws IOException {

        if(cycle > 0) {
            double progress;
            double count = 0;
            long lenght = file.length();
            long cycledLength = lenght * cycle;
            SecureRandom secrand = new SecureRandom();
            RandomAccessFile raf = new RandomAccessFile(file, "rws");
            for (int i = 0; i < cycle; i++) {
                raf.seek(0);
                raf.getFilePointer();
                long pos = 0;
                byte[] data = new byte[4096];
                while (pos < lenght) {
                    byte [] toWrite = RandNGenerator.generateRandomBytes(data, secrand);
                    raf.write(toWrite);
                    pos += data.length;
                    if(thisContext != null) {
                        count += toWrite.length;
                        progress = ((count / (double) cycledLength) * (double) 100);
                        notifyFileDeleteFileProgress(components, thisContext, progress);
                    }
                }
            }
            raf.close();
        }
        FileManager.deleteFile(file);
        return true;
    }


    public static boolean deleteWithVSITRAlgorithm(File file) throws IOException
    {
        final int VSITR_PASSES = 7;
        final byte [] ZERO_ARRAY = new byte [4096];
        final byte [] ONE_ARRAY = new byte[4096];

        Arrays.fill(ZERO_ARRAY, (byte) 0);
        Arrays.fill(ONE_ARRAY, (byte) 1);

        long lenght = file.length();
        double progress;
        double count = 0;
        byte [] toWrite;
        long cycledLength = lenght * VSITR_PASSES;

        SecureRandom secrand = new SecureRandom();
        RandomAccessFile raf = new RandomAccessFile(file, "rws");

        for(int i=0; i<VSITR_PASSES; i++)
        {
            raf.seek(0);
            raf.getFilePointer();
            long pos = 0;
            byte[] data = new byte[4096];
            switch(i)
            {
                case 0:
                case 2:
                case 4:
                    while(pos<lenght)
                    {
                        raf.write(ZERO_ARRAY);
                        pos+=data.length;
                        count += ZERO_ARRAY.length;
                        progress = ((count / (double) cycledLength) * (double) 100);
                        notifyFileDeleteFileProgress(components, thisContext, progress);
                    }
                    break;
                case 1:
                case 3:
                case 5:
                    while(pos < lenght)
                    {
                        raf.write(ONE_ARRAY);
                        pos+=data.length;
                        count += ONE_ARRAY.length;
                        progress = ((count / (double) cycledLength) * (double) 100);
                        notifyFileDeleteFileProgress(components, thisContext, progress);
                    }
                    break;
                case 6:
                    while (pos < lenght) {
                        toWrite = RandNGenerator.generateRandomBytes(data, secrand);
                        raf.write(toWrite);
                        pos += data.length;
                        count += toWrite.length;
                        progress = ((count / (double) cycledLength) * (double) 100);
                        notifyFileDeleteFileProgress(components, thisContext, progress);
                    }
            }
        }
        raf.close();
        FileManager.deleteFile(file);
        return true;
    }

    public static String relativizePath(String path, String saveDir, boolean modeEnc) throws IOException {
        //Aggiustare path file singolo, resta anche la cartella di salvataggio

        File myDir;
        File myFile = new File(path);

        if(!path.startsWith(saveDir.concat(File.separator))) {
            if (path.startsWith(GlobalValues.sdCardPath)) {
                myDir = new File(GlobalValues.sdCardPath);
            } else if (path.startsWith(GlobalValues.getGalleryPath()))
                myDir = new File(GlobalValues.getGalleryPath());
            else return saveDir.concat(File.separator).concat(myFile.getName()).concat(FILE_EXT);
        }else
            myDir = new File(saveDir.concat(File.separator));

        String relativized = myDir.toURI().relativize(myFile.toURI()).getPath();
        String relativizedParent = new File(relativized).getParent();

        String newPath = saveDir.concat(File.separator)
                .concat((modeEnc ? relativized : ((relativizedParent != null ? relativizedParent.concat(File.separator) : relativized))))
                .concat((modeEnc ? FILE_EXT : getRealFileName(myFile)));

        String parentPath = new File(newPath).getParent();

        if(new File(parentPath).exists())
            return newPath;
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.createDirectories(Paths.get(parentPath));
            }else
                new File(parentPath).mkdirs();
            return newPath;
        }
    }

    private static String getRealFileName(File file)
    {
        String filename = file.getName();
        return filename.substring(0, filename.lastIndexOf("."));
    }

}
