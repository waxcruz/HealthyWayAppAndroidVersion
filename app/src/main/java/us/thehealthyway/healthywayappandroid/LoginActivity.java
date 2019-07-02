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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "HW.LoginActivity";


    // UI references.
    private EditText clientEmail;
    private EditText clientPassword;
    private Button loginButton;
    private Button cancelButton;
    private Button createAccountButton;
    private Button forgottenPasswordButton;
    private TextView copyright;
    private TextView message;
    private Model model;
    private String clientEmailKeyed;
    private String clientPasswordKeyed;
    private InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // find Model Controller
        model = Model.getInstance();
        // Set up the login form.
        clientEmail = (EditText) findViewById(R.id.client_old_password);
        clientEmail.setText("");
        clientEmail.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                clientEmailKeyed = textView.getText().toString();
                InputMethodManager imm = (InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                return true;
            }

        });

//        clientEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    clientEmailKeyed = clientEmail.getText().toString();
//                }
//            }
//        });


        clientPassword = (EditText) findViewById(R.id.client_new_password);
        clientPassword.setText("");
        clientPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                clientPasswordKeyed = textView.getText().toString();
                InputMethodManager imm = (InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                return true;
            }
        });
//        clientPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    clientPasswordKeyed = clientPassword.getText().toString();
//                }
//            }
//        });
        loginButton = (Button) findViewById(R.id.change_password_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientEmailKeyed = clientEmail.getText().toString();
                clientPasswordKeyed = clientPassword.getText().toString();
                if (clientEmailKeyed.equals("")) {
                    message.setText("No email address entered.");
                    return;
                }
                if (clientPasswordKeyed.equals("")) {
                    message.setText("No password entered");
                    return;
                }

                attemptLogin();
            }
        });

        createAccountButton = (Button) findViewById(R.id.sign_out_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routeToCreateNewAccount();
            }
        });


        forgottenPasswordButton = (Button) findViewById(R.id.cancel_button);
        forgottenPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routeToForgottenPassword();
            }
        });


        copyright = findViewById(R.id.copyrightText);
        copyright.setText(Model.makeCopyRight());
        message = findViewById(R.id.client_message);
        message.setText(""); // clear message

    }






    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(BundleKeys.BundleKeyValues.BundleKeyEmail.getName(), clientEmailKeyed);
        outState.putString(BundleKeys.BundleKeyValues.BundleKeyPassword.getName(), clientPasswordKeyed);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        clientEmailKeyed = savedInstanceState.getString(BundleKeys.BundleKeyValues.BundleKeyEmail.getName());
        clientPasswordKeyed = savedInstanceState.getString(BundleKeys.BundleKeyValues.BundleKeyPassword.getName());
    }

    public  static Intent makeIntent(Context context){
        return new Intent(context, LoginActivity.class);
    }


    private void attemptLogin(){
        model.loginUser(clientEmailKeyed, clientPasswordKeyed, (message)-> {loginFailure(message); },
                ()-> {loginSuccess(); });
    }


    private void loginFailure(String message){
        if (DEBUG) {
            Log.d(TAG, "loginFailure: "+ message);
        }
        this.message.setText(message.toString());
    }


    private void loginSuccess(){
        if (DEBUG) {
            Log.d(TAG, "loginSuccess: " + clientEmailKeyed);
        }
        model.getNodeOfClient((message)->{failedReadOfClientData(message);},
                ()->{successfulReadOfClientData();}
                );
    }

    void failedReadOfClientData(String message) {
        this.message.setText("failedReadOfClientData: " + message);
    }

    void successfulReadOfClientData() {
        this.message.setText("found client");
        // route to Master Tab Controller because client logged into app
        Intent intent = new Intent();
        intent.putExtra(HealthyWayAppActivities.HealthyWayViews.VIEW_LOGIN_ACTIVITY.getName(),
                HealthyWayAppActivities.MASTER);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void routeToCreateNewAccount(){
        if (DEBUG) {
            Log.d(TAG, "routeToCreateNewAccount: button clicked");
        }
        // route to CreateNewAccount because client wants to create new client account
        Intent intent = new Intent();
        intent.putExtra(HealthyWayAppActivities.HealthyWayViews.VIEW_LOGIN_ACTIVITY.getName(),
                HealthyWayAppActivities.CREATE_NEW_ACCOUNT);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void routeToForgottenPassword(){
        if (DEBUG) {
            Log.d(TAG, "routeToForgottenPassword: button clicked");
        }
        // route to CreateNewAccount because client wants to create new client account
        Intent intent = new Intent();
        intent.putExtra(HealthyWayAppActivities.HealthyWayViews.VIEW_LOGIN_ACTIVITY.getName(),
                HealthyWayAppActivities.FORGOTTEN_PASSWORD);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
