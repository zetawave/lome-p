package me.lo.lomefree.Utils.DiskUtils;

import android.os.StatFs;

import me.lo.lomefree.Globals.GlobalValues;

public class SpaceUsage
{

// --Commented out by Inspection START (18/07/18 19.11):
//    public static long totalMemory(String path)
//    {
//        StatFs statFs = new StatFs(path);
//        return (statFs.getBlockCountLong() * statFs.getBlockSizeLong());
//    }
// --Commented out by Inspection STOP (18/07/18 19.11)

    private static long freeMemory(String path)
    {
        StatFs statFs = new StatFs(path);
        return (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());
    }

    private static long busyMemory(String path)
    {
        StatFs statFs = new StatFs(path);
        long   total  = (statFs.getBlockCountLong() * statFs.getBlockSizeLong());
        long   free   = (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());
        return total - free;
    }

    public static long getTotalFreeMemory()
    {
        return (freeMemory(GlobalValues.sdCardPath)+freeMemory(GlobalValues.rootDirectory));
    }

    public static long getTotalUsedMemory()
    {
        return (busyMemory(GlobalValues.sdCardPath)+busyMemory(GlobalValues.rootDirectory));
    }

}
