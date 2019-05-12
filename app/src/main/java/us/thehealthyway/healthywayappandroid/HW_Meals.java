package us.thehealthyway.healthywayappandroid;

public class HW_Meals {


    enum MealsValues {
        BREAKFAST("Breakfast"),
        MORNING_SNACK("Morning Snack"),
        LUNCH("Lunch"),
        AFTERNOON_SNACK("Afternoon Snack"),
        DINNER("Dinner"),
        EVENING_SNACK("Evening Snack");

        private String stringName;

        MealsValues (String name) {this.stringName = name; }

        String getName() {return stringName;};

   }
    /*
        Singleton model
     */
    private static HW_Meals instance;


    public static final int MealsBreaksfast = 0;
    public static final int MealsMorningSnack = 1;
    public static final int MealsLimitLunch = 2;
    public static final int MealsLimitAfternoonSnack = 3;
    public static final int MealsLimitDinner= 4;
    public static final int MealsLimitEveningSnack = 5;

    private HW_Meals() {
        // throw new AssertionError();
    }

    public static HW_Meals getInstance() {
        if (instance == null) {
            instance = new HW_Meals();
        }
        return instance;
    }
}
