package us.thehealthyway.healthywayappandroid;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Provides access to single model
 */

// run before 1st activity & UI instantiated


public class AppData extends Application {
//  "singleton" members

    //  package name
    public static String packageName;
    public static final String TAG = "HW.AppData";
    //  enables (true) or disables (false) debug logcat messages
    public static final boolean DEBUG = true;  // reset to "false" for submission






    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.d(TAG, "onCreate entry");

        //  gets package name
        packageName = getPackageName();
        Log.i(TAG, "package: " + packageName);

        if (DEBUG) Log.d(TAG, "onCreate exit");
    }


}