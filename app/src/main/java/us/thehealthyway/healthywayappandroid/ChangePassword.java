package us.thehealthyway.healthywayappandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static us.thehealthyway.healthywayappandroid.AppData.DEBUG;



public class ChangePassword extends AppCompatActivity {
    private static final String TAG = "HW.ChangePassword";    // UI references
    private EditText client_old_password;
    private EditText client_new_password;
    private EditText client_confirm_password;
    private TextView client_message;
    private TextView copyright;
    private Button change_password_button;
    private Button cancel_button;
    // Model
    private Model model;

    // keyed data
    private String clientOldPasswordlKeyed;
    private String clientNewPasswordKeyed;
    private String clientConfirmPasswordKeyed;

    // temp data
    private String clientEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Log.d(TAG, "onCreate: ChangePassword Activity debugging");
        setContentView(R.layout.activity_change_password);
        // find Model Controller
        model = Model.getInstance();
        client_message = (TextView) findViewById(R.id.client_message);
        client_old_password = (EditText) findViewById(R.id.client_old_password);
        client_new_password = (EditText) findViewById(R.id.client_new_password);
        client_confirm_password = (EditText) findViewById(R.id.client_confirm_password);
        change_password_button = (Button) findViewById(R.id.change_password_button);
        cancel_button = (Button) findViewById(R.id.cancel_button);
        // wire button listeners
        change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientOldPasswordlKeyed = client_old_password.getText().toString();
                clientNewPasswordKeyed = client_new_password.getText().toString();
                clientConfirmPasswordKeyed = client_confirm_password.getText().toString();
                changePassword();
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequested();
            }
        });

        copyright = findViewById(R.id.copyrightText);
        copyright.setText(Model.makeCopyRight());
        client_message = findViewById(R.id.client_message);
        client_message.setText(""); // clear message
    }


    public  static Intent makeIntent(Context context){
        return new Intent(context, ChangePassword.class);
    }

    void changePassword(){
        if (DEBUG) {
            Log.d(TAG, "changePassword: Enter");
        }
        // add change password logic
        client_message.setText("");
        if (!(clientNewPasswordKeyed.equals(clientConfirmPasswordKeyed))) {
            client_message.setText("Passwords mismatched. Try again");
            return;
        }
        if (model.getSignedInUID() == null) {
            client_message.setText("You must be signed in");
            return;
        } else {
            clientEmail = model.getSignedInEmail();
            model.signoutHandler((message)->{authErrorMessage(message);},
                    ()->{loginclient();});
        }
        // route to client view
        Intent intent = new Intent();
        intent.putExtra(HealthyWayAppActivities.HealthyWayViews.VIEW_CHANGE_PASSWORD_ACTIVITY.getName(),
                HealthyWayAppActivities.MASTER);
        setResult(Activity.RESULT_OK, intent);
        if (DEBUG) {
            Log.d(TAG, "changePassword: Exit");
        }
        finish();
    }

    public void authErrorMessage(String message) {
        client_message.setText(message);
    }

    public void loginclient() {
        // verify client's identity
        model.loginUser(clientEmail, clientOldPasswordlKeyed,
                (message)->{authErrorMessage(message);},
                ()->{updateclientPassword();});
    }

    public void updateclientPassword() {
        // make password change
        model.updatePassword(clientNewPasswordKeyed,
                (message)->{authErrorMessage(message);},
                ()->{changedPasswordSuccessfully();});
    }

    public void changedPasswordSuccessfully() {
        client_message.setText("Password change succeeded");
        // return to caller
        Intent intent = new Intent();
        intent.putExtra(HealthyWayAppActivities.HealthyWayViews.VIEW_CHANGE_PASSWORD_ACTIVITY.getName(),
                HealthyWayAppActivities.SETTINGS);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    void cancelRequested() {
        if (DEBUG) {
            Log.d(TAG, "cancelRequested: Enter");
        }
        // return to caller
        Intent intent = new Intent();
        intent.putExtra(HealthyWayAppActivities.HealthyWayViews.VIEW_CHANGE_PASSWORD_ACTIVITY.getName(),
                HealthyWayAppActivities.SETTINGS);
        setResult(Activity.RESULT_OK, intent);
        if (DEBUG) {
            Log.d(TAG, "cancelRequested: Exit");
        }
        finish();
    }



}
