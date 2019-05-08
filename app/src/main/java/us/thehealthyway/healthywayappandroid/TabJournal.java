package us.thehealthyway.healthywayappandroid;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabJournal.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabJournal#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabJournal extends Fragment implements View.OnClickListener {
    private static final String TAG = "HW.TabJournal";
    // views
    ImageButton mailboxButton;
    Button save_button;
    Button cancel_button;
    Button delete_button;
    Button breakfast_button;
    Button morning_snack_button;
    Button lunch_button;
    Button afternoon_snack_button;
    Button dinner_button;
    Button evening_snack_button;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;
    CheckBox checkBox5;
    CheckBox checkBox6;
    CheckBox checkBox7;
    CheckBox checkBox8;
    CheckBox checkBoxSupplement1;
    CheckBox checkBoxSupplement2;
    CheckBox checkBoxSupplement3;
    CheckBox checkBoxExercise;
    EditText enter_weight;
    EditText enter_date;
    EditText enter_what_happened_today;
    EditText enter_meal_description;
    EditText enter_protein_consumption;
    EditText enter_starch_consumption;
    EditText enter_fruit_consumption;
    EditText enter_fat_consumption;
    EditText enter_veggies_consumption;
    EditText enter_meal_comments;
    TextView stats_header;
    TextView stats_limit;
    TextView stats_today;
    TextView stats_total;
    TextView client_message;
    TextView copyright;
    private InputMethodManager imm;
    
    // keyed values
    String keyed_enter_weight;
    String keyed_enter_date;
    String keyed_enter_what_happened_today;
    String keyed_enter_meal_description;
    String keyed_enter_protein_consumption;
    String keyed_enter_starch_consumption;
    String keyed_enter_fruit_consumption;
    String keyed_enter_fat_consumption;
    String keyed_enter_veggies_consumption;
    String keyed_enter_meal_comments;
    Date selectedDateFromDatePicker;
    String firebase_enter_date;
    int keyed_water_checks;
    int keyed_supplements_checks;
    int keyed_exercise_check;
    HW_Enumerations.Meals mealSelected = HW_Enumerations.Meals.MORNING_SNACK;
    Button lastMealSelected = null;

    // global flag for data changed
    Boolean isDataChanged = false;
    // model
    private Model model;

    // firebase work areas
    Map<String, Object> journalNode;
    Map<String, Object> mealContentsNode;
    Map<String, Object> settingsNode;
    Map<String, Object> userDataNode;
    Map<String, Object> mealNode;

    // Calendar
    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog datePickerDialog;
    Context context;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TabJournal() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabJournal.
     */
    // TODO: Rename and change types and number of parameters
    public static TabJournal newInstance(String param1, String param2) {
        TabJournal fragment = new TabJournal();
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
        selectedDateFromDatePicker = myCalendar.getTime(); // post current time

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // connect to model
        model = Model.getInstance();
        // setup the view
        View view;
        view = inflater.inflate(R.layout.fragment_journal_tab, container, false);
        if (lastMealSelected == null) {
            lastMealSelected = (Button) view.findViewById(R.id.mornng_snack_button);
        }
        // wire view buttons and data entry fields to this controller
        wireViewButtons(view);
        wireViewEditTextFields(view);
        wireViewTextViewFields(view);
        // post copyright
        copyright.setText(Model.makeCopyRight());

        // clear messages
        client_message.setText("");

        // refresh view
        postModelDataToView();
        postKeyedDataToView();
        // turn off action edit buttons
        hideDataEntryButtons();
        // get the data from the model

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(HW_Enumerations.TabNames.TAB_JOURNAL, HW_Enumerations.SubTabNames.SUB_TAB_LEVEL_0);
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

    @Override
    public void onClick(View v) {
        lastMealSelected.setVisibility(View.INVISIBLE);  // hide last selected button
        lastMealSelected = v.findViewById(v.getId());
        switch (v.getId()) {
            case R.id.breakfast_button:
                mealSelected = HW_Enumerations.Meals.BREAKFAST;
                mealNode = (Map<String, Object>) mealContentsNode.get(KeysForFirebase.BREAKFAST_MEAL_KEY);
                break;
            case R.id.mornng_snack_button:
                mealSelected = HW_Enumerations.Meals.MORNING_SNACK;
                mealNode = (Map<String, Object>) mealContentsNode.get(KeysForFirebase.MORNING_SNACK_MEAL_KEY);
                break;
            case R.id.lunch_button:
                mealSelected = HW_Enumerations.Meals.LUNCH;
                mealNode = (Map<String, Object>) mealContentsNode.get(KeysForFirebase.LUNCH_MEAL_KEY);
                break;
            case R.id.afternoon_snack_button:
                mealSelected = HW_Enumerations.Meals.AFTERNOON_SNACK;
                mealNode = (Map<String, Object>) mealContentsNode.get(KeysForFirebase.AFTERNOON_SNACK_MEAL_KEY);
                break;
            case R.id.dinner_button:
                mealSelected = HW_Enumerations.Meals.DINNER;
                mealNode = (Map<String, Object>) mealContentsNode.get(KeysForFirebase.DINNER_MEAL_KEY);
                break;
            case R.id.evening_snack_button:
                mealSelected = HW_Enumerations.Meals.EVENING_SNACK;
                mealNode = (Map<String, Object>) mealContentsNode.get(KeysForFirebase.EVENING_SNACK_MEAL_KEY);
                break;
            default:
                Log.d(TAG, "onClick: bad OnClick view ID");
                return;
        }
        lastMealSelected.setVisibility(View.VISIBLE);
        setupMeal();
    }

    void setupMeal() {
        // meal selection clears keyed data
        keyed_enter_meal_description = null;
        keyed_enter_protein_consumption = null;
        keyed_enter_starch_consumption = null;
        keyed_enter_fruit_consumption = null;
        keyed_enter_fat_consumption = null;
        keyed_enter_veggies_consumption = null;
        keyed_enter_meal_comments = null;
        if (mealNode == null) {
            enter_meal_description.setText("");
            enter_protein_consumption.setText("0.0");
            enter_starch_consumption.setText("0.0");
            enter_veggies_consumption.setText("0.0");
            enter_fat_consumption.setText("0.0");
            enter_fruit_consumption.setText("0.0");
            enter_meal_comments.setText("");
        } else {
            if (mealNode.get(KeysForFirebase.MEAL_DESCRIPTION) != null) {
                enter_meal_description.setText((String) mealNode.get(KeysForFirebase.MEAL_DESCRIPTION));
            } else {
                enter_meal_description.setText("");
            }
            if (mealNode.get(KeysForFirebase.MEAL_COMMENTS) !=  null) {
                enter_meal_comments.setText((String) mealNode.get(KeysForFirebase.MEAL_COMMENTS));
            } else {
                enter_meal_comments.setText("");
            }
            if (mealNode.get(KeysForFirebase.MEAL_PROTEIN_QUANTITY) != null) {
                enter_protein_consumption.setText(String.valueOf((double) mealNode.get(KeysForFirebase.MEAL_PROTEIN_QUANTITY)));
            } else {
                enter_protein_consumption.setText("0.0");
            }
            if (mealNode.get(KeysForFirebase.MEAL_STARCH_QUANTITY) !=null) {
                enter_starch_consumption.setText(String.valueOf((double) mealNode.get(KeysForFirebase.MEAL_STARCH_QUANTITY)));
            } else {
                enter_starch_consumption.setText("0.0");
            }
            if (mealNode.get(KeysForFirebase.MEAL_FAT_QUANTITY) != null) {
                enter_fat_consumption.setText(String.valueOf((double) mealNode.get(KeysForFirebase.MEAL_FAT_QUANTITY)));
            } else {
                enter_fat_consumption.setText("0.0");
            }
            if (mealNode.get(KeysForFirebase.MEAL_FRUIT_QUANTITY) != null) {
                enter_fruit_consumption.setText(String.valueOf((double) mealNode.get(KeysForFirebase.MEAL_FRUIT_QUANTITY)));
            } else {
                enter_fruit_consumption.setText("0.0");
            }
            if (mealNode.get(KeysForFirebase.MEAL_VEGGIES_QUANTITY) != null) {
                enter_veggies_consumption.setText(String.valueOf((double) mealNode.get(KeysForFirebase.MEAL_VEGGIES_QUANTITY)));
            } else {
                enter_veggies_consumption.setText("0.0");
            }
        }
    }

    // button helper methods
    void wireViewButtons(View view) {

        delete_button = (Button) view.findViewById(R.id.my_account_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete journal entries for the day
                deleteDisplayedJournalEntries();
            }
        });
        save_button = (Button) view.findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJournalEntries();
            }
        });

        cancel_button = (Button) view.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelJournalEntries();
            }
        });
        mailboxButton = (ImageButton) view.findViewById(R.id.mailboxButton);
        mailboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailJournal();
            }
        });
        // locate buttons
        breakfast_button = (Button) view.findViewById(R.id.breakfast_button);
        morning_snack_button = (Button) view.findViewById(R.id.mornng_snack_button);
        lunch_button = (Button) view.findViewById(R.id.lunch_button);
        afternoon_snack_button = (Button) view.findViewById(R.id.afternoon_snack_button);
        dinner_button = (Button) view.findViewById(R.id.dinner_button);
        evening_snack_button = (Button) view.findViewById(R.id.evening_snack_button);
        // wire button clicks
        breakfast_button.setOnClickListener(this);
        morning_snack_button.setOnClickListener(this);
        lunch_button.setOnClickListener(this);
        afternoon_snack_button.setOnClickListener(this);
        dinner_button.setOnClickListener(this);
        evening_snack_button.setOnClickListener(this);

    }



    void wireViewEditTextFields(View view) {
        // connect to view
        enter_weight = (EditText) view.findViewById(R.id.enter_weight);
        enter_what_happened_today = (EditText) view.findViewById(R.id.enter_what_happened_today);
        enter_date = (EditText) view.findViewById(R.id.enter_date);
        // clear text on focus
        enter_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_weight.setText("");
                showDataEntryButtons();
            }
        });
       // wire date picker dialog for obtaining date
        enter_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context = getContext();
                int mYear = myCalendar.get(Calendar.YEAR);
                int mMonth = myCalendar.get(Calendar.MONTH);
                int mDay = myCalendar.get(Calendar.DAY_OF_MONTH);
                // date picker dialog
                datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        enter_date.setText(month + "/" + (dayOfMonth + 1) + "/" + year);
                        keyed_enter_date = month + "/" + (dayOfMonth + 1) + "/" + year;
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        firebase_enter_date = year+"-"+(month + 1)+"-"+ dayOfMonth;
                        try {
                            selectedDateFromDatePicker = df.parse(firebase_enter_date);
                        } catch(Exception e) {
                            selectedDateFromDatePicker = myCalendar.getTime();
                        }
                    }
                }, mYear, mMonth, mDay);
                isDataChanged = true;
            }
        });
        enter_what_happened_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_what_happened_today.setText("");
                showDataEntryButtons();
            }
        });
        enter_meal_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_meal_description.setText("");
                showDataEntryButtons();
            }
        });
        enter_protein_consumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_protein_consumption.setText("");
                showDataEntryButtons();
            }
        });
        enter_starch_consumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_starch_consumption.setText("");
                showDataEntryButtons();
            }
        });
        enter_fat_consumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_fat_consumption.setText("");
                showDataEntryButtons();
            }
        });
        enter_fruit_consumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_fruit_consumption.setText("");
                showDataEntryButtons();
            }
        });
        enter_veggies_consumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_veggies_consumption.setText("");
                showDataEntryButtons();
            }
        });
        //  connect editor actions
        enter_weight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                keyed_enter_weight = dataFieldHasChanged(v);
                return true;
            }
        });

        enter_what_happened_today.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                keyed_enter_what_happened_today = dataFieldHasChanged(v);
                return true;
            }
        });

        enter_meal_description.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                keyed_enter_meal_description = dataFieldHasChanged(v);
                return true;
            }
        });

        enter_meal_comments.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                keyed_enter_meal_comments = dataFieldHasChanged(v);
                return true;
            }
        });

        enter_protein_consumption.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                keyed_enter_protein_consumption = dataFieldHasChanged(v);
                return true;
            }
        });

        enter_starch_consumption.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                keyed_enter_starch_consumption = dataFieldHasChanged(v);
                return true;
            }
        });



        enter_fat_consumption.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                keyed_enter_fat_consumption = dataFieldHasChanged(v);
                return true;
            }
        });

        enter_fruit_consumption.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                keyed_enter_fruit_consumption = dataFieldHasChanged(v);
                return true;
            }
        });

        enter_veggies_consumption.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                keyed_enter_veggies_consumption = dataFieldHasChanged(v);
                return true;
            }
        });

    }

    void wireViewTextViewFields(View view) {
        // write only
        copyright = (TextView) view.findViewById(R.id.daily_totals_copyright);
        client_message = (TextView) view.findViewById(R.id.daily_totals_message);
        stats_limit = (TextView) view.findViewById(R.id.stats_limit);
        stats_today = (TextView) view.findViewById(R.id.stats_today);
        stats_total = (TextView) view.findViewById(R.id.stats_total);
        stats_header = (TextView) view.findViewById(R.id.stats_header);
    }


    void deleteDisplayedJournalEntries() {
        isDataChanged = false;
        hideDataEntryButtons();
        
    }

    void saveJournalEntries() {
        isDataChanged = false;
        hideDataEntryButtons();
        saveDataToModel();
    }

    void cancelJournalEntries() {

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
            if (keyed_enter_date != null) enter_date.setText(keyed_enter_date);
            if (keyed_enter_weight != null) enter_weight.setText(keyed_enter_weight);
            if (keyed_enter_what_happened_today != null) enter_what_happened_today.setText(keyed_enter_what_happened_today);
            if (keyed_enter_meal_description != null) enter_meal_description.setText(keyed_enter_meal_description);
            if (keyed_enter_fat_consumption != null) enter_fat_consumption.setText(keyed_enter_fat_consumption);
            if (keyed_enter_fruit_consumption != null) enter_fruit_consumption.setText(keyed_enter_fruit_consumption);
            if (keyed_enter_protein_consumption != null) enter_protein_consumption.setText(keyed_enter_protein_consumption);
            if (keyed_enter_starch_consumption != null) enter_starch_consumption.setText(keyed_enter_starch_consumption);
            if (keyed_enter_veggies_consumption != null) enter_veggies_consumption.setText(keyed_enter_veggies_consumption);
            if (keyed_enter_meal_comments != null) enter_meal_comments.setText(keyed_enter_meal_comments);
        }

    }

    String dataFieldHasChanged(TextView textView) {
        InputMethodManager imm = (InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
        showDataEntryButtons();
        isDataChanged = true;
        return textView.getText().toString();
    }

    void postModelDataToView() {
        FirebaseDictionary meal = new FirebaseDictionary();
        userDataNode = model.getSignedinUserDataNode();
        if (userDataNode.get(KeysForFirebase.NODE_MEAL_CONTENTS) == null) {
            // if no mealContents then set values to zero
            zeroMealConsumption();
        } else {
            mealContentsNode = (FirebaseDictionary) userDataNode.get(KeysForFirebase.NODE_MEAL_CONTENTS);
            if (mealContentsNode.get(firebase_enter_date) == null) {
                zeroMealConsumption();
            } else {
                if (mealContentsNode.get(firebase_enter_date) == null) {
                    zeroMealConsumption();
                } else {
                    meal = (FirebaseDictionary) mealContentsNode.get(firebase_enter_date);
                    showMealQuantities(meal, enter_protein_consumption, KeysForFirebase.MEAL_PROTEIN_QUANTITY);
                    showMealQuantities(meal, enter_fat_consumption, KeysForFirebase.MEAL_FAT_QUANTITY);

                }
            }
        }
        // add meal based on date and meal type
        highlightMealSelected(mealSelected);

        if (userDataNode.get(KeysForFirebase.NODE_JOURNAL) == null) {
            // if no Journal then set values to default
            enter_weight.setText("0.0");
            enter_date.setText(Helpers.showToday());
        } else {

        }
    }

    void showMealQuantities(FirebaseDictionary meal, EditText quantity, String key) {
        if (meal.get(key) == null) {
            quantity.setText("0.0");
        } else {
            quantity.setText(String.valueOf(meal.get(key)));
        }
    }
    void highlightMealSelected(HW_Enumerations.Meals meal) {
        lastMealSelected.setSelected(false);
        switch (meal) {
            case BREAKFAST:
                breakfast_button.setSelected(true);
                lastMealSelected = breakfast_button;
            case MORNING_SNACK:
                morning_snack_button.setSelected(true);
                lastMealSelected = morning_snack_button;
            case LUNCH:
                lunch_button.setSelected(true);
                lastMealSelected = lunch_button;
            case AFTERNOON_SNACK:
                afternoon_snack_button.setSelected(true);
                lastMealSelected = afternoon_snack_button;
            case DINNER:
                dinner_button.setSelected(true);
                lastMealSelected = dinner_button;
            case EVENING_SNACK:
                evening_snack_button.setSelected(true);
                lastMealSelected = evening_snack_button;
            default:
                lastMealSelected.setSelected(true);
                Log.d(TAG, "highlightMealSelected: bad meal");
        }
    }

    void zeroMealConsumption() {
        enter_protein_consumption.setText(String.valueOf("0.0"));
        enter_starch_consumption.setText(String.valueOf("0.0"));
        enter_fat_consumption.setText(String.valueOf("0.0"));
        enter_fruit_consumption.setText(String.valueOf("0.0"));
        enter_veggies_consumption.setText("0.0");
    }

    void saveDataToModel() {
        userDataNode = model.getSignedinUserDataNode();
        if (userDataNode.get(KeysForFirebase.NODE_SETTINGS) == null) {
            settingsNode = new HashMap<String, Object>();
            settingsNode.put(KeysForFirebase.LIMIT_PROTEIN_LOW, "0.0");
            settingsNode.put(KeysForFirebase.LIMIT_PROTEIN_HIGH, "0.0");
            settingsNode.put(KeysForFirebase.LIMIT_FAT, "0.0");
            settingsNode.put(KeysForFirebase.LIMIT_FRUIT, "0.0");
            settingsNode.put(KeysForFirebase.LIMIT_STARCH, "0.0");
        } else {
            settingsNode = (Map<String, Object>)userDataNode.get(KeysForFirebase.NODE_SETTINGS);
        }
        if (keyed_limit_protein_low != null) settingsNode.put(KeysForFirebase.LIMIT_PROTEIN_LOW, keyed_limit_protein_low);
        if (keyed_limit_protein_high != null) settingsNode.put(KeysForFirebase.LIMIT_PROTEIN_HIGH, keyed_limit_protein_high);
        if (keyed_limit_fat != null) settingsNode.put(KeysForFirebase.LIMIT_FAT, keyed_limit_fat);
        if (keyed_limit_fruits != null) settingsNode.put(KeysForFirebase.LIMIT_FRUIT, keyed_limit_fruits);
        if (keyed_limit_starch != null) settingsNode.put(KeysForFirebase.LIMIT_STARCH, keyed_limit_starch);
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
    
    void mailJournal(){
        
    }


    // Firebase methods

    Map<String, Object> journalOnDate(String date, Map<String, Object> node) {
        Map<String, Object> userJournalContents;
        Map<String, Object> userJournalContentsOnDate;
        if (userDataNode == null) {
            userDataNode = new HashMap<String, Object>();
        }
        if (userDataNode.get(KeysForFirebase.NODE_JOURNAL) == null) {
            userJournalContents = new HashMap<String, Object>();
        } else {
            userJournalContents = (Map<String, Object>)userDataNode.get(KeysForFirebase.NODE_JOURNAL);
        }
        if (userJournalContents.get(firebase_enter_date) == null) {
            userJournalContentsOnDate = new HashMap<String, Object>();
        } else {
            userJournalContentsOnDate  = (Map<String,Object>) userJournalContents.get(date);
        }
        return userJournalContentsOnDate;
    }

    Map<String, Object> mealOnDate(String date) {
        Map<String, Object> userMealContents = new HashMap<String, Object>();
        Map<String, Object> userMealContentsOnDate = new HashMap<String, Object>();
        if (userDataNode == null) {
            userDataNode = new HashMap<String, Object>();
        }
        if (userDataNode.get(KeysForFirebase.NODE_MEAL_CONTENTS) == null) {
            userMealContents = new HashMap<String, Object>();
        } else {
            userMealContents = (Map<String, Object>) userDataNode.get(KeysForFirebase.NODE_MEAL_CONTENTS);
        }
        if (userMealContents.get(date) == null) {
            userMealContentsOnDate = new HashMap<String, Object>();
        } else {
            userMealContents = (Map<String, Object>) userMealContents.get(date);
        }
        return userMealContents;
    }


    void updateJournalOnDate(String date, Map<String, Object> node) {
        // uses local copy of userData node
        Map<String, Object> userJournalContents;
        Map<String, Object> userJournalContentsOnDate;
        if (userDataNode == null) {
            userDataNode = new HashMap<String, Object>();
        }
        if (userDataNode.get(KeysForFirebase.NODE_JOURNAL) == null) {
            userJournalContents = new HashMap<String, Object>();
        } else {
            userJournalContents = (Map<String, Object>)userDataNode.get(KeysForFirebase.NODE_JOURNAL);
        }
        if (userJournalContents.get(date) == null) {
            userJournalContentsOnDate = new HashMap<String, Object>();
        } else {
            userJournalContentsOnDate  = (Map<String,Object>) userJournalContents.get(date);
        }

        // deletion checks
        if (node.size() == 0) {
            if (userJournalContentsOnDate.size() == 0) {
                return; // no node to remove
            } else {
                userJournalContents.put(date,null);
            }
        } else {
            userJournalContentsOnDate = node;
            userJournalContents.put(date, userJournalContentsOnDate);
        }
        // update node
        userDataNode.put(KeysForFirebase.NODE_JOURNAL, userJournalContents);
    }


    void updateMealOnDate(String date, Map<String, Object> node) {
        Map<String, Object> userMealContents = new HashMap<String, Object>();
        Map<String, Object> userMealContentsOnDate = new HashMap<String, Object>();
        if (userDataNode == null) {
            userDataNode = new HashMap<String, Object>();
        }
        if (userDataNode.get(KeysForFirebase.NODE_MEAL_CONTENTS) == null) {
            userMealContents = new HashMap<String, Object>();
        } else {
            userMealContents = (Map<String, Object>) userDataNode.get(KeysForFirebase.NODE_MEAL_CONTENTS);
        }
        if (userMealContents.get(date) == null) {
            userMealContentsOnDate = new HashMap<String, Object>();
        } else {
            userMealContents = (Map<String, Object>) userMealContents.get(date);
        }
        // deletion checks
        if (node.size() == 0) {
            if (userMealContentsOnDate.size() == 0) {
                return; // no node to remove
            } else {
                userMealContents.put(date, null);
            }
        } else {
            userMealContentsOnDate = node;
            userMealContents.put(date, userMealContentsOnDate);
        }
        // update node
        userDataNode.put(KeysForFirebase.NODE_MEAL_CONTENTS, userMealContents);
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
