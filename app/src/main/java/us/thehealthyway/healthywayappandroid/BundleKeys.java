package us.thehealthyway.healthywayappandroid;

public class BundleKeys {


    enum BundleKeyValues {
        BundleKeyEmail("keyEmail"),
        BundleKeyPassword("keyPassword");

        ;

        private String stringName;

        BundleKeyValues(String name) {
            this.stringName = name;
        }

        String getName() {return stringName;};
    }

    public static final int BundleKeyEmail = 0;
    public static final int BundleKeyPassword = 1;

    private BundleKeys() {
        throw new AssertionError();
    }



}
