package us.thehealthyway.healthywayappandroid;

public class ConstantsHTML {
    public static String LANDSCAPE = " (easier to read in landscape mode) ";
    public static String JOURNAL_DAY_HEADER =
            "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<title>Healthy Way</title>\n" +
                    "<style>\n" +
                    "table, th, td {\n" +
                    "border : 1px solid green ;\n" +
                    "border-collapse : collapse ;\n" +
                    "width : 100% ;\n" +
                    //                   "table-layout: fixed;\n" +
                    "}\n" +
                    ".seven {\n" +
                    "width : 7% ; font-size : 6px ;\n" +
                    "}\n" +
                    ".seventeen {\n" +
                    "width : 17% ; font-size : 6px ;\n" +
                    "}\n" +
                    ".eightteen {\n" +
                    "width : 18% ; font-size : 6px ;\n" +
                    "}\n" +
                    ".thirty {\n" +
                    "width : 30% ; font-size:6px ;\n" +
                    "}\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h2>Journal</h2>\n" +
                    "<h3>HW_EMAIL_INSTRUCTION</h3>\n"
            ;
    public static String JOURNAL_DAILY_TOTALS_ROW =
            "<p>HW_RECORDED_DATE</p>\n" +
                    "<table>\n" +
                    "<colgroup>\n" +
                    "<col class=\"seventeen\"/>\n" +
                    "<col class=\"thirty\"/>\n" +
                    "<col class=\"seven\"/>\n" +
                    "<col class=\"seven\"/>\n" +
                    "<col class=\"seven\"/>\n" +
                    "<col class=\"seven\"/>\n" +
                    "<col class=\"seven\"/>\n" +
                    "<col class=\"eightteen\"/>\n" +
                    "</colgroup>\n" +
                    "<tr>\n" +
                    "<th>Meal</th>\n" +
                    "<th>Food eaten</th>\n" +
                    "<th>P</th>\n" +
                    "<th>S</th>\n" +
                    "<th>V</th>\n" +
                    "<th>Fr</th>\n" +
                    "<th>F</th>\n" +
                    "<th>Feelings/Comments</th>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "<td>Daily Totals</td>\n" +
                    "<td> </td>\n" +
                    "<td>HW_DAILY_TOTAL_PROTEIN_VALUE</td>\n" +
                    "<td>HW_DAILY_TOTAL_STARCH_VALUE</td>\n" +
                    "<td>HW_DAILY_TOTAL_VEGGIES_VALUE</td>\n" +
                    "<td>HW_DAILY_TOTAL_FRUIT_VALUE</td>\n" +
                    "<td>HW_DAILY_TOTAL_FAT_VALUE</td>\n" +
                    "<td> </td>\n" +
                    "</tr>\n"
            ;
    public static String JOURNAL_MEAL_ROW =
            "<tr>\n" +
                    "<td>HW_MEAL_NAME</td>\n" +
                    "<td>HW_MEAL_CONTENTS_DESCRIPTION</td>\n" +
                    "<td>HW_MEAL_PROTEIN_COUNT</td>\n" +
                    "<td>HW_MEAL_STARCH_COUNT</td>\n" +
                    "<td>HW_MEAL_VEGGIES_COUNT</td>\n" +
                    "<td>HW_MEAL_FRUIT_COUNT</td>\n" +
                    "<td>HW_MEAL_FAT_COUNT</td>\n" +
                    "<td>HW_MEAL_COMMENTS</td>\n" +
                    "</tr>\n"
            ;
    public static String JOURNAL_DATE_TOTALS =
            "<tr>\n" +
                    "<td>Totals</td>\n" +
                    "<td> </td>\n" +
                    "<td>HW_DATE_TOTAL_PROTEIN</td>\n" +
                    "<td><font color=\"HW_TOTAL_STARCH_COLOR\">HW_DATE_TOTAL_STARCH</font></td>\n" +
                    "<td>HW_DATE_TOTAL_VEGGIES</td>\n" +
                    "<td><font color=\"HW_TOTAL_FRUIT_COLOR\">HW_DATE_TOTAL_FRUIT</font></td>\n" +
                    "<td><font color=\"HW_TOTAL_FAT_COLOR\">HW_DATE_TOTAL_FAT</font></td>\n" +
                    "<td> </td>\n" +
                    "</tr>\n" +
                    "</table>\n"
            ;
    public static String JOURNAL_DATE_STATS =
            "<font size=\"3\">     Water: HW_DATE_WATER_CHECKS Supplements: HW_DATE_SUPPLEMENTS_CHECKS Exercise: HW_DATE_EXERCISE_CHECKS </font>\n" +
                    "<p>\n"
            ;
    public static String JOURNAL_DATE_COMMENTS = "<font size=\"3\">HW_COMMENTS</font>";
    public static String JOURNAL_DATE_TRAILER =
            "</p>\n" +
                    "</body>\n" +
                    "</html>\n"
            ;
}
