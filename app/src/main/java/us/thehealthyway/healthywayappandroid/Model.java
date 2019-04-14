package us.thehealthyway.healthywayappandroid;

import android.content.Context;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Helper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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
import java.util.Date;
import java.util.Dictionary;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

// My Android constants
import static android.support.constraint.Constraints.TAG;
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
    private Map<String, Object> clientNode;
    public Map<String, Object> getClientNode() {
        return clientNode;
    }
    public void setClientNode(Map<String, Object> clientNode) {
        this.clientNode = clientNode;
    }
    private String clientErrorMessage;
    public String getClientErrorMessage() {
        return clientErrorMessage;
    }
    public void setClientErrorMessage(String clientErrorMessage) {
        this.clientErrorMessage = clientErrorMessage;
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

//        // Initialize Firebase Auth
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        mFirebaseAuth.signOut();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        if (mFirebaseUser == null) {
//            // Not signed in, launch the Sign In activity
//            mFirebaseAuth.signInWithEmailAndPassword("wmyronw@yahoo.com", "waxwax")
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            Log.d(TAG, "onComplete");
//                            if (task.isSuccessful()) {
//                                Log.d(TAG,"Successful login");
//                                isAdminSignedIn = true;
//                                signedInUID = mFirebaseAuth.getCurrentUser().getUid();
//                                signedInEmail = mFirebaseAuth.getCurrentUser().getEmail();
//                                healthywaysc = FirebaseDatabase.getInstance();
//                                ref = healthywaysc.getReference();
//                                successHandler.successful();
//                            } else {
//                                Log.d(TAG, "Failed to sign in. Error is " + task.getException());
//                                signedInError = task.getException().toString();
//                                isAdminSignedIn = false;
//                                failureHandler.failure("Failed to sign in. Error: " + task.getException().getLocalizedMessage());
//                            }
//                        }
//                    });
//        } else {
//            signedInUID = mFirebaseUser.getUid();
//            signedInEmail = mFirebaseUser.getEmail();
//            isAdminSignedIn = true;
//        }


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

    void getNodeOfClient(final String email, final FailureHandler errorHandler, final SuccessHandler handler) {
        // emails-->users-->userData
        if (clientNode != null) {
            clientNode.clear();
        }
        clientErrorMessage = "";
        // find the email in node emails
        String firebaseMail = Helpers.makeFirebaseEmailKey(email);
        DatabaseReference emailsRef = ref.child(KeysForFirebase.NODE_EMAILS).child(firebaseMail);
        ValueEventListener clientEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    final Map<String, Object> nodeEmailsValue = (Map<String, Object>) dataSnapshot.getValue();
                    if (nodeEmailsValue == null) {
                        clientErrorMessage = "No Client found with that email address";
                        errorHandler.failure(clientErrorMessage);
                        return;
                    }
                    final String  clientUID =  (String) nodeEmailsValue.get("uid");
                    if (clientUID == null) {
                        clientErrorMessage = "No Client found with that email address";
                        errorHandler.failure(clientErrorMessage);
                        return;
                    }
                    // use emails secondary key, UID, to verify client email is cross linked to users UID
                    DatabaseReference userRef = ref.child(KeysForFirebase.NODE_USERS).child(clientUID);
                    ValueEventListener userEvent = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String, Object> nodeUsersValue = (Map<String, Object>) dataSnapshot.getValue();
                            if (nodeEmailsValue == null) {
                                clientErrorMessage = "Encountered error searching for client UID";
                                errorHandler.failure(clientErrorMessage);
                                return;
                            }
                            String checkEmail = (String) nodeUsersValue.get("email");
                            if (checkEmail.equals(email)) {
                                // retrieve the client data
                                DatabaseReference userDataRef = ref.child(KeysForFirebase.NODE_USERDATA).child(clientUID);
                                ValueEventListener userDataEvent = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        clientNode = (Map<String, Object>) dataSnapshot.getValue();
                                        clientErrorMessage = "";
                                        handler.successful();
                                        return;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        clientNode = null;
                                        clientErrorMessage = "Encountered error searching for client data";
                                        errorHandler.failure(clientErrorMessage);
                                        return;
                                    }
                                };
                                userDataRef.addListenerForSingleValueEvent(userDataEvent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    userRef.addListenerForSingleValueEvent(userEvent);

                } catch (NullPointerException e) {
                    errorHandler.failure("Null exception condition in getNodeOfClient. Error condition: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        emailsRef.addListenerForSingleValueEvent(clientEvent);
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
    // list methods

    Map<String, Object> getNodeEmails(final FailureHandler failureHandler, final SuccessHandler successHandler) {
        DatabaseReference emailsNode = ref.child("emails");
        ValueEventListener emailsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    setEmailsInFirebase((Map<String, Object>) dataSnapshot.getValue());
                    successHandler.successful();
                } catch (NullPointerException e) {
                    failureHandler.failure("Null exception condition in createAuthUserNode. Error condition: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                failureHandler.failure("Database error reading emails node with message: " + databaseError.toString());
            }

        };
        emailsNode.addListenerForSingleValueEvent(emailsListener);

        return null;
    }

    public String [] getFullListOfClientEmails() {
        Set<String> keys = emailsInFirebase.keySet();
        String [] keyValues = keys.toArray(new String[keys.size()]);
        return keyValues;
    }

    // create copyright string
    static String makeCopyRight() {
        Calendar calendar = Calendar.getInstance();
        return String.format("Copyright @ %s The Healthy Way", calendar.get(Calendar.YEAR));
    }
}
