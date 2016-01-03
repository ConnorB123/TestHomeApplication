package com.beeselmane.testapplication;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class AppPackage
{
    public boolean canOpenAsApplication = false;
    public boolean isSystemApplication = false;
    public CharSequence label = null;
    public String publicName = null;
    public CharSequence name = null;
    public Drawable icon = null;

    public AppPackage(PackageManager manager, ResolveInfo info)
    {
        this.label = info.loadLabel(manager);
        this.name = info.activityInfo.packageName;
        this.icon = info.activityInfo.loadIcon(manager);
        this.publicName = info.activityInfo.name;
    }
}
