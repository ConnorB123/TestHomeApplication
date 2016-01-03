package com.beeselmane.testapplication;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends Activity
{
    private static boolean showAllBundles = false;

    private List<AppPackage> apps = null;
    private ListView appListView = null;
    private HomeActivity self = this;

    private SharedPreferences preferences = null;
    private boolean useDarkItems = false;
    private int PICK_IMAGE_REQUEST = 1;

    public void reload()
    {
        if (preferences == null) preferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        this.useDarkItems = preferences.getBoolean("UseDarkBackground", false);

        this.reloadAppList();
        this.setupAppListView();
        this.addClickListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_applist);
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        this.reload();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        this.reload();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        this.reload();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getMenuInflater().inflate(R.menu.homebar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
            return true;
        } else if (id == R.id.action_wallpaper) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this.getApplicationContext());
            DisplayMetrics displayMetrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                wallpaperManager.setBitmap(bitmap);
                wallpaperManager.suggestDesiredDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
                Toast.makeText(this, "Successfully changed wallpaper!", Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                Toast.makeText(this, "Error setting wallpaper!", Toast.LENGTH_SHORT).show();
                ex.printStackTrace(System.err);
            }
        }
    }

    private void addClickListener()
    {
        this.appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) convertView = self.getLayoutInflater().inflate(R.layout.list_item, null);
                AppPackage representedApplication = apps.get(position);

                if (self.useDarkItems) convertView.setBackgroundColor(0x2F000000);
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

        if (this.useDarkItems) this.appListView.setDivider(new ColorDrawable(0x333333));
    }
}
