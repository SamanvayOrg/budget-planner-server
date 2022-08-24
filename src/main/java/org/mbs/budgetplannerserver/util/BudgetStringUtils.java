package org.mbs.budgetplannerserver.util;

public class BudgetStringUtils {
    public static String getBudgetYearString(int year, int minus) {
        int actual = year - minus;
        int nextYear = actual + 1;
        return actual+"-"+String.valueOf(nextYear).substring(2)+" ";
    }
}
