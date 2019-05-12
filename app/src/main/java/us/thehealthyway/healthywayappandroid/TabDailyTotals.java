package us.thehealthyway.healthywayappandroid;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabDailyTotals.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabDailyTotals#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabDailyTotals extends Fragment {

    // constants
    BundleKeys bundleKeys;
    // views
    EditText limit_protein_low;
    EditText limit_protein_high;
    EditText limit_fat;
    EditText limit_starch;
    EditText limit_fruits;
    TextView daily_totals_message;
    Button save_button;
    Button cancel_button;
    Button my_account_button;
    TextView copyright;
    private InputMethodManager imm;


    // keyed values
    String keyed_limit_protein_low;
    String keyed_limit_protein_high;
    String keyed_limit_fat;
    String keyed_limit_starch;
    String keyed_limit_fruits;
    String keyed_limit_veggies; // constant 3 servings
    // global flag for data changed
    Boolean isDataChanged = false;
    
    // model
    private Model model;

    // firebase work areas
    Map<String, Object> settingsNode;
    Map<String, Object> userDataNode;


    private  FragmentTransaction ft;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;




    public TabDailyTotals() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabDailyTotals.
     */
    // TODO: Rename and change types and number of parameters
    public static TabDailyTotals newInstance(String param1, String param2) {
        TabDailyTotals fragment = new TabDailyTotals();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // dummy initialization of enums
        // BundleKeys.BundleKeyValues.values();
        bundleKeys = BundleKeys.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // connect to model
        model = Model.getInstance();
        // setup the view
        View view;
        view = inflater.inflate(R.layout.fragment_daily_totals_tab, container, false);

        // wire view buttons and data entry fields to this controller
        wireViewButtons(view);
        wireViewEditTextFields(view);
        wireViewTextViewFields(view);
        // post copyright
        copyright.setText(Model.makeCopyRight());

        // clear messages
        daily_totals_message.setText("");
        
        // refresh view
        postModelDataToView();
        postKeyedDataToView();
        // turn off action edit buttons
        hideDataEntryButtons();
        // get the data from the model

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(HW_Enumerations.TabNames.TAB_DAILY_TOTALS, HW_Enumerations.SubTabNames.SUB_TAB_LEVEL_1);
        }
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



    // button helper methods
    void wireViewButtons(View view) {

        my_account_button = (Button) view.findViewById(R.id.my_account_button);
        my_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // route to next level view
                mListener.onFragmentInteraction(HW_Enumerations.TabNames.TAB_DAILY_TOTALS, HW_Enumerations.SubTabNames.SUB_TAB_LEVEL_1);
            }
        });
        save_button = (Button) view.findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        cancel_button = (Button) view.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSettings();
            }
        });
    }

    void wireViewEditTextFields(View view) {
        // connect to view
        limit_protein_low = (EditText) view.findViewById(R.id.limit_protein_low);
        limit_protein_high = (EditText) view.findViewById(R.id.limit_protein_high);
        limit_fat = (EditText) view.findViewById(R.id.limit_fat);
        limit_fruits = (EditText) view.findViewById(R.id.limit_fruits);
        limit_starch = (EditText) view.findViewById(R.id.limit_starch);
        // clear text on focus
        limit_fat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limit_fat.setText("");
                showDataEntryButtons();
            }
        });
        limit_fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limit_fruits.setText("");
                showDataEntryButtons();
            }
        });
        limit_starch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limit_starch.setText("");
                showDataEntryButtons();
            }
        });
        limit_protein_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limit_protein_low.setText("");
                showDataEntryButtons();
            }
        });
        limit_protein_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limit_protein_high.setText("");
                showDataEntryButtons();
            }
        });



        // connect editor actions

        limit_protein_low.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                keyed_limit_protein_low = dataFieldHasChanged(textView);
                return true;
            }

        });

        limit_protein_high.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                keyed_limit_protein_high = dataFieldHasChanged(textView);
                return true;
            }

        });

        limit_fat.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                keyed_limit_fat = dataFieldHasChanged(textView);
                return true;
            }

        });

        limit_fruits.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                keyed_limit_fruits = dataFieldHasChanged(textView);
                return true;
            }

        });

        limit_starch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                keyed_limit_starch = dataFieldHasChanged(textView);
                return true;
            }

        });

    }

    void wireViewTextViewFields(View view) {
        copyright = (TextView) view.findViewById(R.id.daily_totals_copyright);
        daily_totals_message = (TextView) view.findViewById(R.id.daily_totals_message);
    }


    void saveSettings() {
        isDataChanged = false;
        hideDataEntryButtons();
        saveDataToModel();
    }

    void cancelSettings() {

        hideDataEntryButtons();
    }

    void showDataEntryButtons() {
        cancel_button.setVisibility(View.VISIBLE);
        save_button.setVisibility(View.VISIBLE);
    }

    void hideDataEntryButtons() {
        cancel_button.setVisibility(View.INVISIBLE);
        save_button.setVisibility(View.INVISIBLE);
    }
    

    void postKeyedDataToView() {
        if (isDataChanged) {
            if (keyed_limit_protein_low != null) limit_protein_low.setText(keyed_limit_protein_low);
            if (keyed_limit_protein_high != null ) limit_protein_high.setText(keyed_limit_protein_high);
            if (keyed_limit_starch != null ) limit_starch.setText(keyed_limit_starch);
            if (keyed_limit_fat != null ) limit_fat.setText(keyed_limit_fat);
            if (keyed_limit_fruits != null ) limit_fruits.setText(keyed_limit_fruits);
        } 
        // skip hardcoded veggies values
    }

    String dataFieldHasChanged(TextView textView) {
        InputMethodManager imm = (InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
        showDataEntryButtons();
        isDataChanged = true;
        return textView.getText().toString();
    }

    void postModelDataToView() {
        userDataNode = model.getSignedinUserDataNode();
        if ((Map<String, Object>)userDataNode.get(KeysForFirebase.NODE_SETTINGS) == null) {
            // if no settings then set values to zero
            limit_protein_low.setText(String.valueOf("0.0"));
            limit_protein_high.setText(String.valueOf("0.0"));
            limit_starch.setText(String.valueOf("0.0"));
            limit_fat.setText(String.valueOf("0.0"));
            limit_fruits.setText(String.valueOf("0.0"));
        } else {
            settingsNode = (Map<String, Object>)userDataNode.get(KeysForFirebase.NODE_SETTINGS);
            limit_protein_low.setText(String.valueOf(settingsNode.get(KeysForFirebase.LIMIT_PROTEIN_LOW)));
            limit_protein_high.setText(String.valueOf(settingsNode.get(KeysForFirebase.LIMIT_PROTEIN_HIGH)));
            limit_starch.setText(String.valueOf(settingsNode.get(KeysForFirebase.LIMIT_STARCH)));
            limit_fat.setText(String.valueOf(settingsNode.get(KeysForFirebase.LIMIT_FAT)));
            limit_fruits.setText(String.valueOf(settingsNode.get(KeysForFirebase.LIMIT_FRUIT)));
        }
    }

    void saveDataToModel() {
        userDataNode = model.getSignedinUserDataNode();
        if ((Map<String, Object>)userDataNode.get(KeysForFirebase.NODE_SETTINGS) == null) {
            settingsNode = new HashMap<String, Object>();
            settingsNode.put(KeysForFirebase.LIMIT_PROTEIN_LOW, 0.0);
            settingsNode.put(KeysForFirebase.LIMIT_PROTEIN_HIGH, 0.0);
            settingsNode.put(KeysForFirebase.LIMIT_FAT, 0.0);
            settingsNode.put(KeysForFirebase.LIMIT_FRUIT, 0.0);
            settingsNode.put(KeysForFirebase.LIMIT_STARCH, 0.0);
        } else {
            settingsNode = (Map<String, Object>)userDataNode.get(KeysForFirebase.NODE_SETTINGS);
        }
        if (keyed_limit_protein_low != null) settingsNode.put(KeysForFirebase.LIMIT_PROTEIN_LOW, Double.valueOf(keyed_limit_protein_low));
        if (keyed_limit_protein_high != null) settingsNode.put(KeysForFirebase.LIMIT_PROTEIN_HIGH, Double.valueOf(keyed_limit_protein_high));
        if (keyed_limit_fat != null) settingsNode.put(KeysForFirebase.LIMIT_FAT, Double.valueOf(keyed_limit_fat));
        if (keyed_limit_fruits != null) settingsNode.put(KeysForFirebase.LIMIT_FRUIT, Double.valueOf(keyed_limit_fruits));
        if (keyed_limit_starch != null) settingsNode.put(KeysForFirebase.LIMIT_STARCH, Double.valueOf(keyed_limit_starch));
        userDataNode.put(KeysForFirebase.NODE_SETTINGS,settingsNode);
        model.setNodeUserData(userDataNode, (message)-> {writeFailure(message); },
                ()-> {writeSuccess(); });
    }

    void writeFailure(String message) {
        daily_totals_message.setText(message);
    }

    void writeSuccess() {
        isDataChanged = false;
        keyed_limit_protein_low = null;
        keyed_limit_protein_high = null;
        keyed_limit_fat = null;
        keyed_limit_fruits = null;
        keyed_limit_starch = null;
        save_button.setVisibility(View.INVISIBLE);
        cancel_button.setVisibility(View.INVISIBLE);
    }
    public void onClick() {

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
}
