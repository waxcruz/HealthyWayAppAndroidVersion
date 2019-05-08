package us.thehealthyway.healthywayappandroid;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Helpers {
    public Helpers() {
    }

    public static String makeFirebaseEmailKey(String email) {
        String firebaseEmail = email.replaceAll("[.]", ",");
        return firebaseEmail;
    }

    public static String restoreEmail(String firebaseEmailKey) {
        String validEmail = firebaseEmailKey.replaceAll("[,]", ".");
        return validEmail;
    }

    public static  String showToday() {
        String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
        return date;
    }

    public static  String makeFirebaseDate(Date myDate) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(myDate);
        return date;
    }

}
