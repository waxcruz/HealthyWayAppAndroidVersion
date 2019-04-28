package us.thehealthyway.healthywayappandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static us.thehealthyway.healthywayappandroid.AppData.DEBUG;



public class CreateNewAccount extends AppCompatActivity {
    private static final String TAG = "HW.CreateNewAccount";    // UI References
    private EditText createMailAccount;
    private EditText createAccountPassword;
    private EditText createAccountPasswordConfirmation;
    private Button signinButton;
    private Button cancelButton;
    private TextView copyright;
    private TextView message;
    private Model model;
    private String createEmailKeyed;
    private String createAccountPasswordKeyed;
    private String createAccountPasswordConfirmationKeyed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        model = Model.getInstance();
        // setup create form
        createMailAccount = (EditText) findViewById(R.id.create_account_email);
        createAccountPassword = (EditText) findViewById(R.id.create_account_password);
        createAccountPasswordConfirmation = (EditText) findViewById(R.id.create_account_password_confirmation);
        signinButton = (Button) findViewById(R.id.sign_in_button);
        cancelButton = (Button)findViewById(R.id.cancel_button);
        // wire listeners
        createMailAccount.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                createEmailKeyed = v.getText().toString();
                hideMyKeyboard(v);
                return true;
            }
        });
        createAccountPassword.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                createAccountPasswordKeyed =v.getText().toString();
                hideMyKeyboard(v);
                return true;
            }
        });
        createAccountPasswordConfirmation.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                createAccountPasswordConfirmationKeyed = v.getText().toString();
                hideMyKeyboard(v);
                return true;
            }
        });
        // wire buttons
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEmailKeyed = createMailAccount.getText().toString();
                createAccountPasswordKeyed = createAccountPassword.getText().toString();
                createAccountPasswordConfirmationKeyed = createAccountPasswordConfirmation.getText().toString();
                createLogin();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequested();
            }
        });

        copyright = findViewById(R.id.copyrightText);
        copyright.setText(Model.makeCopyRight());
        message = findViewById(R.id.client_message);
        message.setText(""); // clear message
    }



    void createLogin(){
        if (DEBUG) {
            Log.d(TAG, "createLogin: Enter");
        }
        // add Firebase create account logic
        message.setText(""); // clear message
        model.signoutHandler((message)-> {failureSigningOutCurrentStaffMemeber(message); });
        createEmailKeyed = createMailAccount.getText().toString();
        createAccountPasswordKeyed = createAccountPassword.getText().toString();
        createAccountPasswordConfirmationKeyed = createAccountPasswordConfirmation.getText().toString();
        if (!(createAccountPasswordKeyed.equals(createAccountPasswordConfirmationKeyed))) {
            message.setText("Passwords mismatched. Try again");
            return;
        }
        model.createAuthUserNode(createEmailKeyed, createAccountPasswordKeyed,
                (message)-> {authErrorDisplay(message); },
                ()-> {creationOfClientSucceeded(); } );
        if (DEBUG) {
            Log.d(TAG, "createLogin: Exit");
        }
    }

    public void failureSigningOutCurrentStaffMemeber(String errorMessage) {
        if (DEBUG) {
            Log.d(TAG, "failure signing out the current logged staff member");
        }
        Log.e(TAG, "failureSigninOutCurrentStaffMember" + errorMessage);
        message.setText(errorMessage);
    }


    public void authErrorDisplay(String errorMessage) {
        if (DEBUG) {
            Log.d(TAG, "authErrorDisplay: creation of staff failed with error");
        }
        Log.e(TAG, "authErrorDisplay: " + errorMessage);
        message.setText(errorMessage);
    }


    public void databaseErrorDisplay(String errorMessage) {
        if (DEBUG) {
            Log.d(TAG, "databaseErrorDisplay: " + errorMessage);
        }
        Log.e(TAG, "databaseErrorDisplay: " + errorMessage);
        message.setText(errorMessage);
    }


    public void creationOfClientSucceeded() {
        message.setText("Client account created");
        createClientInUsersNode();
    }

    public  void createClientInUsersNode() {
        model.createUserInUsersNode(model.getSignedInUID(), createEmailKeyed,
                (message)-> {databaseErrorDisplay(message); },
                ()-> {createClientInEmailsNode(); }  );
    }


    public void createClientInEmailsNode() {
        message.setText("Client identity created");
        model.createEmailInEmailsNode(model.getSignedInUID(), createEmailKeyed,
                (message)-> { databaseErrorDisplay(message);},
                ()->{completedClientCreationInDatabase();});
    }


    public void completedClientCreationInDatabase() {
        message.setText("Client email created");
        if (DEBUG) {
            Log.d(TAG, "completedClientCreationInDatabase: Enter");
        }
        // route to client view
        Intent intent = new Intent();
        intent.putExtra(HealthyWayAppActivities.HealthyWayViews.VIEW_CREATE_NEW_ACCOUNT.getName(),
                HealthyWayAppActivities.MASTER);
        if (DEBUG) {
            Log.d(TAG, "completedClientCreationInDatabase: Exit");
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    void cancelRequested(){
        if (DEBUG) {
            Log.d(TAG, "cancelRequested: Enter");
        }
        // return to login
        Intent intent = new Intent();
        intent.putExtra(HealthyWayAppActivities.HealthyWayViews.VIEW_CREATE_NEW_ACCOUNT.getName(),
                HealthyWayAppActivities.LOGIN_ACTIVITY);
        if (DEBUG) {
            Log.d(TAG, "cancelRequested: Exit");
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    void hideMyKeyboard(TextView v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    public  static Intent makeIntent(Context context){
        return new Intent(context, CreateNewAccount.class);
    }


}
