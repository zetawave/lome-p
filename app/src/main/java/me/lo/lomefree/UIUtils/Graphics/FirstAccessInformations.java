package me.lo.lomefree.UIUtils.Graphics;

import android.content.Context;
import java.util.HashMap;
import me.lo.lomefree.Interfaces.FirstAccessViews;
import me.lo.lomefree.R;

public class FirstAccessInformations implements FirstAccessViews
{
    public static HashMap<Integer, Integer> IMAGES;
    public static HashMap<Integer, String[]> TEXTS_AND_TITLES;

    public static void initializeFirstAccessInformations(Context thisContext)
    {
        IMAGES = new HashMap<>();
        TEXTS_AND_TITLES = new HashMap<>();

        IMAGES.put(KEY_SEARCH, R.drawable.searchkey);
        TEXTS_AND_TITLES.put(KEY_SEARCH, new String[]{thisContext.getString(R.string.key_search), thisContext.getString(R.string.key_search_first_informations)});

        IMAGES.put(APP_VERSION, R.drawable.currentlogo);
        TEXTS_AND_TITLES.put(APP_VERSION, new String[]{thisContext.getString(R.string.newVersionTitle), thisContext.getString(R.string.newVersionDescription)});
    }

}
