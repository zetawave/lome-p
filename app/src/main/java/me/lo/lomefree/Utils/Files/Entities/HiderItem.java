package me.lo.lomefree.Utils.Files.Entities;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.Serializable;

import me.lo.lomefree.R;

public class HiderItem implements Serializable
{
    private String oldPath;
    private String oldParent;
    private String newParentPath;
    private String name;
    private String newFilepath;
    private String size;



    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    private int thumbnail_int;
    private transient Bitmap thumbnail_bitmap;
    static final long serialVersionUID = 987639234389L;
    private String category;
    private String extension;
    public final static int IMAGE = 0;
    public static final int VIDEO = 1;
    public static final int FILES = 2;
    public static final int ALL = 3;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getNewFilepath() {
        return newFilepath;
    }

    public void setNewFilepath(String newFilepath) {
        this.newFilepath = newFilepath;
    }

// --Commented out by Inspection START (18/07/18 19.11):
//    public int getThumbnail_int() {
//        return thumbnail_int;
//    }
// --Commented out by Inspection STOP (18/07/18 19.11)

    public void setThumbnail_int(int thumbnail_int) {
        this.thumbnail_int = thumbnail_int;
    }

    public Bitmap getThumbnail_bitmap() {
        return thumbnail_bitmap;
    }

    public void setThumbnail_bitmap(Bitmap thumbnail_bitmap) {
        this.thumbnail_bitmap = thumbnail_bitmap;
    }

    public String getOldPath() {
        return oldPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public String getOldParent() {
        return oldParent;
    }

    public void setOldParent(String oldParent) {
        this.oldParent = oldParent;
    }

    public String getNewParentPath() {
        return newParentPath;
    }

    public void setNewParentPath(String newParentPath) {
        this.newParentPath = newParentPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isResources()
    {
        return (thumbnail_bitmap == null);
    }

    public boolean isVideoOrImage(Context context)
    {
        return (getCategory().equals(context.getString(R.string.video)) || getCategory().equals(context.getString(R.string.image)));
    }

    public boolean isImage(Context context){
        return (getCategory().equals(context.getString(R.string.image)));
    }

    public boolean isVideo(Context context){
        return (getCategory().equals(context.getString(R.string.video)));
    }

}
