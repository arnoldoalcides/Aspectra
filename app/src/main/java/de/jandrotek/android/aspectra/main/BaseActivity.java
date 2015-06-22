package de.jandrotek.android.aspectra.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import de.jandrotek.android.aspectra.libprefs.AspectraSettings;
import de.jandrotek.android.aspectra.libprefs.SettingsActivity;
import de.jandrotek.android.aspectra.libspectrafiles.SpectrumFiles;

public class BaseActivity extends AppCompatActivity //ActionBarActivity
{

//    protected static final int ACT_ITEM_LIVE_VIEW   = 0;
//    protected static final int ACT_ITEM_VIEW_CONFIG = 1;
//    protected static final int ACT_ITEM_VIEW_PLOT   = 2;
//    protected static final int ACT_ITEM_ANALYZE     = 3;

    protected AspectraSettings mAspectraSettings;
    protected String mFileFolder;
    protected String mFileExt;
    protected SpectrumFiles mSpectrumFiles;

    protected boolean mCameraPresent = false;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       mAspectraSettings = new AspectraSettings();
       mSpectrumFiles = new SpectrumFiles();
       Context context = getApplicationContext();
       SharedPreferences prefs = PreferenceManager
               .getDefaultSharedPreferences(context);
       mAspectraSettings.connectPrefs(context, prefs);

       updateFromPreferences();

   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void updateFromPreferences() {
        mAspectraSettings.loadSettings();
        // used by all activities
        mFileFolder = mAspectraSettings.getPrefsSpectraBasePath();
        mFileExt = mAspectraSettings.getPrefsSpectraExt();
        // the rest is updated local
    }

}

