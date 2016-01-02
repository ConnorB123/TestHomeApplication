package com.beeselmane.testapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends Activity
{
    private static boolean showAllBundles = false;
    private static boolean darkItems = false;

    private List<AppPackage> apps = null;
    private ListView appListView = null;
    private HomeActivity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_applist);
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        this.reloadAppList();
        this.setupAppListView();
        this.addClickListener();
        this.setTitle("Applications");

        System.out.println("onCreate()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void addClickListener()
    {
        this.appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                AppPackage application = apps.get(position);
                Intent intent = self.getPackageManager().getLaunchIntentForPackage(application.name.toString());
                self.startActivity(intent);
                self.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    private void setupAppListView()
    {
        ArrayAdapter<AppPackage> adapter = new ArrayAdapter<AppPackage>(this, R.layout.list_item, this.apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                if (convertView == null) convertView = self.getLayoutInflater().inflate(R.layout.list_item, null);
                AppPackage representedApplication = apps.get(position);

                if (HomeActivity.darkItems) convertView.setBackgroundColor(0x2F000000);
                else convertView.setBackgroundColor(Color.TRANSPARENT);

                ImageView iconView = (ImageView)convertView.findViewById(R.id.item_app_icon);
                iconView.setImageDrawable(representedApplication.icon);
                TextView labelView = (TextView)convertView.findViewById(R.id.item_app_label);
                labelView.setText(representedApplication.label);
                TextView nameView = (TextView)convertView.findViewById(R.id.item_app_name);
                nameView.setText(representedApplication.name);

                return convertView;
            }
        };

        this.appListView.setAdapter(adapter);
    }

    private void reloadAppList()
    {
        this.appListView = (ListView)this.findViewById(R.id.apps_list);
        PackageManager packageManager = this.getPackageManager();
        this.apps = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        if (!HomeActivity.showAllBundles) intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo info : availableActivities)
        {
            AppPackage application = new AppPackage(packageManager, info);
            this.apps.add(application);
        }

        Collections.sort(this.apps, new Comparator<AppPackage>() {
            @Override
            public int compare(AppPackage lhs, AppPackage rhs) {
                return lhs.label.toString().compareTo(rhs.label.toString());
            }
        });

        if (HomeActivity.darkItems) this.appListView.setDivider(new ColorDrawable(0x333333));
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}
