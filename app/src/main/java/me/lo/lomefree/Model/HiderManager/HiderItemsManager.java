package me.lo.lomefree.Model.HiderManager;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.lo.lomefree.Globals.GlobalValues;
import me.lo.lomefree.Interfaces.DBNames;
import me.lo.lomefree.R;
import me.lo.lomefree.Utils.Files.Entities.HiderItem;
import me.lo.lomefree.Utils.Files.FDManager.DataManager;
import me.lo.lomefree.Utils.Files.FDManager.FileManager;

public class HiderItemsManager implements DBNames
{
    public static List<HiderItem> getHiderItemsFromDB()
    {
        List<HiderItem> hiderItems = (List<HiderItem>) DataManager.readObject(GlobalValues.hiderPath.concat(File.separator.concat(HIDER_ITEM_DB_NAMES)));
        hiderItems = (hiderItems == null ? new ArrayList<HiderItem>() : hiderItems);
        if (hiderItems.size() > 0)
        {
            List<HiderItem>hiderItems2 = new ArrayList<>();

            for(HiderItem instance : hiderItems)
            {
                if(new File(instance.getNewFilepath()).exists())
                    hiderItems2.add(instance);
            }
            hiderItems = hiderItems2;
            saveSuccess(hiderItems);


            for (HiderItem hiderItem : hiderItems) {
                File instance = new File(hiderItem.getNewFilepath());
                try {
                    if (FileManager.isImage(instance))
                        hiderItem.setThumbnail_bitmap(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(hiderItem.getNewFilepath()), 256, 256));
                    else if (FileManager.isVideo(instance)) {
                        hiderItem.setThumbnail_bitmap(ThumbnailUtils.createVideoThumbnail(hiderItem.getNewFilepath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
                    }
                } catch (Exception ex) {
                    Log.d("ECCEZZIONE BITMAP", Arrays.toString(ex.getStackTrace()));
                    hiderItem.setThumbnail_bitmap(null);
                    hiderItem.setThumbnail_int(R.drawable.media);
                }
            }
        }
        return hiderItems;
    }

    public static HiderItem addMediaItems(String path, Context context) throws IOException {

        HiderItem instance = getHiderItem(path);
        File instanceFile = new File(path);

        if(FileManager.isImage(instanceFile)) {
            instance.setCategory(context.getString(R.string.image));
            try {
                instance.setExtension(instanceFile.getName().substring(instanceFile.getName().lastIndexOf(".") + 1));
            }catch(Exception ex)
            {
                instance.setExtension("None");
            }
            instance.setThumbnail_bitmap(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 256, 256));
        }
        else if(FileManager.isVideo(instanceFile)) {
            instance.setCategory(context.getString(R.string.video));
            try {
                instance.setExtension(instanceFile.getName().substring(instanceFile.getName().lastIndexOf(".") + 1));
            }catch(Exception ex)
            {
                instance.setExtension("None");
            }
            instance.setThumbnail_bitmap(ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
        }

        if(instance.getThumbnail_bitmap() == null)
            instance.setThumbnail_int(R.drawable.media);
        instance.setSize(FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(instanceFile)));
        FileManager.moveFileInHideDirectory(instanceFile, instance.getNewFilepath());
        return instance;

    }

    private static String getNewHidePath(String filePath, HiderItem instance)
    {
        return filePath.concat(File.separator)
                .concat(instance.getOldParent())
                .concat(File.separator
                        .concat(instance.getName()
                                .concat(instance.getOldPath()
                                        .substring(instance.getOldPath()
                                                .lastIndexOf(".")))));
    }

    private static HiderItem getHiderItem(String path)
    {
        HiderItem instance = new HiderItem();
        File instanceFile = new File(path);
        String filePath = GlobalValues.hiderPath;

        instance.setName(instanceFile.getName().substring(0, instanceFile.getName().lastIndexOf(".")));
        instance.setOldPath(instanceFile.getAbsolutePath());
        instance.setOldParent(new File(instanceFile.getParent()).getName());
        instance.setNewFilepath(getNewHidePath(filePath, instance));
        instance.setNewParentPath(filePath.concat(File.separator.concat(instance.getOldParent())));
        return instance;
    }

    public static HiderItem addFileItems(String path, Context context) throws IOException
    {
        HiderItem instance = getHiderItem(path);
        File instanceFile = new File(path);

        instance.setThumbnail_int(R.drawable.filehideitem);
        instance.setCategory(context.getString(R.string.file_category));
        try
        {
            instance.setExtension(instanceFile.getName().substring(instanceFile.getName().lastIndexOf(".") + 1));
        }catch(Exception ex)
        {
            instance.setExtension("None");
        }

        instance.setSize(FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(instanceFile)));
        FileManager.moveFileInHideDirectory(instanceFile, instance.getNewFilepath());
        return instance;

    }

    public static void saveSuccess(List<HiderItem> hiderItems) {
        DataManager.saveObject(GlobalValues.hiderPath.concat(File.separator.concat(HIDER_ITEM_DB_NAMES)), hiderItems);
    }
}
