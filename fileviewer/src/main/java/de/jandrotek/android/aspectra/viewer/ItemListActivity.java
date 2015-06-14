package de.jandrotek.android.aspectra.viewer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import de.jandrotek.android.aspectra.libprefs.AspectraSettings;
import de.jandrotek.android.aspectra.libspectrafiles.SpectrumFiles;
//import de.jandrotek.android.aspectra.common.SettingsActivity;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details
 * (if present) is a {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ItemListFragment.Callbacks} interface
 * to listen for item selections.
 */
//public class ItemListActivity extends BaseActivity
public class ItemListActivity extends ActionBarActivity
        implements ItemListFragment.Callbacks {

    private static final String TAG = "ListItemsAct";
    private AspectraSettings mAspectraSettings;
    private String mFileFolder;
    private String mFileExt;

    private SpectrumFiles mSpectrumFiles = null;
    private int mFileListSize = 0;
    private String[] mFiles;
    private int mChartLength;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "onCreate() called");
        }

        mAspectraSettings = new AspectraSettings();
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        mAspectraSettings.connectPrefs(context, prefs);

        updateFromPreferences();

        if(mSpectrumFiles == null) {
            mSpectrumFiles = new SpectrumFiles();
            mFileListSize = mSpectrumFiles.scanFolderForFiles(mFileFolder,mFileExt );
        }

        setContentView(R.layout.activity_item_list);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ItemListFragment) getFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);
        }

    }

//    private int scanFolderForFiles(String fileFolder, String fileExtension) {
//        mSpectrumFiles.setFileFolder(fileFolder);
//        mSpectrumFiles.setFileExt(fileExtension);
//        mSpectrumFiles.searchForFiles();
//         return(mSpectrumFiles.getFileListSize());
//    }

//    //@Override
//    protected void updateFromPreferences(){
//        super.updateFromPreferences();
//       	mChartLength = mAspectraSettings.getPrefsSpectraLength();
//    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFromPreferences();
        mFileListSize = mSpectrumFiles.scanFolderForFiles(mFileFolder,mFileExt );
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.global, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ItemDetailActivity.class);
            detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    protected void updateFromPreferences() {
        mAspectraSettings.loadSettings();
        mFileFolder = mAspectraSettings.getPrefsSpectraBasePath();
        mFileExt = mAspectraSettings.getPrefsSpectraExt();
    }

}