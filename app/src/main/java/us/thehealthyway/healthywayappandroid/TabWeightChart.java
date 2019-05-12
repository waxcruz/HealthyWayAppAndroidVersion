package us.thehealthyway.healthywayappandroid;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static us.thehealthyway.healthywayappandroid.Helpers.doubleFromObject;
import static us.thehealthyway.healthywayappandroid.AppData.DEBUG;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabWeightChart.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabWeightChart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabWeightChart extends Fragment {
    private static final String TAG = "HW.TabWeightChart";

    // Model
    private Model model;
    // view fields
    TextView client_message;
    private LineChart weightChart;









    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TabWeightChart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabWeightChart.
     */
    // TODO: Rename and change types and number of parameters
    public static TabWeightChart newInstance(String param1, String param2) {
        TabWeightChart fragment = new TabWeightChart();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // find Model Controller
        model = Model.getInstance();
        // find view fields
        View view;
        view = inflater.inflate(R.layout.fragment_weight_chart_tab, container, false);
        client_message = (TextView) view.findViewById(R.id.client_message);
        weightChart = (LineChart) view.findViewById(R.id.weight_chart);
        Map<String, Object> userDataNode = model.getSignedinUserDataNode();
        lineChartUdate(userDataNode);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(HW_Enumerations.TabNames.TAB_WEIGHT_GRAPH, HW_Enumerations.SubTabNames.SUB_TAB_LEVEL_0);
        }
    }


    // Charting
    void lineChartUdate(Map<String, Object> node) {
        Map<String, Object> nodeJournal;
        ArrayList<String> lastSevenDays = new ArrayList<>();
        if (DEBUG) {
            Log.d(TAG, "lineChartUdate: display weight chart");
        }
        client_message.setText("");
        if (node == null){
            return;
        }
        if (node.containsKey(KeysForFirebase.NODE_JOURNAL)){
            nodeJournal = (Map<String, Object>) node.get(KeysForFirebase.NODE_JOURNAL);
        } else {
            client_message.setText("No data");
            return;
        }
        Map<String, Object> nodeDate;
        double weight = 0f;
        Set<String> datesOfConsumpation = nodeJournal.keySet();
        ArrayList<String> sortedKeyDates = new ArrayList<String>(datesOfConsumpation);
        Collections.sort(sortedKeyDates);
        // only 7 days of data for weight chart
        Collections.reverse(sortedKeyDates);
        lastSevenDays.clear();
        for (String logDate : sortedKeyDates) {
            if (nodeJournal.containsKey(logDate)) {
                nodeDate = (Map<String, Object>) nodeJournal.get(logDate);
                if (nodeDate.containsKey(KeysForFirebase.WEIGHED)) {
                    weight = doubleFromObject((Object) nodeDate.get(KeysForFirebase.WEIGHED));
                    if (weight > 0.0) {
                        lastSevenDays.add(logDate);
                        if (lastSevenDays.size() == 7) {
                            break; // only need last seven records
                        }
                    }
                }
            }
        }
        Collections.reverse(lastSevenDays);
        String startDate = "";
        Double startWeight = 0.0;
        Map<String, Double> chartDataPoint = new HashMap<String, Double>();
        for (String weightDate : lastSevenDays) {
            if (nodeJournal.containsKey(weightDate)) {
                nodeDate = (Map<String, Object>) nodeJournal.get(weightDate);
            } else {
                nodeDate = new HashMap<String, Object>();
            }
            if (nodeDate.containsKey(KeysForFirebase.WEIGHED)) {
                weight = ((Number) nodeDate.get(KeysForFirebase.WEIGHED)).doubleValue();
                if (weight == 0f) {
                    continue; // skip days with zero weight
                }
            } else {
                continue; // skip days with missing weight
            }
            if (startDate.equals("")) {
                startWeight = weight;
                startDate = weightDate; // earliest date
                chartDataPoint.put(weightDate, 0.0);
            } else {
                chartDataPoint.put(weightDate, Double.valueOf(weight - startWeight));
            }

        }
        if (chartDataPoint.size() <= 1) {
            return; // no line to show
        }
        // now format chart series
        float xValue;
        float yValue;
        List<Entry> chartSeries = new ArrayList<Entry>();
        ArrayList<String> chartLabels = new ArrayList<String>();
        Entry point = new Entry();
        xValue = 0.0f;
        yValue = 0.0f;
        // change sort order
        for (String weightDate : lastSevenDays) {
            point = new Entry();
            point.setX(xValue);
            if (chartDataPoint.containsKey(weightDate)) {
                point.setY(((Number) chartDataPoint.get(weightDate)).floatValue());
            } else {
                continue;
            }
            chartSeries.add(point);
            chartLabels.add(mmddDisplay(weightDate));
            xValue += 1.0;
        }
        Log.i(TAG, "lineChartUdate: chartSeries: " + chartSeries.size());
        Log.i(TAG, "lineChartUdate: chartLabels: " + chartLabels.size());
        LineDataSet weightDataSet = new LineDataSet(chartSeries, "Weight Loss/Gain");
        weightDataSet.setColor(ContextCompat.getColor(getContext(), R.color.hw_green));
        weightDataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.hw_green));
        weightDataSet.setCircleColors(ContextCompat.getColor(getContext(),R.color.hw_green));
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override

            public String getFormattedValue(float value, AxisBase axis) {
                Log.i(TAG, "getFormattedValue: value = " + value);
                Log.i(TAG, "getFormattedValue: axis = " + axis.toString());
                if (((int) value ) >= chartLabels.size() || ((int) value) < 0) {
                    Log.i(TAG, "getFormattedValue: out of bounds index" + ((int) value));
                    return "bad label at " + value;
                } else {
                    Log.i(TAG, "getFormattedValue: getFormattedValue =" + chartLabels.get((int) value));
                    return  chartLabels.get((int) value);                }

            }
        };
        XAxis xAxis = weightChart.getXAxis();
        xAxis.setValueFormatter(formatter);
        xAxis.setLabelRotationAngle(-45f);
        xAxis.setGranularity(1f);
        Description chartTitle = weightChart.getDescription();
        chartTitle.setText("The Healthy Way Maintenance Chart");
        LineData weightData = new LineData(weightDataSet);
        weightChart.setData(weightData);
        weightChart.setVisibility(View.VISIBLE);
        weightChart.invalidate();
    }

    private String mmddDisplay(String firebaseDate){
        String mmDD = "";
        if (firebaseDate == null) {
            return "??/??";
        } else {
            mmDD = firebaseDate.substring(5,7)+"/"+firebaseDate.substring(8,10);
        }
        return mmDD;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(HW_Enumerations.TabNames tabIndex, HW_Enumerations.SubTabNames subTabIndex);
    }


    public void setOnFragmentInteractionListener(OnFragmentInteractionListener callback) {
        this.mListener = callback;
    }


}
