package me.lo.lomefree.Utils.Files.FDManager;

import java.io.File;
import java.util.ArrayList;

import me.lo.lomefree.Interfaces.Extensions;

class Analyzers implements Extensions
{
    public static Long count = 0L;
    public static String paths = "";

    public static void analyzeFilesFromDir(String path, String [] extensions, ArrayList<String> keyFound)
    {
        File[] files = new File(path).listFiles();
        if(files != null) {
            for (File file : files) {
                if (file.isDirectory())
                    analyzeFilesFromDir(file.getAbsolutePath(), extensions, keyFound);
                else
                    for (String extension : extensions)
                        if (file.getName().endsWith(extension))
                            keyFound.add(file.getAbsolutePath());
            }
        }
    }


    public static void analyzeFilesFromDir(String path, String [] extensions)
    {
        File [] files = new File(path).listFiles();

        if(files != null) {
            for (File file : files) {
                if (file.isDirectory())
                    analyzeFilesFromDir(file.getAbsolutePath(), extensions);
                else
                    for (String extension : extensions)
                        if (file.getName().endsWith(extension)) {
                            count += 1L;
                            paths = paths.concat(file.getAbsolutePath() + "\n\n");
                            //paths = paths.concat(file.getName().concat("\n").concat(file.getParent()).concat("\n\n"));
                        }

            }
        }
    }
}
