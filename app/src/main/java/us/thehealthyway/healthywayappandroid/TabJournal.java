package us.thehealthyway.healthywayappandroid;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.JetPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static us.thehealthyway.healthywayappandroid.Helpers.doubleFromObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabJournal.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabJournal#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabJournal extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {
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
    CheckBox checkBoxWater1;
    CheckBox checkBoxWater2;
    CheckBox checkBoxWater3;
    CheckBox checkBoxWater4;
    CheckBox checkBoxWater5;
    CheckBox checkBoxWater6;
    CheckBox checkBoxWater7;
    CheckBox checkBoxWater8;
    CheckBox checkBoxSupplement1;
    CheckBox checkBoxSupplement2;
    CheckBox checkBoxSupplement3;
    CheckBox checkBoxExercise;
    EditText enter_weight;
    Button enter_date; // special case to prevent data entry
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

    // groups
    ArrayList<CheckBox> groupCheckboxes = new ArrayList<CheckBox>();

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
    // track checkboxes
    boolean isUpdateOfCheckboxesNeeded = false;
    int track_water_checks = -1;
    int track_supplements_checks = -1;
    int track_exercise_check = -1;
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
    // capture data changes
    String onFocusValue;
    String offFocusValue;
    View focusView;

    // fields
    Map<String, EditText> fields = new HashMap<>();
    EditText[] editTexts;


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
        // Instantiate enumeration singleton classes
        HW_Meals meals = HW_Meals.getInstance();
        HW_FoodValues foodTypes = HW_FoodValues.getInstance();

        mealSelected = HW_Enumerations.Meals.BREAKFAST;
        selectedDateFromDatePicker = myCalendar.getTime(); // post current time
        firebase_enter_date = Helpers.makeFirebaseDate(selectedDateFromDatePicker);

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
        // setup EditText fields
        // wire view buttons and data entry fields to this controller
        wireViewButtons(view);
        if (lastMealSelected == null) {
            lastMealSelected = breakfast_button;
            lastMealSelected.setSelected(true);
            lastMealSelected.setBackgroundColor(Color.parseColor("#4A2353"));
            lastMealSelected.setTextColor(Color.parseColor("#FFFFFF"));
        }
        wireViewEditTextFields(view);
        wireViewTextViewFields(view);
        groupCheckboxes = wireCheckmarks(view);
        // post copyright
        copyright.setText(Model.makeCopyRight());

        // clear messages
        client_message.setText("");
        // refresh view
        postModelDataToView();
        postKeyedDataToView();
        // turn off action edit buttons
        hideDataEntryButtons();
        // show the totals
        displayTotals();

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
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (v instanceof EditText) {
                onFocusValue = ((EditText) v).getText().toString();
                focusView = v;
                Log.d(TAG, "onFocusChange (on), value: " + onFocusValue);
            }
        } else {
            if (v instanceof EditText) {
                offFocusValue = ((EditText) v).getText().toString();
                Log.d(TAG, "onFocusChange (off), value: " + offFocusValue);
                if (v.getId() != focusView.getId()) {
                    Log.d(TAG, "onFocusChange: " + "missed focus transition");
                } else {
                    if (onFocusValue.equals(offFocusValue)) {
                        return;
                    } else {
                        captureDataChange(v, offFocusValue);
                    }
                }
            }
        }
    }


    // capture data changes with touches
    void captureDataChange(View view, String changedData) {
        isDataChanged = true;
        switch (view.getId()) {
            case R.id.enter_weight:
                keyed_enter_weight = changedData;
                break;
            case R.id.enter_what_happened_today:
                keyed_enter_what_happened_today = changedData;
                break;
            case R.id.enter_meal_description:
                keyed_enter_meal_description = changedData;
                break;
            case R.id.enter_fat_consumption:
                keyed_enter_fat_consumption = changedData;
                break;
            case R.id.enter_fruit_consumption:
                keyed_enter_fruit_consumption = changedData;
                break;
            case R.id.enter_protein_consumption:
                keyed_enter_protein_consumption = changedData;
                break;
            case R.id.enter_starch_consumption:
                keyed_enter_starch_consumption = changedData;
                break;
            case R.id.enter_veggies_consumption:
                keyed_enter_veggies_consumption = changedData;
                break;
            default:
                Log.d(TAG, "captureDataChange: CODE ME");
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkBoxWater1:
            case R.id.checkBoxWater2:
            case R.id.checkBoxWater3:
            case R.id.checkBoxWater4:
            case R.id.checkBoxWater5:
            case R.id.checkBoxWater6:
            case R.id.checkBoxWater7:
            case R.id.checkBoxWater8:
            case R.id.checkBoxSupplement1:
            case R.id.checkBoxSupplement2:
            case R.id.checkBoxSupplement3:
            case R.id.checkBoxExercise:
                isUpdateOfCheckboxesNeeded = true;
                int checkIndex = 0;
                switch (v.getId()) {
                    case R.id.checkBoxWater1:
                        checkIndex = 0;
                        break;
                    case R.id.checkBoxWater2:
                        checkIndex = 1;
                        break;
                    case R.id.checkBoxWater3:
                        checkIndex = 2;
                        break;
                    case R.id.checkBoxWater4:
                        checkIndex = 3;
                        break;
                    case R.id.checkBoxWater5:
                        checkIndex = 4;
                        break;
                    case R.id.checkBoxWater6:
                        checkIndex = 5;
                        break;
                    case R.id.checkBoxWater7:
                        checkIndex = 6;
                        break;
                    case R.id.checkBoxWater8:
                        checkIndex = 7;
                        break;
                    case R.id.checkBoxSupplement1:
                        checkIndex = 8;
                        break;
                    case R.id.checkBoxSupplement2:
                        checkIndex = 9;
                        break;
                    case R.id.checkBoxSupplement3:
                        checkIndex = 10;
                        break;
                    case R.id.checkBoxExercise:
                        checkIndex = 11;
                        break;
                }
                CheckBox box = (CheckBox) groupCheckboxes.get(checkIndex);
                if (box.isChecked()) {
                    if (checkIndex < 8) {
                        if (track_water_checks == -1) {
                            track_water_checks = 0;
                        }
                        track_water_checks++;
                    } else {
                        if (checkIndex < 11) {
                            if (track_supplements_checks == -1) {
                                track_supplements_checks = 0;
                            }
                            track_supplements_checks++;
                        } else {
                            track_exercise_check = 1;
                        }
                    }
                } else {
                    if (checkIndex < 8) {
                        track_water_checks--;
                    } else {
                        if (checkIndex < 11) {
                            track_supplements_checks--;
                        } else {
                            track_exercise_check = 0;
                        }
                    }
                }
                showCheckboxes(track_water_checks, track_supplements_checks, track_exercise_check);
                showDataEntryButtons();
                break;
            default:
                lastMealSelected.setSelected(false);
                lastMealSelected.setBackgroundColor(Color.parseColor("#FFFFFF"));
                lastMealSelected.setTextColor(Color.parseColor("#4A2353"));
                lastMealSelected = v.findViewById(v.getId());
                Map<String, Object> mealContentsContentsOnDate = new HashMap<>();
                if (mealContentsNode != null) {
                    mealContentsContentsOnDate = (Map<String, Object>) mealContentsNode.get(firebase_enter_date);
                }
                switch (v.getId()) {
                    case R.id.breakfast_button:
                        mealSelected = HW_Enumerations.Meals.BREAKFAST;
                        mealNode = (Map<String, Object>) mealContentsContentsOnDate.get(KeysForFirebase.BREAKFAST_MEAL_KEY);
                        break;
                    case R.id.mornng_snack_button:
                        mealSelected = HW_Enumerations.Meals.MORNING_SNACK;
                        mealNode = (Map<String, Object>) mealContentsContentsOnDate.get(KeysForFirebase.MORNING_SNACK_MEAL_KEY);
                        break;
                    case R.id.lunch_button:
                        mealSelected = HW_Enumerations.Meals.LUNCH;
                        mealNode = (Map<String, Object>) mealContentsContentsOnDate.get(KeysForFirebase.LUNCH_MEAL_KEY);
                        break;
                    case R.id.afternoon_snack_button:
                        mealSelected = HW_Enumerations.Meals.AFTERNOON_SNACK;
                        mealNode = (Map<String, Object>) mealContentsContentsOnDate.get(KeysForFirebase.AFTERNOON_SNACK_MEAL_KEY);
                        break;
                    case R.id.dinner_button:
                        mealSelected = HW_Enumerations.Meals.DINNER;
                        mealNode = (Map<String, Object>) mealContentsContentsOnDate.get(KeysForFirebase.DINNER_MEAL_KEY);
                        break;
                    case R.id.evening_snack_button:
                        mealSelected = HW_Enumerations.Meals.EVENING_SNACK;
                        mealNode = (Map<String, Object>) mealContentsContentsOnDate.get(KeysForFirebase.EVENING_SNACK_MEAL_KEY);
                        break;
                    default:
                        Log.d(TAG, "onClick: bad OnClick view ID");
                        return;
                }
                lastMealSelected.setSelected(true);
                lastMealSelected.setBackgroundColor(Color.parseColor("#4A2353"));
                lastMealSelected.setTextColor(Color.parseColor("#FFFFFF"));
                setupMeal();
        }
    }


    void showCheckboxes(int water_checks, int supplements_checks, int exercise_check) {
        // -1 in track value indicates no data keyed. Use values from database
        if (water_checks != -1) {
            for (int i = 0; i < 8; i++) {
                if (i < water_checks) {
                    groupCheckboxes.get(i).setChecked(true);
                } else {
                    groupCheckboxes.get(i).setChecked(false);
                }
            }
        }
        if (supplements_checks != -1) {
            for (int i = 0; i < 3; i++) {
                if (i < supplements_checks) {
                    groupCheckboxes.get(i + 8).setChecked(true);
                } else {
                    groupCheckboxes.get(i + 8).setChecked(false);
                }
            }
        }
        if (exercise_check != -1) {
            if (exercise_check == 0) {
                groupCheckboxes.get(11).setChecked(false);
            } else {
                groupCheckboxes.get(11).setChecked(true);
            }
        }
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
            if (mealNode.get(KeysForFirebase.MEAL_COMMENTS) != null) {
                enter_meal_comments.setText((String) mealNode.get(KeysForFirebase.MEAL_COMMENTS));
            } else {
                enter_meal_comments.setText("");
            }
            if (mealNode.get(KeysForFirebase.MEAL_PROTEIN_QUANTITY) != null) {
                enter_protein_consumption.setText(String.valueOf(doubleFromObject((Object) mealNode.get(KeysForFirebase.MEAL_PROTEIN_QUANTITY))));
            } else {
                enter_protein_consumption.setText("0.0");
            }
            if (mealNode.get(KeysForFirebase.MEAL_STARCH_QUANTITY) != null) {
                enter_starch_consumption.setText(String.valueOf(doubleFromObject((Object) mealNode.get(KeysForFirebase.MEAL_STARCH_QUANTITY))));
            } else {
                enter_starch_consumption.setText("0.0");
            }
            if (mealNode.get(KeysForFirebase.MEAL_FAT_QUANTITY) != null) {
                enter_fat_consumption.setText(String.valueOf(doubleFromObject((Object) mealNode.get(KeysForFirebase.MEAL_FAT_QUANTITY))));
            } else {
                enter_fat_consumption.setText("0.0");
            }
            if (mealNode.get(KeysForFirebase.MEAL_FRUIT_QUANTITY) != null) {
                enter_fruit_consumption.setText(String.valueOf(doubleFromObject((Object) mealNode.get(KeysForFirebase.MEAL_FRUIT_QUANTITY))));
            } else {
                enter_fruit_consumption.setText("0.0");
            }
            if (mealNode.get(KeysForFirebase.MEAL_VEGGIES_QUANTITY) != null) {
                enter_veggies_consumption.setText(String.valueOf(doubleFromObject((Object) mealNode.get(KeysForFirebase.MEAL_VEGGIES_QUANTITY))));
            } else {
                enter_veggies_consumption.setText("0.0");
            }
        }
    }

    // button helper methods
    void wireViewButtons(View view) {

        delete_button = (Button) view.findViewById(R.id.delete_button);
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
        enter_date = (Button) view.findViewById(R.id.enter_date);
        enter_meal_description = (EditText) view.findViewById(R.id.enter_meal_description);
        enter_meal_comments = (EditText) view.findViewById(R.id.enter_meal_comments);
        enter_protein_consumption = (EditText) view.findViewById(R.id.enter_protein_consumption);
        enter_starch_consumption = (EditText) view.findViewById(R.id.enter_starch_consumption);
        enter_fat_consumption = (EditText) view.findViewById(R.id.enter_fat_consumption);
        enter_fruit_consumption = (EditText) view.findViewById(R.id.enter_fruit_consumption);
        enter_veggies_consumption = (EditText) view.findViewById(R.id.enter_veggies_consumption);
        // organize fields
        fields = organizeEditTextFields(view);
        editTexts = fields.values().toArray(new EditText[0]);
        // wire EditTexts to focus handlers
        // wire focus and touch events

        for (EditText field : editTexts) {
            field.setOnFocusChangeListener(this);
            field.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    field.setText("");
                    showDataEntryButtons();
                }
            });
            //  connect editor actions
            field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String keyed = dataFieldHasChanged(v);
                    captureDataChange(v, keyed);
                    return true;
                }
            });
        }

        // clear text on focus
/*
        enter_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_weight.setText("");
                showDataEntryButtons();
            }
        });
*/
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
                        String stringDayOfMonth = String.valueOf(dayOfMonth);
                        if (stringDayOfMonth.length() == 1) {
                            stringDayOfMonth = "0" + stringDayOfMonth;
                        }
                        String stringMonth = String.valueOf(month + 1);
                        if (stringMonth.length() == 1) {
                            stringMonth = "0" + stringMonth;
                        }
                        enter_date.setText(stringMonth + "/" + stringDayOfMonth + "/" + year);
                        keyed_enter_date = stringMonth + "/" + stringDayOfMonth + "/" + year;
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        firebase_enter_date = year + "-" + stringMonth + "-" + stringDayOfMonth;
                        try {
                            selectedDateFromDatePicker = df.parse(firebase_enter_date);
                        } catch (Exception e) {
                            selectedDateFromDatePicker = myCalendar.getTime();
                        }
                        isDataChanged = true;
                        initializeView();
                        datePickerDialog.hide();
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(enter_date.getWindowToken(), 0);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();
                isDataChanged = true;
            }
        });
    }

    void wireViewTextViewFields(View view) {
        // write only
        copyright = (TextView) view.findViewById(R.id.copyrightText);
        client_message = (TextView) view.findViewById(R.id.client_message);
        stats_limit = (TextView) view.findViewById(R.id.stats_limit);
        stats_today = (TextView) view.findViewById(R.id.stats_today);
        stats_total = (TextView) view.findViewById(R.id.stats_total);
        stats_header = (TextView) view.findViewById(R.id.stats_header);
    }

    ArrayList<CheckBox> wireCheckmarks(View view) {
        ArrayList<CheckBox> marks = new ArrayList<>();
        marks.add((CheckBox) view.findViewById(R.id.checkBoxWater1));
        marks.add((CheckBox) view.findViewById(R.id.checkBoxWater2));
        marks.add((CheckBox) view.findViewById(R.id.checkBoxWater3));
        marks.add((CheckBox) view.findViewById(R.id.checkBoxWater4));
        marks.add((CheckBox) view.findViewById(R.id.checkBoxWater5));
        marks.add((CheckBox) view.findViewById(R.id.checkBoxWater6));
        marks.add((CheckBox) view.findViewById(R.id.checkBoxWater7));
        marks.add((CheckBox) view.findViewById(R.id.checkBoxWater8));
        marks.add((CheckBox) view.findViewById(R.id.checkBoxSupplement1));
        marks.add((CheckBox) view.findViewById(R.id.checkBoxSupplement2));
        marks.add((CheckBox) view.findViewById(R.id.checkBoxSupplement3));
        marks.add((CheckBox) view.findViewById(R.id.checkBoxExercise));
        for (int i = 0; i < marks.size(); i++) {
            marks.get(i).setOnClickListener(this::onClick);
            marks.get(i).setChecked(false);
        }
        return marks;
    }

    void deleteDisplayedJournalEntries() {
        journalNode = new HashMap<>();
        mealContentsNode = new HashMap<>();
        updateJournalOnDate(firebase_enter_date, journalNode);
        updateMealOnDate(firebase_enter_date, mealContentsNode);
        model.setNodeUserData(userDataNode, (message) -> {
                    deleteFailure(message);
                },
                () -> {
                    deleteSuccess();
                });

    }


    void deleteFailure(String message) {
        client_message.setText(message);
    }

    void deleteSuccess() {
        initializeView();
    }


    void saveJournalEntries() {
        saveDataToModel();
    }

    void cancelJournalEntries() {
        initializeView();
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
            if (keyed_enter_what_happened_today != null)
                enter_what_happened_today.setText(keyed_enter_what_happened_today);
            if (keyed_enter_meal_description != null)
                enter_meal_description.setText(keyed_enter_meal_description);
            if (keyed_enter_fat_consumption != null)
                enter_fat_consumption.setText(keyed_enter_fat_consumption);
            if (keyed_enter_fruit_consumption != null)
                enter_fruit_consumption.setText(keyed_enter_fruit_consumption);
            if (keyed_enter_protein_consumption != null)
                enter_protein_consumption.setText(keyed_enter_protein_consumption);
            if (keyed_enter_starch_consumption != null)
                enter_starch_consumption.setText(keyed_enter_starch_consumption);
            if (keyed_enter_veggies_consumption != null)
                enter_veggies_consumption.setText(keyed_enter_veggies_consumption);
            if (keyed_enter_meal_comments != null)
                enter_meal_comments.setText(keyed_enter_meal_comments);
            showCheckboxes(track_water_checks, track_supplements_checks, track_exercise_check);
        }

    }

    String dataFieldHasChanged(TextView textView) {
        InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
        showDataEntryButtons();
        isDataChanged = true;
        return textView.getText().toString();
    }

    void postModelDataToView() {
        // add meal based on date and meal type
        Map<String, Object> meal = new HashMap<String, Object>();
        userDataNode = model.getSignedinUserDataNode();
        if (userDataNode.get(KeysForFirebase.NODE_MEAL_CONTENTS) == null) {
            // if no mealContents then set values to zero
            zeroMealConsumption();
        } else {
            mealContentsNode = (Map<String, Object>) userDataNode.get(KeysForFirebase.NODE_MEAL_CONTENTS);
            if (mealContentsNode.get(firebase_enter_date) == null) {
                zeroMealConsumption();
            } else {
                if (mealContentsNode.get(firebase_enter_date) == null) {
                    zeroMealConsumption();
                } else {
                    Map<String, Object> mealOnDate = (Map<String, Object>) mealContentsNode.get(firebase_enter_date);
                    if (mealOnDate.get(mealKey(mealSelected)) == null) {
                        zeroMealConsumption();
                    } else {
                        meal = (Map<String, Object>) mealOnDate.get(mealKey(mealSelected));
                        showMealQuantities(meal, enter_protein_consumption, KeysForFirebase.MEAL_PROTEIN_QUANTITY);
                        showMealQuantities(meal, enter_fat_consumption, KeysForFirebase.MEAL_FAT_QUANTITY);
                        showMealQuantities(meal, enter_fruit_consumption, KeysForFirebase.MEAL_FRUIT_QUANTITY);
                        showMealQuantities(meal, enter_starch_consumption, KeysForFirebase.MEAL_STARCH_QUANTITY);
                        showMealQuantities(meal, enter_veggies_consumption, KeysForFirebase.MEAL_VEGGIES_QUANTITY);
                    }
                }
            }
        }
        highlightMealSelected(mealSelected);

        if (userDataNode.get(KeysForFirebase.NODE_JOURNAL) == null) {
            // if no Journal then set values to default
            enter_weight.setText("0.0");
            enter_date.setText(Helpers.showToday());
            enter_what_happened_today.setText("");
            showCheckboxes(0, 0, 0);
        } else {
            // journal exists
            journalNode = (Map<String, Object>) userDataNode.get(KeysForFirebase.NODE_JOURNAL);
            // now check for individual fields
            if (journalNode.get(firebase_enter_date) == null) {
                enter_weight.setText("0.0");
                enter_date.setText(Helpers.showToday());
                enter_what_happened_today.setText("");
                showCheckboxes(0, 0, 0);
            } else {
                Map<String, Object> journalNodeContentsOnDate = (Map<String, Object>) journalNode.get(firebase_enter_date);
                enter_date.setText(firebase_enter_date);
                if (journalNodeContentsOnDate.get(KeysForFirebase.WEIGHED) == null) {
                    enter_weight.setText("0.0");
                } else {
                    enter_weight.setText(String.valueOf(doubleFromObject((Object) journalNodeContentsOnDate.get(KeysForFirebase.WEIGHED))));
                }
                if (journalNodeContentsOnDate.get(KeysForFirebase.NOTES) == null) {
                    enter_what_happened_today.setText("");
                } else {
                    enter_what_happened_today.setText((String) journalNodeContentsOnDate.get(KeysForFirebase.NOTES));
                }
                int countWater = 0;
                int countSupplements = 0;
                int countExercise = 0;
                if (journalNodeContentsOnDate.get(KeysForFirebase.GLASSES_OF_WATER) != null) {
                    countWater = (doubleFromObject((Object) journalNodeContentsOnDate.get(KeysForFirebase.GLASSES_OF_WATER))).intValue();
                }
                if (journalNodeContentsOnDate.get(KeysForFirebase.SUPPLEMENTS) != null) {
                    countSupplements = (doubleFromObject((Object) journalNodeContentsOnDate.get(KeysForFirebase.SUPPLEMENTS))).intValue();
                }
                if (journalNodeContentsOnDate.get(KeysForFirebase.EXERCISED) != null) {
                    countExercise = (doubleFromObject((Object) journalNodeContentsOnDate.get(KeysForFirebase.EXERCISED))).intValue();
                }
                showCheckboxes(countWater, countSupplements, countExercise);
            }
        }
    }

    void showMealQuantities(Map<String, Object> meal, EditText quantity, String key) {
        if (meal.get(key) == null) {
            quantity.setText("0.0");
        } else {
            quantity.setText(String.valueOf(meal.get(key)));
        }
    }

    void highlightMealSelected(HW_Enumerations.Meals meal) {
        lastMealSelected.setSelected(false);
        lastMealSelected.setBackgroundColor(Color.parseColor("#FFFFFF"));
        lastMealSelected.setTextColor(Color.parseColor("#4A2353"));
        switch (meal) {
            case BREAKFAST:
                breakfast_button.setSelected(true);
                lastMealSelected = breakfast_button;
                break;
            case MORNING_SNACK:
                morning_snack_button.setSelected(true);
                lastMealSelected = morning_snack_button;
                break;
            case LUNCH:
                lunch_button.setSelected(true);
                lastMealSelected = lunch_button;
                break;
            case AFTERNOON_SNACK:
                afternoon_snack_button.setSelected(true);
                lastMealSelected = afternoon_snack_button;
                break;
            case DINNER:
                dinner_button.setSelected(true);
                lastMealSelected = dinner_button;
            case EVENING_SNACK:
                evening_snack_button.setSelected(true);
                lastMealSelected = evening_snack_button;
                break;
            default:
                lastMealSelected.setSelected(true);
                Log.d(TAG, "highlightMealSelected: bad meal");
                break;
        }
        lastMealSelected.setSelected(true);
        lastMealSelected.setBackgroundColor(Color.parseColor("#4A2353"));
        lastMealSelected.setTextColor(Color.parseColor("#FFFFFF"));
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
        // update Journal node
        Map<String, Object> journalNodeContentsOnDate;
        if (userDataNode.get(KeysForFirebase.NODE_JOURNAL) == null) {
            journalNode = new HashMap<String, Object>();
            journalNodeContentsOnDate = new HashMap<String, Object>();
        } else {
            journalNode = (Map<String, Object>) userDataNode.get(KeysForFirebase.NODE_JOURNAL);
            if (journalNode.get(firebase_enter_date) == null) {
                journalNodeContentsOnDate = new HashMap<String, Object>();
            } else {
                journalNodeContentsOnDate = (Map<String, Object>) journalNode.get(firebase_enter_date);
            }
        }
        if (keyed_enter_weight != null)
            journalNodeContentsOnDate.put(KeysForFirebase.WEIGHED, Double.valueOf(keyed_enter_weight));
        if (keyed_enter_what_happened_today != null)
            journalNodeContentsOnDate.put(KeysForFirebase.NOTES, keyed_enter_what_happened_today);
        if (track_water_checks != -1)
            journalNodeContentsOnDate.put(KeysForFirebase.GLASSES_OF_WATER, Integer.valueOf(track_water_checks).doubleValue());
        if (track_supplements_checks != -1)
            journalNodeContentsOnDate.put(KeysForFirebase.SUPPLEMENTS, Integer.valueOf(track_supplements_checks).doubleValue());
        if (track_exercise_check != -1)
            journalNodeContentsOnDate.put(KeysForFirebase.EXERCISED, Integer.valueOf(track_exercise_check).doubleValue());
        journalNode.put(firebase_enter_date, journalNodeContentsOnDate);
        userDataNode.put(KeysForFirebase.NODE_JOURNAL, journalNode);
        // update meaContents node
        Map<String, Object> mealContentsNodeContentsOnDate;
        Map<String, Object> mealNode;
        if (userDataNode.get(KeysForFirebase.NODE_MEAL_CONTENTS) == null) {
            mealContentsNode = new HashMap<String, Object>();
            mealContentsNodeContentsOnDate = new HashMap<String, Object>();
            mealNode = new HashMap<String, Object>();
        } else {
            mealContentsNode = (Map<String, Object>) userDataNode.get(KeysForFirebase.NODE_MEAL_CONTENTS);
            if (mealContentsNode.get(firebase_enter_date) == null) {
                mealContentsNodeContentsOnDate = new HashMap<String, Object>();
                mealNode = new HashMap<String, Object>();
            } else {
                mealContentsNodeContentsOnDate = (Map<String, Object>) mealContentsNode.get(firebase_enter_date);
                if (mealContentsNodeContentsOnDate.get(mealKey(mealSelected)) == null) {
                    mealNode = new HashMap<String, Object>();
                } else {
                    mealNode = (Map<String, Object>) mealContentsNodeContentsOnDate.get(mealKey(mealSelected));
                }
            }
        }

        if (keyed_enter_protein_consumption != null)
            mealNode.put(KeysForFirebase.MEAL_PROTEIN_QUANTITY, Double.valueOf(keyed_enter_protein_consumption));
        if (keyed_enter_fat_consumption != null)
            mealNode.put(KeysForFirebase.MEAL_FAT_QUANTITY, Double.valueOf(keyed_enter_fat_consumption));
        if (keyed_enter_fruit_consumption != null)
            mealNode.put(KeysForFirebase.MEAL_FRUIT_QUANTITY, Double.valueOf(keyed_enter_fruit_consumption));
        if (keyed_enter_starch_consumption != null)
            mealNode.put(KeysForFirebase.MEAL_STARCH_QUANTITY, Double.valueOf(keyed_enter_starch_consumption));
        if (keyed_enter_veggies_consumption != null)
            mealNode.put(KeysForFirebase.MEAL_VEGGIES_QUANTITY, Double.valueOf(keyed_enter_veggies_consumption));
        if (keyed_enter_meal_description != null)
            mealNode.put(KeysForFirebase.MEAL_DESCRIPTION, keyed_enter_meal_description);
        if (keyed_enter_meal_comments != null)
            mealNode.put(KeysForFirebase.MEAL_COMMENTS, keyed_enter_meal_comments);
        mealContentsNodeContentsOnDate.put(mealKey(mealSelected), mealNode);
        mealContentsNode.put(firebase_enter_date, mealContentsNodeContentsOnDate);
        userDataNode.put(KeysForFirebase.NODE_MEAL_CONTENTS, mealContentsNode);
        // ready to update Firebase
        model.setNodeUserData(userDataNode, (message) -> {
                    writeFailure(message);
                },
                () -> {
                    writeSuccess();
                });
    }

    String mealKey(HW_Enumerations.Meals meal) {
        switch (meal) {
            case BREAKFAST:
                return KeysForFirebase.BREAKFAST_MEAL_KEY;
            case MORNING_SNACK:
                return KeysForFirebase.MORNING_SNACK_MEAL_KEY;
            case LUNCH:
                return KeysForFirebase.LUNCH_MEAL_KEY;
            case AFTERNOON_SNACK:
                return KeysForFirebase.AFTERNOON_SNACK_MEAL_KEY;
            case DINNER:
                return KeysForFirebase.DINNER_MEAL_KEY;
            case EVENING_SNACK:
                return KeysForFirebase.EVENING_SNACK_MEAL_KEY;
            default:
                return KeysForFirebase.BREAKFAST_MEAL_KEY;
        }
    }

    void writeFailure(String message) {
        client_message.setText(message);
    }

    void writeSuccess() {
        initializeView();
    }

    void initializeView() {
        isDataChanged = false;
        track_water_checks = -1;
        track_supplements_checks = -1;
        track_exercise_check = -1;
        keyed_enter_meal_comments = null;
        keyed_enter_meal_description = null;
        keyed_enter_what_happened_today = null;
        keyed_enter_veggies_consumption = null;
        keyed_enter_starch_consumption = null;
        keyed_enter_protein_consumption = null;
        keyed_enter_fruit_consumption = null;
        keyed_enter_fat_consumption = null;
        keyed_enter_weight = null;
        keyed_enter_date = null;
        // clear the view
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        // Check if no view has focus:
        View view = this.getView();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        postModelDataToView();
        hideDataEntryButtons();
    }

    void mailJournal() {

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
            userJournalContents = (Map<String, Object>) userDataNode.get(KeysForFirebase.NODE_JOURNAL);
        }
        if (userJournalContents.get(firebase_enter_date) == null) {
            userJournalContentsOnDate = new HashMap<String, Object>();
        } else {
            userJournalContentsOnDate = (Map<String, Object>) userJournalContents.get(date);
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
            userJournalContents = (Map<String, Object>) userDataNode.get(KeysForFirebase.NODE_JOURNAL);
        }
        if (userJournalContents.get(date) == null) {
            userJournalContentsOnDate = new HashMap<String, Object>();
        } else {
            userJournalContentsOnDate = (Map<String, Object>) userJournalContents.get(date);
        }

        // deletion checks
        if (node.size() == 0) {
            if (userJournalContentsOnDate.size() == 0) {
                return; // no node to remove
            } else {
                userJournalContents.put(date, null);
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
            userMealContentsOnDate = (Map<String, Object>) userMealContents.get(date);
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

    // organize EditText fields for less coding
    Map<String, EditText> organizeEditTextFields(View v) {
        Map<String, EditText> etMap = new HashMap<>();
        etMap.put(String.valueOf(R.id.enter_weight), enter_weight);
        etMap.put(String.valueOf(R.id.enter_what_happened_today), enter_what_happened_today);
        etMap.put(String.valueOf(R.id.enter_meal_description), enter_meal_description);
        etMap.put(String.valueOf(R.id.enter_meal_comments), enter_meal_comments);
        etMap.put(String.valueOf(R.id.enter_protein_consumption), enter_protein_consumption);
        etMap.put(String.valueOf(R.id.enter_fat_consumption), enter_fat_consumption);
        etMap.put(String.valueOf(R.id.enter_fruit_consumption), enter_fruit_consumption);
        etMap.put(String.valueOf(R.id.enter_starch_consumption), enter_starch_consumption);
        etMap.put(String.valueOf(R.id.enter_veggies_consumption), enter_veggies_consumption);
        return etMap;
    }


    // build and display totals
    void displayTotals() {
        // assumes userData node loaded

        // BUILD TOTALS CHART
        // header and column 1 build
        double amount;
        stats_header.setText("          Pr   Fa  Str   Fr  Veg "); // hard coded spacing. Beware!
        String daily = "Daily ";
        String today = "Today ";
        String total = "Total ";
        SpannableStringBuilder negatives = null;
        double[] limits = new double[5];
        if (userDataNode == null) {
            daily = daily + formatTotals(limits);
            today = today + formatTotals(limits);
            total = total + formatTotals(limits);
        } else {
            // daily limits
            if (userDataNode.get(KeysForFirebase.NODE_SETTINGS) == null) {
                daily = daily + formatTotals(limits);
            } else {
                Map<String, Object> settingsNode = (Map<String, Object>) userDataNode.get(KeysForFirebase.NODE_SETTINGS);
                if (settingsNode.get(KeysForFirebase.LIMIT_PROTEIN_LOW) != null) {
                    limits[0] = doubleFromObject((Object) settingsNode.get(KeysForFirebase.LIMIT_PROTEIN_LOW));
                }
                if (settingsNode.get(KeysForFirebase.LIMIT_FAT) != null) {
                    limits[1] = doubleFromObject((Object) settingsNode.get(KeysForFirebase.LIMIT_FAT));
                }
                if (settingsNode.get(KeysForFirebase.LIMIT_STARCH) != null) {
                    limits[2] = doubleFromObject((Object) settingsNode.get(KeysForFirebase.LIMIT_STARCH));
                }
                if (settingsNode.get(KeysForFirebase.LIMIT_FRUIT) != null) {
                    limits[3] = doubleFromObject((Object) settingsNode.get(KeysForFirebase.LIMIT_FRUIT));
                }
                limits[4] = 3.0; // veggies hard coded limit
                daily += formatTotals(limits);
            }
            // today consumption
            double[] todayTotals = new double[5];
            if (userDataNode.get(KeysForFirebase.NODE_MEAL_CONTENTS) == null) {
                today = today + formatTotals(todayTotals);
            } else {
                Map<String, Object> mealContents = (Map<String, Object>) userDataNode.get(KeysForFirebase.NODE_MEAL_CONTENTS);
                if (mealContents.get(firebase_enter_date) == null) {
                    today = today + formatTotals(todayTotals);
                } else {
                    Map<String, Object> mealContentsContentsOnDate = (Map<String, Object>) mealContents.get(firebase_enter_date);
                    for (Map.Entry<String, Object> aMeal : mealContentsContentsOnDate.entrySet()) {
                        Map<String, Object> mealDetails = (Map<String, Object>) aMeal.getValue();
                        for (Map.Entry<String, Object> aComponent : mealDetails.entrySet()) {
                            int component = HW_FoodValues.FoodValues.valueOf(aComponent.getKey()).ordinal();
                            if (component < HW_FoodValues.FoodValuesComments) {
                                amount = doubleFromObject((Object) aComponent.getValue());
                            } else {
                                amount = 0.0;
                            }
                            switch (component) {
                                case HW_FoodValues.FoodValueProtein:
                                    todayTotals[0] += amount;
                                    break;
                                case HW_FoodValues.FoodValueFat:
                                    todayTotals[1] += amount;
                                    break;
                                case HW_FoodValues.FoodValueStarch:
                                    todayTotals[2] += amount;
                                    break;
                                case HW_FoodValues.FoodValueFruit:
                                    todayTotals[3] += amount;
                                    break;
                                case HW_FoodValues.FoodValueVeggies:
                                    todayTotals[4] += amount;
                                    break;
                            }
                        }
                    }
                    today += formatTotals(todayTotals);
                }
            }
            double[] totals = new double[5];
            for (int i = 0; i < limits.length; i++) {
                totals[i] = limits[i] - todayTotals[i];
            }
            total += formatTotals(totals);
            negatives = formatTotalsWithWarning(total, totals);
        }
        stats_limit.setText(daily);
        stats_today.setText(today);
        if (negatives == null) {
            stats_total.setText(total);
        } else {
            stats_total.setText(negatives);
        }


    }


    String formatTotals(double[] totals) {
        // String strDouble = String.format("%.2f", 1.23456)
        String count;
        String line = "";
        for (int i = 0; i < totals.length; i++) {
            count = "     " + (String.format("%5.2f", totals[i])); // count must be a length of 5
            if (count.length() != 5) {
                count = count.substring(count.length() - 5);
            }
            line += count;
        }
        return line;
    }

    SpannableStringBuilder formatTotalsWithWarning(String line, double[] totals) {
        SpannableStringBuilder sb = new SpannableStringBuilder(line);


        for (int i = 1; i < 4; i++) {
            if (totals[i] < 0.0) {
                sb.setSpan(new ForegroundColorSpan(Color.RED), 6 + 5 * i, 11 + 5 * i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        if (sb.length() == 0) {
            sb = null;
        }
        return sb;
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
