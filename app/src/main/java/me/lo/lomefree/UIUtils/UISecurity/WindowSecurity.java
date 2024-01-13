package me.lo.lomefree.UIUtils.UISecurity;

import android.view.Window;
import android.view.WindowManager;

import me.lo.lomefree.SettingsUtils.CustomSettings;

public class WindowSecurity
{
    private static void disableScreenshots(Window window)
    {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    public static void initSecurityFlags(CustomSettings customSettings, Window window)
    {
        if(!customSettings.isScreen_capture())
            WindowSecurity.disableScreenshots(window);
    }
}
