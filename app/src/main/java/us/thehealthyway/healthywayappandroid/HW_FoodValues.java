package us.thehealthyway.healthywayappandroid;

public class HW_FoodValues {



    enum FoodValues {
        MEAL_FAT_QUANTITY("FAT"),
        MEAL_FRUIT_QUANTITY("FRUIT"),
        MEAL_PROTEIN_QUANTITY("PROTEIN"),
        MEAL_STARCH_QUANTITY("STARCH"),
        MEAL_VEGGIES_QUANTITY("VEGGIES"),
        MEAL_COMMENTS("COMMENTS"),
        MEAL_DESCRIPTION("DESCRIPTION");

        private String stringName;

        FoodValues (String name) {this.stringName = name; }

        String getName() {return stringName;};

    }
    /*
        Singleton model
     */
    private static HW_FoodValues instance;


    public static final int FoodValueFat = 0;
    public static final int FoodValueFruit = 1;
    public static final int FoodValueProtein = 2;
    public static final int FoodValueStarch = 3;
    public static final int FoodValueVeggies= 4;
    public static final int FoodValuesComments = 5;
    public static final int FoodValuesDescription = 6;


    private HW_FoodValues() {
        // throw new AssertionError();
    }

    public static HW_FoodValues getInstance() {
        if (instance == null) {
            instance = new HW_FoodValues();
        }
        return instance;
    }
    
    
}
