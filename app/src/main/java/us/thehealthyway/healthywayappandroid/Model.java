package us.thehealthyway.healthywayappandroid;

import androidx.annotation.NonNull;

import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// My Android constants
import static us.thehealthyway.healthywayappandroid.AppData.TAG;
import static us.thehealthyway.healthywayappandroid.AppData.DEBUG;





interface SuccessHandler {
    void successful();
}

interface FailureHandler {
    void failure(String message);
}


interface StatusHandler {
    void status(String message);
}


public class Model {
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase healthywaysc;
    private DatabaseReference ref;
    private ValueEventListener valueListener;
    private ChildEventListener childListener;
    // Keys and content
    private String firebaseDateKey;
    private Map<String, Object> settingsInFirebase;
        public Map<String, Object> getSettingsInFirebase() {
            return settingsInFirebase;
        }
        public void setSettingsInFirebase(Map<String, Object> settingsInFirebase) {
            this.settingsInFirebase = settingsInFirebase;
        }
    private Map<String, Object> journalInFirebase;
        public Map<String, Object> getJournalInFirebase() {
            return journalInFirebase;
        }
        public void setJournalInFirebase(Map<String, Object> journalInFirebase) {
            this.journalInFirebase = journalInFirebase;
        }
    private Map<String, Object> mealContentsInFirebase;
        public Map<String, Object> getMealContentsInFirebase() {
        return mealContentsInFirebase;
    }
        public void setMealContentsInFirebase(Map<String, Object> mealContentsInFirebase) {
            this.mealContentsInFirebase = mealContentsInFirebase;
        }
    private Map<String, Object> emailsInFirebase;
        public Map<String, Object> getEmailsInFirebase() { return emailsInFirebase;};
        public void setEmailsInFirebase(Map<String, Object> emailsInFirebase) {
            this.emailsInFirebase = emailsInFirebase;
            Set<String> keys = emailsInFirebase.keySet();
            String [] keyValues = keys.toArray(new String[keys.size()]);
            setEmailsList(keyValues);
        }
    // master data here and firebase is the backup
    private Map<String, Object> signedinUserDataNode;
        public Map<String, Object> getSignedinUserDataNode() {

            return signedinUserDataNode;
        }
        public void setSignedinUserDataNode(Map<String, Object> signedinUserDataNode) {
            this.signedinUserDataNode = signedinUserDataNode;
        }
    private String signedinUserErrorMessage;
        public String getSignedinUserErrorMessage() {
            return signedinUserErrorMessage;
        }
        public void setSignedinUserErrorMessage(String signedinUserErrorMessage) {
            this.signedinUserErrorMessage = signedinUserErrorMessage;
        }
    private String emailsList[];
        public String[] getEmailsList() {
            return emailsList;
        }
        public void setEmailsList(String[] emailsList) {
            this.emailsList = emailsList;
        }
    // Firebase attributes
    private boolean isAdminSignedIn;
    private String signedInUID;
    private String signedInEmail;
    private String signedInError;


    /*
        Singleton model
     */
    private static Model instance;


    private Model() { }
    /*
        Private to prevent anyone else from instantiating the model
     */

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }


    void startModel(final FailureHandler failureHandler, final SuccessHandler successHandler) {
        healthywaysc = FirebaseDatabase.getInstance();
        ref = healthywaysc.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        signedInUID = null;
        signedInEmail = null;
        signedInError = null;
        successHandler.successful();


    }

    void stopModel() {

        ref.removeEventListener(childListener);
    }


    // Helper methods for Firebase

    void updateChildOfRecordInFirebase(String table, String recordID, String path, Object value) {
        String fullFirebasePath = table + "/" + recordID + path;
        ref.child(fullFirebasePath).setValue(value);
    }

    // Authentication

    void loginUser( String email, String password, final FailureHandler errorHandler, final SuccessHandler handler) {
        // Initialize Firebase Auth
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "onComplete");
                        if (task.isSuccessful()) {
                            Log.d(TAG,"Successful login");
                            isAdminSignedIn = true;
                            try {
                                signedInUID = mFirebaseAuth.getCurrentUser().getUid();
                                signedInEmail = mFirebaseAuth.getCurrentUser().getEmail();
                                signedInError = null;
                                handler.successful();
                            } catch (NullPointerException e) {
                                signedInUID = null;
                                signedInEmail = null;
                                signedInError = null;
                                handler.successful();
                            }

                        } else {
                            try {
                                Log.d(TAG, "Failed to sign in. Error is " + task.getException());
                                signedInError = task.getException().toString();
                                isAdminSignedIn = false;
                                errorHandler.failure("Failed to sign in. Error is " + task.getException().getMessage());
                            } catch (NullPointerException e) {
                                Log.d(TAG, "Failed to sign in. No error message returned");
                                signedInError = "Failed to sign in. No error message returned";
                                isAdminSignedIn = false;
                                errorHandler.failure(signedInError);
                            }
                        }
                    }
                });
    }

    void signoutHandler(final FailureHandler errorHandler) {
        try {
            if (mFirebaseAuth.getCurrentUser().getDisplayName() != null) {
                mFirebaseAuth.signOut();
                signedInUID = null;
                signedInEmail = null;
                signedInError = null;
            }
        } catch (NullPointerException e) {
            mFirebaseAuth.signOut();
            signedInUID = null;
            signedInEmail = null;
            signedInError = null;
        }

    }



    void signoutHandler(final FailureHandler errorHandler, final SuccessHandler handler) {
        try {
            if (mFirebaseAuth.getCurrentUser().getUid() != null) {
                mFirebaseAuth.signOut();
                signedInUID = null;
                signedInEmail = null;
                signedInError = null;
                handler.successful();
            } else {
                errorHandler.failure("No authenticated user found");
            }

        } catch (NullPointerException e) {
            mFirebaseAuth.signOut();
            signedInUID = null;
            signedInEmail = null;
            signedInError = null;
            errorHandler.failure("No authenticated user found");
        }
    }



    void passwordReset(String email, final FailureHandler errorHandler, final SuccessHandler handler) {
        mFirebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete in passwordReset");
                        if (task.isSuccessful()) {
                            handler.successful();
                        } else {
                            try {
                                errorHandler.failure("failure in password reset: " + task.getException().getLocalizedMessage());
                            } catch (NullPointerException e) {
                                errorHandler.failure("failure in password reset: no message available");
                            }
                        }
                    }
                });
    }

    void updatePassword(String newPassword, final FailureHandler errorHandler, final SuccessHandler handler) {
        try {
            mFirebaseAuth.getCurrentUser().updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (DEBUG) {
                                Log.d(TAG, "updatePassword completed");
                            }
                            if (task.isSuccessful()) {
                                if (DEBUG) {
                                    Log.d(TAG, "updatePassword successful");
                                }
                                handler.successful();
                            } else {
                                if (DEBUG) {
                                    Log.d(TAG, "updatePassowrd failed");
                                }
                                try {
                                    errorHandler.failure(task.getException().getMessage());
                                } catch (NullPointerException e) {
                                    errorHandler.failure("updatePassword was unsuccessful and no error message available");
                                }
                            }
                        }
                    });
        } catch (NullPointerException e) {
            errorHandler.failure("updatePassword return error with no message");
        }
    }


    void checkFirebaseConnected(final StatusHandler handler) {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        System.out.println("connected");
                    } else {
                        System.out.println("not connected");
                    }
                    handler.status(connected ? "connected" : "not connected");

                } catch (NullPointerException e) {
                    handler.status("Connected state not returned from Firebase");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }
    // Used by Healthy Way Admin app

    void getNodeOfClient(final FailureHandler errorHandler, final SuccessHandler handler) {
        // userData
        if (signedinUserDataNode != null) {
            signedinUserDataNode.clear();
        }
        signedinUserErrorMessage = "";
        // find user data
         try {
            // retrieve the client data
            DatabaseReference userDataRef = ref.child(KeysForFirebase.NODE_USERDATA).child(signedInUID);
            ValueEventListener userDataEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    signedinUserDataNode = (Map<String, Object>)dataSnapshot.getValue();
                    signedinUserErrorMessage = "";
                    handler.successful();
                    return;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    signedinUserDataNode = null;
                    signedinUserErrorMessage = "Encountered error searching for client data";
                    errorHandler.failure(signedinUserErrorMessage);
                    return;
                }
            };
            userDataRef.addListenerForSingleValueEvent(userDataEvent);

        } catch (NullPointerException e) {
            errorHandler.failure("Null exception condition in getNodeOfClient. Error condition: " + e.getLocalizedMessage());
        }
    }


    void createAuthUserNode(String email, String password, final FailureHandler errorHandler, final SuccessHandler handler) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()) {
                                signedInUID = task.getResult().getUser().getUid();
                                signedInEmail = task.getResult().getUser().getEmail();
                                if (signedInUID == null) {
                                    errorHandler.failure("Account creation failure");
                                } else {
                                    handler.successful();
                                }
                            } else {
                                errorHandler.failure(task.getException().getLocalizedMessage());
                            }
                        } catch (NullPointerException e) {
                            errorHandler.failure("Null exception condition in createAuthUserNode. Error condition: " + e.getLocalizedMessage());
                        }
                    }
                });
    }

    void createUserInUsersNode(String uid, String email, final FailureHandler errorHandler, final SuccessHandler handler) {
        // create admin user in users node
        Map<String, Object> newUser = new HashMap<String, Object>();
        newUser.put("email", email);
        newUser.put("isAdmin", true);
        DatabaseReference userRef = ref.child(KeysForFirebase.NODE_USERS).child(uid);
        userRef.setValue(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        handler.successful();
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        errorHandler.failure("Account creation failed for Users Node");
                    }
                });
    }

    void createEmailInEmailsNode(String uid, String email, final FailureHandler errorHandler, final SuccessHandler handler) {
        // create admin user in users node
        Map<String, Object> newEmail = new HashMap<String, Object>();
        newEmail.put("uid", uid);
        newEmail.put("isAdmin", true);
        String keyEmail = Helpers.makeFirebaseEmailKey(email);
        DatabaseReference emailsRef = ref.child(KeysForFirebase.NODE_EMAILS).child(keyEmail);
        emailsRef.setValue(newEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        handler.successful();
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        errorHandler.failure("Account creation failed for Email Node");
                    }
                });
    }


    // write client data to userData
    void setNodeUserData(Map<String, Object> node, final FailureHandler errorHandler,
                         final SuccessHandler handler) {
        DatabaseReference userDataRef = ref.child(KeysForFirebase.NODE_USERDATA).child(signedInUID);
        try {
            userDataRef.setValue(node)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        signedinUserDataNode = node;
                        signedinUserErrorMessage = "";
                        handler.successful();
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        errorHandler.failure("Failure to update client settings");
                    }
                });
        } catch (Exception e) {

        }

     }

    // read userData node of client
    void getNodeUserData(final FailureHandler errorHandler,
                         final SuccessHandler handler) {
        if (signedinUserDataNode.size() == 0) {
            DatabaseReference userDataRef = ref.child(KeysForFirebase.NODE_USERDATA).child(signedInUID);
            // find user data
            try {
                // retrieve the client data
                ValueEventListener userDataEvent = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        signedinUserDataNode = (Map<String, Object>) dataSnapshot.getValue();
                        signedinUserErrorMessage = "";
                        handler.successful();
                        return;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        signedinUserDataNode = null;
                        signedinUserErrorMessage = "Encountered error searching for client data";
                        errorHandler.failure(signedinUserErrorMessage);
                        return;
                    }
                };
                userDataRef.addListenerForSingleValueEvent(userDataEvent);

            } catch (NullPointerException e) {
                errorHandler.failure("Null exception condition in getNodeOfClient. Error condition: " + e.getLocalizedMessage());
            }

        } else {
            handler.successful();
        }
    }

    String getSignedInUID() {
        return signedInUID;
    }

    String getSignedInEmail() {
        return signedInEmail;
    }

    String getIsAdminSignedIn() {
        if (isAdminSignedIn)
            return "true";
        else
            return "false";
    }

    String getSignedInError() {
        return signedInError;
    }

    // create copyright string
    static String makeCopyRight() {
        Calendar calendar = Calendar.getInstance();
        return String.format("Copyright @ %s The Healthy Way of BUILD 1.4", calendar.get(Calendar.YEAR));
    }



}
