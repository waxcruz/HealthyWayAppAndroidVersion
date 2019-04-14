package us.thehealthyway.healthywayappandroid;

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
}
