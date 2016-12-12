package de.jandrotek.android.aspectra.libplotspectrav3;

import android.util.Log;

import com.jjoe64.graphview.GraphView;

import de.jandrotek.android.aspectra.core.AspectraGlobals;

/**
 * Created by jan on 03.09.15.
// */
public class PlotViewPresenter {

    private static final String TAG = "PlotViewPresenter";
    private PlotViewFragment mPlotViewFragment;
    private int mItemListSizeAct = 0;// actually used

    public boolean isInitialized() {
        return mInitialized;
    }

    private static boolean mInitialized = false;

    public int getmDataLengthMax() {
        return mDataLengthMax;
    }

    private int mDataLengthMax = 0;
    private int[] mPlotDataLength;
//    private static final int PLOT_DATA_SIZE = AspectraGlobals.eMaxSpectrumSize;
    private int realPlotDataSize = 0;//PLOT_DATA_SIZE;
    private int mCallerActivity = -1;

    public PlotViewPresenter(int callerActivity, PlotViewFragment fragment) {
        mCallerActivity = callerActivity;
        mPlotViewFragment = fragment;

    }

    public void init(int plotCount, int [][] data) {
        if(plotCount <= AspectraGlobals.eMaxPlotCount) {
            mPlotViewFragment.setItemlistSize(plotCount);
            mItemListSizeAct = plotCount;
        } else {
            mPlotViewFragment.setItemlistSize(AspectraGlobals.eMaxPlotCount);
            mItemListSizeAct = AspectraGlobals.eMaxPlotCount;
        }
        if (mPlotDataLength == null) {
            mPlotDataLength = new int[AspectraGlobals.eMaxPlotCount];
        }

        for (int i = 0; i < mItemListSizeAct; i++) {// must be new
            addPlot(i, data[i]);
        }
        mInitialized = true;
    }

    public void addPlot(int index,int [] data){
        int length = data.length;
        if(length > 0) {
            mPlotDataLength[index] = length;
            GraphView.GraphViewData[] realData = createSinglePlotData(data);
            mPlotViewFragment.createPlotSerie(index, realData);
        }
    }

    public void updateSinglePlot(int index, int[] data) {
        int length = data.length;
        if(length > 0) {
            if (mPlotDataLength == null) {
                mPlotDataLength = new int[AspectraGlobals.eMaxPlotCount];
            }
            mPlotDataLength[index] = length;
            GraphView.GraphViewData[] realData = generateData(data, length);
            if (mPlotViewFragment.isFullInitialized()) {
                mPlotViewFragment.updateSinglePlot(index, realData);// in live view, here we get null exception
            }
            updateFragmentPort(0,length);
        }
    }

    public GraphView.GraphViewData[] createSinglePlotData(int[] data) {
        int length = data.length;
        GraphView.GraphViewData[] realData = generateData(data, length);
        return realData;
    }

    public void updateFragmentPort(int start, int end) {
        mPlotViewFragment.updateGraphViewPort(start, end);
    }

    private GraphView.GraphViewData[] generateData( int[] data, int length) {
        int realLength;
        GraphView.GraphViewData[] realData = new GraphView.GraphViewData[AspectraGlobals.eMaxSpectrumSize];

        try {
            if (length > AspectraGlobals.eMaxSpectrumSize) {
                realLength = AspectraGlobals.eMaxSpectrumSize;
            } else {
                realLength = length;
            }
            for (int i = 0; i < realLength; i++) {

                realData[i] = new GraphView.GraphViewData(i, data[i]);
            }
            //TODO: check in plot act length, and add needed data only for that length
            if (mItemListSizeAct > 1) {
                mDataLengthMax = findMaxDataLength();
            } else {
                mDataLengthMax = realLength;
            }
            for (int i = realLength; i < AspectraGlobals.eMaxSpectrumSize; i++) {
                realData[i] = new GraphView.GraphViewData(i, 0);
            }
        } catch (Exception exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Exception caused by generateData()", exception);
            }
        }
        return realData;
    }

    private int findMaxDataLength() {
        int max = 0;
        int i;
        for (i = 0; i < mItemListSizeAct; i++) {
            if (mPlotDataLength[i] > max) {
                max = mPlotDataLength[i];
            }
        }
        return max;
    }

    public void clearAllSeries(){
        mPlotViewFragment.clearPlotSeries();
    }
}
