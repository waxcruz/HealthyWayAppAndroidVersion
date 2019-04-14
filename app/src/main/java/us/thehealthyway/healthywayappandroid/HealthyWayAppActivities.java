package us.thehealthyway.healthywayappandroid;


import java.util.HashMap;
import java.util.Map;

public class HealthyWayAppActivities {


    enum HealthyWayViews {
        VIEW_ADMIN_ACTIVITY("AdminActivity"),
        VIEW_CHANGE_PASSWORD_ACTIVITY("ChangePassword"),
        VIEW_CLIENT_VIEW("ClientView"),
        VIEW_CREATE_NEW_ACCOUNT("CreateNewAccount"),
        VIEW_FORGOTTEN_PASSWORD("ForgottenPassword"),
        VIEW_LOADING_PLEASE_WAIT("LoadingPleaseWait"),
        VIEW_LOGIN_ACTIVITY("LoginActivity"),
        VIEW_SETTINGS("Settings"),
        PERMISSIONS_READ_STORAGE("PermissionsStorageRead"),
        PERMISSIONS_WRITE_STORAGE("PermissionsStorageWrite");


        private String stringName;


        HealthyWayViews(String name) {
            this.stringName = name;
        }

        String getName() {return stringName;};
    }

    public static final int ADMIN_ACTIVITY = 0;
    public static final int CHANGE_PASSWORD_ACTIVITY = 1;
    public static final int CLIENT_VIEW = 2;
    public static final int CREATE_NEW_ACCOUNT = 3;
    public static final int FORGOTTEN_PASSWORD = 4;
    public static final int LOADING_PLEASE_WAIT = 5;
    public static final int LOGIN_ACTIVITY = 6;
    public static final int SETTINGS = 7;
    public static final int PERMISSION_READ_STORAGE = 8;
    public static final int PERMISSION_WRITE_STORAGE = 9;






    private HealthyWayAppActivities() {
        throw new AssertionError();
    }

}
