package de.jandrotek.android.aspectra.analyze;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import de.jandrotek.android.aspectra.core.SpectrumBase;
import de.jandrotek.android.aspectra.libplotspectrav3.PlotViewFragment;
import de.jandrotek.android.aspectra.libtouch.TouchView;

public class AnalyzeActivity extends AppCompatActivity
        implements TouchView.OnTouchViewInteractionListener
{
    public static final String ARG_ITEM_REFERENCE = "item_reference";
    public static final String ARG_ITEM_EDIT = "item_edit";

    //    private AnalyzeFragment mAnalyzeFragment;
    private PlotViewFragment mViewFragment;
    private AnalyzeViewController mViewController;
    private Fragment mContent;
    private TouchView mTouchView;

    private String mSpectrumNameToEdit;
    //    private String mSpectrumAbsNameToEdit;
    private String mSpectrumNameReference;
    //    private String mSpectrumNameAbsReference;
//    private int mSpectrumToEditLength;
//    private int mSpectrumReferenceLength;
//    private int[] mSpectrumToEditValues = null;
//    private int[] mSpectrumReferenceValues = null;
    private int mSpectrumLengthMax;
    private SpectrumBase mSpectrumToEdit;
    private SpectrumBase mSpectrumToEditBackup;
    private boolean mSpectrumAlreadyEdited = false;
    //    private SpectrumBase mSpectrumReference;
    private Map<String, String> mSpectraMap;
    public static boolean mCalcBusy = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        if (savedInstanceState == null) {
            mSpectraMap = new HashMap<>();

            // spectrum reference
            if(getIntent().getExtras().containsKey(ARG_ITEM_REFERENCE)){
                mSpectrumNameReference = getIntent().getExtras().getString(ARG_ITEM_REFERENCE);
                mSpectraMap.put(ARG_ITEM_REFERENCE, mSpectrumNameReference);
            }

            // spectrum to edit
            if(getIntent().getExtras().containsKey(ARG_ITEM_EDIT)){
                mSpectrumNameToEdit = getIntent().getExtras().getString(ARG_ITEM_EDIT);
                mSpectraMap.put(ARG_ITEM_EDIT, mSpectrumNameToEdit);
            }

            mViewFragment = PlotViewFragment.newInstance(2);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_analyze_plot_container, mViewFragment)
                    .commit();
            mViewController = new AnalyzeViewController();
            mViewController.init(mViewFragment, mSpectrumNameToEdit, mSpectrumNameReference);

            mTouchView = (TouchView)findViewById(R.id.analyze_touchview_overlay);
        }

//        mViewController.generateGraphViewData();
//        mViewController.updateSpectraView(mSpectrumLengthMax);
        mCalcBusy = false;
    }



    @Override
    public void onResume(){
        int length;
        super.onResume();
        mViewController.initDisplayInFragment();
        length = mViewController.generateGraphViewData();
        mSpectrumLengthMax = length;
        mViewController.updateSpectraView(mSpectrumLengthMax);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "mContent", mViewFragment);


    }

    @Override
    public void onRestoreInstanceState(Bundle inState){
        mViewFragment = (PlotViewFragment) getSupportFragmentManager().getFragment(inState, "mContent");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_analyze_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO: interaction with TouchView
    public void onTouchViewInteraction(int _toolId, float _value){
        if(mSpectrumAlreadyEdited != true){
            mSpectrumAlreadyEdited = true;
            mSpectrumToEditBackup = mSpectrumToEdit;
        }
        if(mCalcBusy != true) {
            if (_toolId == TouchView.ePlotAction_Move) {
                mCalcBusy = true;
                new CalcTask(_toolId,_value, 0f).execute();
//                while(mCalcBusy)// add timeout
//                    ;
//                updateEditedSpectrumInFragment();
            }
        }
    }


//    private void updateEditedSpectrumInFragment() {
//        mSpectrumToEditLength = mSpectrumToEdit.getDataSize();
//        mSpectrumLengthMax = Math.max(mSpectrumToEditLength, mSpectrumReferenceLength);
//        mSpectrumToEditValues = mSpectrumToEdit.getValues();
//        mViewController.updateSpectraView(mSpectrumLengthMax);
//        mAnalyzeFragment.updateEditedPlot();
//    }

    public class CalcTask extends AsyncTask<Void, Void, Void> {
        private final int action;
        private final float factor;
        private final float staticPoint;

        CalcTask(int action, float factor, float staticPoint){
            this.action = action;
            this.factor = factor;
            this.staticPoint = staticPoint;
        }

        @Override
        protected Void doInBackground(Void... params) {
//            mCalcBusy = true;
            if (action == TouchView.ePlotAction_Move) {
                if(factor < 0){ // move left
                    int startIndex = mSpectrumToEdit.getStartIndex();
                    // cheap and dirty handling, moving left, cut the data
                    // moving left proper handling needs modify both spectra, edit and ref
                    // and after moving right, again modify both

//                    if(startIndex < -factor) { // additinal we must append left reference
//                        mSpectrumReference.moveData((int) factor + startIndex );
//                        mSpectrumToEdit.moveData((int) startIndex);
//                    } else { // startIndex bigger as move
                        mSpectrumToEdit.moveData((int) factor);
//                    }
                } else { // move right
                    mSpectrumToEdit.moveData((int) factor);
                }
            }
            return(null);
        }

        @Override
        protected void onPostExecute(Void arg0) {
            mViewController.updateEditedSpectrumInFragment();
            mCalcBusy = false;
        }

    }
}
