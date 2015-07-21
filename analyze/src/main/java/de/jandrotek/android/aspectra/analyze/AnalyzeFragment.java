package de.jandrotek.android.aspectra.analyze;

import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.SpinnerAdapter;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.HashMap;
import java.util.Map;

import de.jandrotek.android.aspectra.core.AspectraGlobals;
import de.jandrotek.android.aspectra.core.SpectrumAsp;
import de.jandrotek.android.aspectra.core.SpectrumChr;
import de.jandrotek.android.aspectra.libspectrafiles.SpectrumFiles;

/**
 * A placeholder fragment containing a simple view.
 */
public class AnalyzeFragment extends Fragment {

    private static final int ITEM_SELECTED_STRETCH = 0;
    private static final int ITEM_SELECTED_CROP = 1;
    private static final int ITEM_SELECTED_EDIT_NOTES = 2;
    private static final int ITEM_SELECTED_MARKERS = 3;
    private static final int ITEM_SELECTED_SET_REFERENCE = 4;


    public void setSpectrumLengthMax(int mSpectrumLengthMax) {
        this.mSpectrumLengthMax = mSpectrumLengthMax;
    }

    private int mSpectrumLengthMax;

    public void setSpectrumToEditValues(int[] mSpectrumToEditValues) {
        this.mSpectrumToEditValues = mSpectrumToEditValues;
    }

    public void setSpectrumReferenceValues(int[] mSpectrumReferenceValues) {
        this.mSpectrumReferenceValues = mSpectrumReferenceValues;
    }

    private int[] mSpectrumToEditValues = null;
    private int[] mSpectrumReferenceValues = null;
    private int mColorEdit = Color.rgb(255, 0, 0);
    private int mColorRef = Color.rgb(0, 0, 255);
    private GraphView.GraphViewData[] realDataReference;
    private GraphView.GraphViewData[] realDataToEdit;
    private static Map<String, String> mStaticSpectra;
    private GraphViewSeries mDataSeriesEdit;
    private GraphViewSeries mDataSeriesRef;

    private LineGraphView mGraphView;

    private ActionBar.OnNavigationListener mOnNavigationListener;

    public static AnalyzeFragment newInstance() {
        AnalyzeFragment fragment = new AnalyzeFragment();
        return fragment;
    }

    public AnalyzeFragment() {
        mStaticSpectra = new HashMap<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realDataToEdit = new GraphView.GraphViewData[mSpectrumLengthMax];
        realDataReference = new GraphView.GraphViewData[mSpectrumLengthMax];
    }

    public void updateEditedPlot(){
        realDataToEdit = generateData(mSpectrumToEditValues, mSpectrumLengthMax);
        if(mDataSeriesEdit == null){
            GraphViewSeries mDataSeriesEdit = new GraphViewSeries(
                    "",
                    new GraphViewSeries.GraphViewSeriesStyle(mColorEdit, 1),
                    realDataToEdit);
            mGraphView.addSeries(mDataSeriesEdit);
//            mDataSeriesEdit.resetData(realDataToEdit);

        } else {
            mDataSeriesEdit.resetData(realDataToEdit);
        }
        mGraphView.setViewPort(0, mSpectrumLengthMax);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(de.jandrotek.android.aspectra.libplotspectrav3.R.layout.fragment_plot_view, container, false);

        ActionBar actionbar = ((AnalyzeActivity)getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.analyze_actionbar_list,
                android.R.layout.simple_spinner_dropdown_item);

        mGraphView = new LineGraphView(getActivity(), "");

        if(mSpectrumToEditValues != null) {
            realDataToEdit = generateData(mSpectrumToEditValues, mSpectrumLengthMax);
            GraphViewSeries mDataSeriesEdit = new GraphViewSeries(
                    "",
                    new GraphViewSeries.GraphViewSeriesStyle(mColorEdit, 1),
                    realDataToEdit);
            mGraphView.addSeries(mDataSeriesEdit);
        }

        if(mSpectrumReferenceValues != null) {
            realDataReference = generateData(mSpectrumReferenceValues, mSpectrumLengthMax);
            GraphViewSeries mDataSeriesRef = new GraphViewSeries(
                    "",
                    new GraphViewSeries.GraphViewSeriesStyle(mColorRef, 1),
                    realDataReference);
            mGraphView.addSeries(mDataSeriesRef);
        }

        mGraphView.getGraphViewStyle().setTextSize(20);
        mGraphView.getGraphViewStyle().setNumHorizontalLabels(5);
        mGraphView.getGraphViewStyle().setNumVerticalLabels(4);

        mGraphView.setViewPort(0, mSpectrumLengthMax);
        registerForContextMenu(mGraphView);

        //TODO: optional - activate scaling / zooming
        // both modi will be handled with Touch-view helper class, not only in viewer
        // in liveView is disabled, first in AnalyzeActivity
        FrameLayout mFrameLayout = (FrameLayout)rootView.findViewById(de.jandrotek.android.aspectra.libplotspectrav3.R.id.flPlotView);
        mFrameLayout.addView(mGraphView);
        setRetainInstance(true);
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mOnNavigationListener = new ActionBar.OnNavigationListener() {
            String[] strings = getResources().getStringArray(R.array.analyze_actionbar_list);

            @Override
            public boolean onNavigationItemSelected(int position, long itemID){
//                mSelectedChildFragmentID = position;
//                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//                // check if one child already exists, not yet
//                if((mAsciiViewer == null) && (mTiltViewer == null) && (mLRViewer == null)){
////					if (position == SELECTED_LEFTRIGHT_CHILD){
////						mLRViewer = new LeftRightViewFragment();
////						transaction.add(R.id.child_fragment, mLRViewer).commit();
////					}
//                    if(position == SELECTED_ASCIIDATA_CHILD){
//                        mAsciiViewer = new AsciiViewFragment();
//                        transaction.add(R.id.child_fragment, mAsciiViewer).commit();
//                    }
//                    else if(position == SELECTED_TILTVIEW_CHILD){ // if child == tiltView
//                        mTiltViewer = new TiltViewFragment();
//                        transaction.add(R.id.child_fragment, mTiltViewer).commit();
//                    }
//                } else { // one child alredy exists
////					if (position == SELECTED_LEFTRIGHT_CHILD){
////						if(mLRViewer == null){
////							mLRViewer = new LeftRightViewFragment();
////												}
////						transaction.replace(R.id.child_fragment, mLRViewer).commit();
////					}
//                    if(position == SELECTED_ASCIIDATA_CHILD){
//                        if(mAsciiViewer == null){
//                            mAsciiViewer = new AsciiViewFragment();
//                        }
//                        transaction.replace(R.id.child_fragment, mAsciiViewer).commit();
//                    }
//                    else if(position == SELECTED_TILTVIEW_CHILD){ // if
//                        if(mTiltViewer == null){
//                            mTiltViewer = new TiltViewFragment();
//                        }
//                        transaction.replace(R.id.child_fragment, mTiltViewer).commit();
//                    }
//                }
                return true;
            }
        };
        actionbar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
        return rootView;
    }
    private GraphView.GraphViewData[] generateData(int[] data, int length) {
        GraphView.GraphViewData[]    realData = new GraphView.GraphViewData[length];
        for (int i=0; i<length; i++) {

            realData[i] = new GraphView.GraphViewData(i, data[i]);
        }
        //TODO: check in plot act length, and add needed data only for that length

        for(int i = length; i < mSpectrumLengthMax ; i++){
            realData[i] = new GraphView.GraphViewData(i, 0);
        }
        return realData;
    }

}
