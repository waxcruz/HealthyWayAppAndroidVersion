package us.thehealthyway.healthywayappandroid;

import android.text.TextUtils;

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

    public static String showToday() {
        String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
        return date;
    }

    public static String makeFirebaseDate(Date myDate) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(myDate);
        return date;
    }

    public static String makeDisplayDate(Date myDate) {
        String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(myDate);
        return date;
    }

    public static Double doubleFromObject(Object o) {
        Double d;
        if (o instanceof Long) {
            d = ((Long) o).doubleValue();
        } else {
            if (o instanceof Double) {
                d = (Double) o;
            } else {
                if (o instanceof String) {
                    d = Double.valueOf((String) o);
                } else {
                    d = 0.0;
                }
            }
        }
        return d;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


}

