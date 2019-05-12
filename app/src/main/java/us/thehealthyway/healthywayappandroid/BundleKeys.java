package us.thehealthyway.healthywayappandroid;

public class BundleKeys {


    enum BundleKeyValues {
        BundleKeyEmail("keyEmail"),
        BundleKeyPassword("keyPassword"),
        BundleKeyLimitProteinLow("keyLimitProteinLow"),
        BundleKeyLimitProteinHigh("keyLimitProteinHigh"),
        BundleKeyLimitStarch("keyLimitStarch"),
        BundleKeyLimitFat("keyLimitFat"),
        BundleKeyLimitFruits("keyLimitFruits"),
        BundleKeyLimitVeggies("keyLimitVeggies"),
        BundleKeyKeyingSettings("keyKeyingSettings");

        private String stringName;

        BundleKeyValues(String name) {
            this.stringName = name;
        }

        String getName() {return stringName;};
    }
    /*
        Singleton model
     */
    private static BundleKeys instance;


    public static final int BundleKeyEmail = 0;
    public static final int BundleKeyPassword = 1;
    public static final int BundleKeyLimitProteinLow = 2;
    public static final int BundleKeyLimitProteinHigh = 3;
    public static final int BundleKeyLimitStarch= 4;
    public static final int BundleKeyLimitFat = 5;
    public static final int BundleKeyFruits = 6;
    public static final int BundleKeyLimitVeggies = 7;
    public static final int BundleKeyKeyingSettings = 8;

    private BundleKeys() {
       // throw new AssertionError();
    }

    public static BundleKeys getInstance() {
        if (instance == null) {
            instance = new BundleKeys();
        }
        return instance;
    }

}
