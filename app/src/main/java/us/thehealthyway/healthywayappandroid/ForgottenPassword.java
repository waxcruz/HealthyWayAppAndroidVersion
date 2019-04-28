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


public class ForgottenPassword extends AppCompatActivity {
    private static final String TAG = "HW.ForgottenPassword";
    // UI References
    private EditText reset_password_staff_email;
    private Button change_password_button;
    private Button cancel_button;
    private TextView copyrightText;
    private TextView client_message;
    // model
    private Model model;
    // keyed strings
    private String resetPasswordStaffEmailKeyed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);
        // find model controller
        model = Model.getInstance();
        // setup reset form
        reset_password_staff_email = (EditText) findViewById(R.id.reset_password_staff_email);
        change_password_button = (Button) findViewById(R.id.change_password_button);
        cancel_button = (Button) findViewById(R.id.cancel_button);
        copyrightText = (TextView) findViewById(R.id.copyrightText);
        copyrightText.setText(Model.makeCopyRight());
        client_message = (TextView) findViewById(R.id.client_message);
        client_message.setText("Enter your account email address. You'll receive an email with instructions to reset your password. Once you finish the reset of your password, login again.");
        // wire listeners
        reset_password_staff_email.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                resetPasswordStaffEmailKeyed = v.getText().toString();
                hideMyKeyboard(v);
                return true;
            }
        });

        // wire buttons
        change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPasswordStaffEmailKeyed = reset_password_staff_email.getText().toString();
                resetPassword();
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequested();
            }
        });
    }


    public  static Intent makeIntent(Context context){
        return new Intent(context, ForgottenPassword.class);
    }

    void hideMyKeyboard(TextView v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    void resetPassword() {
        if (DEBUG) {
            Log.d(TAG, "resetPassword: Enter");
        }
        // add reset password code for Firebase
        client_message.setText("");
        model.signoutHandler((message)->{errorMessage(message);});
        model.passwordReset(resetPasswordStaffEmailKeyed,
                (message)->{errorMessage(message);},
                ()->{sendEmailInstructions();}
        );
        if (DEBUG) {
            Log.d(TAG, "resetPassword: Exit");
        }
    }

    public void sendEmailInstructions() {
        if (DEBUG) {
            Log.d(TAG, "sendEmailInstructions: Enter");
        }
        // return to login
        Intent intent = new Intent();
        intent.putExtra(HealthyWayAppActivities.HealthyWayViews.VIEW_FORGOTTEN_PASSWORD.getName(),
                HealthyWayAppActivities.LOGIN_ACTIVITY);
        setResult(Activity.RESULT_OK, intent);
        if (DEBUG) {
            Log.d(TAG, "sendEmailInstructions: Exit");
        }
        finish();
    }

    public void errorMessage(String message) {
        client_message.setText(message);
    }

    void cancelRequested() {
        if (DEBUG) {
            Log.d(TAG, "cancelRequested: Enter");
        }
        // return to login
        Intent intent = new Intent();
        intent.putExtra(HealthyWayAppActivities.HealthyWayViews.VIEW_FORGOTTEN_PASSWORD.getName(),
                HealthyWayAppActivities.LOGIN_ACTIVITY);
        setResult(Activity.RESULT_OK, intent);
        if (DEBUG) {
            Log.d(TAG, "cancelRequested: Exit");
        }
        finish();
    }



}
