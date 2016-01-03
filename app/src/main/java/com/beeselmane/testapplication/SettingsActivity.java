package com.beeselmane.testapplication;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;

public class SettingsActivity extends AppCompatPreferenceActivity
{
    public static class LauncherPreferenceFragment extends PreferenceFragment
    {
        private static final int kImagePickActivityRequestCode = 1;
        private LauncherPreferenceFragment self = this;

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            this.addPreferencesFromResource(R.xml.preferences);
            Preference setWallpaperButton = this.findPreference(this.getString(R.string.key_set_wallpaper));

            setWallpaperButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    self.setWallpaperClicked();
                    return false;
                }
            });
        }

        public void setWallpaperClicked()
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), kImagePickActivityRequestCode);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (requestCode == kImagePickActivityRequestCode)
            {
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(this.getContext().getApplicationContext());
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    WindowManager windowManager = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);
                    windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                    Uri uri = data.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), uri);
                        wallpaperManager.setBitmap(bitmap);
                        wallpaperManager.suggestDesiredDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
                        Toast.makeText(this.getContext(), "Successfully changed wallpaper!", Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        Toast.makeText(this.getContext(), "Error setting wallpaper!", Toast.LENGTH_SHORT).show();
                        ex.printStackTrace(System.err);
                    }
                } else {
                    Toast.makeText(this.getContext(), "Set Wallpaper Cancelled.", Toast.LENGTH_SHORT).show();
                }
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.getFragmentManager().beginTransaction().replace(android.R.id.content, new LauncherPreferenceFragment()).commit();
    }
}
