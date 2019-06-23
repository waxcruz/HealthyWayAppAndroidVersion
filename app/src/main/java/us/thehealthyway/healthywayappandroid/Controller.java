package us.thehealthyway.healthywayappandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
// Application Singleton Model
import com.google.firebase.FirebaseApp;

import java.util.Map;

import static android.view.View.INVISIBLE;

import static us.thehealthyway.healthywayappandroid.AppData.DEBUG;

public class Controller extends AppCompatActivity {

    private static final String TAG = "HW.Controller";
    private Context context;
    private HealthyWayAppActivities.HealthyWayViews viewControllers[]; // activities
    private Model model;
    private ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        context = this.context;
        viewControllers = HealthyWayAppActivities.HealthyWayViews.values(); // dummy initialization of enum
        TextView copyright = findViewById(R.id.copyrightText);
        progress = findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);
        FirebaseApp.initializeApp(context);
        if (DEBUG) Log.d(TAG, "onCreate");
        model = Model.getInstance();
        copyright.setText(Model.makeCopyRight());
        model.startModel((message)-> {failureStartingModel(message); }, ()-> {successStartingModel(); });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        int routeToView = 0;
        Bundle routingInfo;
        switch (requestCode) {
            case HealthyWayAppActivities.CONTROLLER:
                if (resultCode == Activity.RESULT_OK) {
                    if (DEBUG) Log.d(TAG, "Returned from Controller, somethings wrong!" );
                    routingInfo = data.getExtras();
                    if (routingInfo != null) {
                        routeToView = (int) routingInfo.get(HealthyWayAppActivities.HealthyWayViews.VIEW_CONTROLLER.getName());
                    }
                } else {
                    if (DEBUG) Log.d(TAG, "Returned from Controller with cancel");
                    routeToView = HealthyWayAppActivities.CONTROLLER;
                }

                break;
            case HealthyWayAppActivities.CHANGE_PASSWORD_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s", HealthyWayAppActivities.HealthyWayViews.VIEW_CHANGE_PASSWORD_ACTIVITY.getName()));
                    routingInfo = data.getExtras();
                    if (routingInfo != null) {
                        routeToView = (int) routingInfo.get(HealthyWayAppActivities.HealthyWayViews.VIEW_CHANGE_PASSWORD_ACTIVITY.getName());
                    }
                } else {
                    if (DEBUG) Log.d(TAG, "Returned from Controller with cancel");
                    routeToView = HealthyWayAppActivities.SETTINGS;
                }
                break;
            case HealthyWayAppActivities.MASTER:
                if (resultCode == Activity.RESULT_OK) {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s", HealthyWayAppActivities.HealthyWayViews.VIEW_MASTER.getName()));
                    routingInfo = data.getExtras();
                    if (routingInfo != null) {
                        routeToView = (int) routingInfo.get(HealthyWayAppActivities.HealthyWayViews.VIEW_MASTER.getName());
                    }
                } else {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s with Cancel", HealthyWayAppActivities.HealthyWayViews.VIEW_MASTER.getName()));
                    routeToView = HealthyWayAppActivities.MASTER;
                }
                break;
            case HealthyWayAppActivities.CREATE_NEW_ACCOUNT:
                if (resultCode == Activity.RESULT_OK) {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s", HealthyWayAppActivities.HealthyWayViews.VIEW_CREATE_NEW_ACCOUNT.getName()));
                    routingInfo = data.getExtras();
                    if (routingInfo != null) {
                        routeToView = (int) routingInfo.get(HealthyWayAppActivities.HealthyWayViews.VIEW_CREATE_NEW_ACCOUNT.getName());
                    }
                } else {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s with Cancel", HealthyWayAppActivities.HealthyWayViews.VIEW_CREATE_NEW_ACCOUNT.getName()));
                    routeToView = HealthyWayAppActivities.LOGIN_ACTIVITY;
                }

                break;
            case HealthyWayAppActivities.FORGOTTEN_PASSWORD:
                if (resultCode == Activity.RESULT_OK) {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s", HealthyWayAppActivities.HealthyWayViews.VIEW_FORGOTTEN_PASSWORD.getName()));
                    routingInfo = data.getExtras();
                    if (routingInfo != null) {
                        routeToView = (int) routingInfo.get(HealthyWayAppActivities.HealthyWayViews.VIEW_FORGOTTEN_PASSWORD.getName());
                    }
                } else {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s with Cancel", HealthyWayAppActivities.HealthyWayViews.VIEW_FORGOTTEN_PASSWORD.getName()));
                    routeToView = HealthyWayAppActivities.LOGIN_ACTIVITY;
                }
                break;
            case HealthyWayAppActivities.LOADING_PLEASE_WAIT:
                if (resultCode == Activity.RESULT_OK) {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s", HealthyWayAppActivities.HealthyWayViews.VIEW_LOADING_PLEASE_WAIT.getName()));
                    routingInfo = data.getExtras();
                    if (routingInfo != null) {
                        routeToView = (int) routingInfo.get(HealthyWayAppActivities.HealthyWayViews.VIEW_LOADING_PLEASE_WAIT.getName());
                    }
                } else {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s with Cancel", HealthyWayAppActivities.HealthyWayViews.VIEW_LOADING_PLEASE_WAIT.getName()));
                    routeToView = HealthyWayAppActivities.LOADING_PLEASE_WAIT;
                }
                break;
            case HealthyWayAppActivities.LOGIN_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s", HealthyWayAppActivities.HealthyWayViews.VIEW_LOGIN_ACTIVITY.getName()));
                    routingInfo = data.getExtras();
                    if (routingInfo != null) {
                        routeToView = (int) routingInfo.get(HealthyWayAppActivities.HealthyWayViews.VIEW_LOGIN_ACTIVITY.getName());
                    }
                } else {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s with Cancel", HealthyWayAppActivities.HealthyWayViews.VIEW_LOGIN_ACTIVITY.getName()));
                    routeToView = HealthyWayAppActivities.LOGIN_ACTIVITY;
                }
                break;
            case HealthyWayAppActivities.SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s", HealthyWayAppActivities.HealthyWayViews.VIEW_SETTINGS.getName()));
                    routingInfo = data.getExtras();
                    if (routingInfo != null) {
                        routeToView = (int) routingInfo.get(HealthyWayAppActivities.HealthyWayViews.VIEW_SETTINGS.getName());
                    }
                } else {
                    if (DEBUG) Log.d(TAG, String.format("Returned from %s with Cancel", HealthyWayAppActivities.HealthyWayViews.VIEW_SETTINGS.getName()));
                    routeToView = HealthyWayAppActivities.MASTER;
                }
                break;
            default:
                Log.e(TAG, "Bad requestCode from onActivityResult" + requestCode);
                routeToView = HealthyWayAppActivities.CONTROLLER;
                break;
        }
        switchActivities(routeToView); // switch to next activity
    }

    protected void switchActivities(int segue){
        Intent intent;
        switch (segue) {
            case HealthyWayAppActivities.CHANGE_PASSWORD_ACTIVITY:
                intent = ChangePassword.makeIntent(Controller.this);
                startActivityForResult(intent, HealthyWayAppActivities.CHANGE_PASSWORD_ACTIVITY);
                break;
            case HealthyWayAppActivities.CONTROLLER:
                intent = Controller.makeIntent(Controller.this);
                startActivityForResult(intent, HealthyWayAppActivities.CONTROLLER);
                break;
            case HealthyWayAppActivities.CREATE_NEW_ACCOUNT:
                intent = CreateNewAccount.makeIntent(Controller.this);
                startActivityForResult(intent, HealthyWayAppActivities.CREATE_NEW_ACCOUNT);
                break;
            case HealthyWayAppActivities.FORGOTTEN_PASSWORD:
                intent = ForgottenPassword.makeIntent(Controller.this);
                startActivityForResult(intent, HealthyWayAppActivities.FORGOTTEN_PASSWORD);
                break;
            case HealthyWayAppActivities.LOGIN_ACTIVITY:
                intent = LoginActivity.makeIntent(Controller.this);
                startActivityForResult(intent, HealthyWayAppActivities.LOGIN_ACTIVITY);
                break;
            case HealthyWayAppActivities.MASTER:
                intent = MasterActivity.makeIntent(Controller.this);
                startActivityForResult(intent, HealthyWayAppActivities.MASTER);
                break;

            default:
                Log.e(TAG, "Bad segue from switchActivities" + segue);
                break;
        }
    }



    // my Firebase handlers

    public void successStartingModel(){
        progress.setVisibility(INVISIBLE);
        if (DEBUG) {
            Log.d(TAG, "is Admin signed in?: " + model.getIsAdminSignedIn());
            Log.d(TAG, "UID: " + model.getSignedInUID());
            Log.d(TAG, "Email: " + model.getSignedInEmail());
        }

        switchActivities(HealthyWayAppActivities.LOGIN_ACTIVITY);

    }

    public void failureStartingModel(String errorMessage) {
        progress.setVisibility(INVISIBLE);
        if (DEBUG) {
            Log.d(TAG, errorMessage);

        }
    }

    public void successCreateAdmin(){
        if (DEBUG) {
            Log.d(TAG, "success resetting password");
        }
    }

    public void failureCreateAdmin(String errorMessage) {
        if (DEBUG) {
            Log.d(TAG, "failure updating password");
        }
        Log.e(TAG, "failure in password reset" + errorMessage);
    }

    public void successGetEmailsNode(){
        if (DEBUG) {
            Log.d(TAG, "success getting emails node");
        }
    }

    public void failureGetEmailsNode(String errorMessage) {
        if (DEBUG) {
            Log.d(TAG, errorMessage);
        }
    }

    public void successPasswordReset(){
        if (DEBUG) {
            Log.d(TAG, "success resetting password");
        }
    }

    public void failurePasswordReset(String errorMessage) {
        if (DEBUG) {
            Log.d(TAG, "failure resettng password");
        }
        Log.e(TAG, "failure in password reset" + errorMessage);
    }


    public void successPasswordUpdate(){
        if (DEBUG) {
            Log.d(TAG, "success resetting password");
        }
    }

    public void failurePasswordUpdate(String errorMessage) {
        if (DEBUG) {
            Log.d(TAG, "failure updating password");
        }
        Log.e(TAG, "failure in password reset" + errorMessage);
    }


    public void successCreateEmail(){
        if (DEBUG) {
            Log.d(TAG, "success creating email");
        }
    }

    public void failureCreateEmail(String errorMessage) {
        if (DEBUG) {
            Log.d(TAG, "failure creating email" + errorMessage);
        }
        Log.e(TAG, "failure creating email");
    }

    public void successCreateUser(){
        if (DEBUG) {
            Log.d(TAG, "success creating user");
        }
    }

    public void failureCreateUser(String errorMessage) {
        if (DEBUG) {
            Log.d(TAG, "failure creating user" + errorMessage);
        }
        Log.e(TAG, "failure creating user");
    }

    public void successGetUserDataNode(){
        if (DEBUG) {
            Log.d(TAG, "success getting user data node");
        }
    }

    public void failureGetUserDataNode(String errorMessage) {
        if (DEBUG) {
            Log.d(TAG, "failure getting  user data node" + errorMessage);
        }
        Log.e(TAG, "failure getting user data node");
    }


    public void connectionStatus(String status) {
        if (DEBUG) {
            Log.d(TAG, "Connection status is " + status);
        }
    }

    public  static Intent makeIntent(Context context){
        return new Intent(context, Controller.class);
    }









}
