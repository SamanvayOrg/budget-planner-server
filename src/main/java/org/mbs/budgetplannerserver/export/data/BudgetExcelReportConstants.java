package org.mbs.budgetplannerserver.export.data;

import java.util.Arrays;
import java.util.List;

public interface BudgetExcelReportConstants {

    int TITLE_ROW = 0;
    int SUB_TITLE_ROW = TITLE_ROW + 1;
    int HEADER_ROW = 3;
    int STARTING_COLUMN_NUM = 0;
    int OPENING_BALANCE_ROW = HEADER_ROW +1;
    int OPENING_BALANCE_START_NUM = STARTING_COLUMN_NUM + 3;
    int INT_CONSTANT_ZERO = 0;
    String TOTAL = " Total";
    String EMPTY_STRING = "";
    String REPLACE_PATTERN = "\\{(.*?)\\}";
    String YEAR_STRING = "yearString";
    String MINUS = "-";
    String TITLE_TEXT = " Budget Estimate of the Receipts and Payments for the financial year ";
    String SUB_TITLE_TEXT = "(Form No. 84) (See Rule No. 406)";
    String OPENING_BALANCE = "OPENING BALANCE";

    enum SerialNumberTypes {
        CAPITALS, SMALL, NUMBERS
    }

    interface BudgetReportColumns {
        String SR= "Sr";
        String PARTICULARS= "Particulars";
        String CODE_NUMBER= "Code Number";
        String YEAR_4_ACTUALS= "Year-4 Actuals";
        String YEAR_3_ACTUALS= "Year-3 Actuals";
        String YEAR_2_ACTUALS= "Year-2 Actuals";

        String YEAR_1_ACTUALS= "Year-1 Actuals";
        String YEAR_1_ACTUALS_FOR_8_MONTHS= "Year-1 Actuals for 8 months";
        String YEAR_1_PROBABLES_FOR_REMAINING_4_MONTHS= "Year-1 Probables for remaining 4 months";
        String YEAR_1_PROBABLES_FOR_FULL_YEAR= "Year-1 Probables for full year";
        String YEAR_0_BUDGETED_AMOUNT= "Year-0 Budgeted amount";
        String YEAR_0_ACTUALS_FOR_8_MONTHS= "Year-0 Actuals for 8 months";
        String YEAR_0_PROBABLES_FOR_REMAINING_4_MONTHS= "Year-0  Probables for remaining 4 months";
        String YEAR_0_PROBABLES_FOR_FULL_YEAR= "Year-0 Probables for full year";
        String YEAR_0_ACTUALS= "Year-0 Actuals";
    }

    List<String> BudgetReportHeaderForBudgeted = Arrays.asList(
            "Sr",
            "Particulars",
            "Code Number",
            "{"+YEAR_STRING+MINUS+"4} Actuals",
            "{"+YEAR_STRING+MINUS+"3} Actuals",
            "{"+YEAR_STRING+MINUS+"2} Actuals",
            "{"+YEAR_STRING+MINUS+"1} Actuals for 8 months",
            "{"+YEAR_STRING+MINUS+"1} Probables for remaining 4 months",
            "{"+YEAR_STRING+MINUS+"1} Probables for full year",
            "{"+YEAR_STRING+MINUS+"0} Budgeted amount"
    );

    List<String> BudgetReportColumnsForBudgeted = Arrays.asList(
            BudgetReportColumns.YEAR_4_ACTUALS,
            BudgetReportColumns.YEAR_3_ACTUALS,
            BudgetReportColumns.YEAR_2_ACTUALS,
            BudgetReportColumns.YEAR_1_ACTUALS_FOR_8_MONTHS,
            BudgetReportColumns.YEAR_1_PROBABLES_FOR_REMAINING_4_MONTHS,
            BudgetReportColumns.YEAR_1_PROBABLES_FOR_FULL_YEAR,
            BudgetReportColumns.YEAR_0_BUDGETED_AMOUNT
    );

    List<String> BudgetReportHeaderForEstimates = Arrays.asList(
            "Sr",
            "Particulars",
            "Code Number",
            "{"+YEAR_STRING+MINUS+"4} Actuals",
            "{"+YEAR_STRING+MINUS+"3} Actuals",
            "{"+YEAR_STRING+MINUS+"2} Actuals",
            "{"+YEAR_STRING+MINUS+"1} Actuals for 8 months",
            "{"+YEAR_STRING+MINUS+"1} Probables for remaining 4 months",
            "{"+YEAR_STRING+MINUS+"1} Probables for full year",
            "{"+YEAR_STRING+MINUS+"0} Budgeted amount"
    );

    List<String> BudgetReportColumnsForEstimates = Arrays.asList(
            BudgetReportColumns.YEAR_4_ACTUALS,
            BudgetReportColumns.YEAR_3_ACTUALS,
            BudgetReportColumns.YEAR_2_ACTUALS,
            BudgetReportColumns.YEAR_1_ACTUALS_FOR_8_MONTHS,
            BudgetReportColumns.YEAR_1_PROBABLES_FOR_REMAINING_4_MONTHS,
            BudgetReportColumns.YEAR_1_PROBABLES_FOR_FULL_YEAR,
            BudgetReportColumns.YEAR_0_BUDGETED_AMOUNT
    );

    List<String> BudgetReportHeaderForActuals = Arrays.asList(
            "Sr",
            "Particulars",
            "Code Number",
            "{"+YEAR_STRING+MINUS+"4} Actuals",
            "{"+YEAR_STRING+MINUS+"3} Actuals",
            "{"+YEAR_STRING+MINUS+"2} Actuals",
            "{"+YEAR_STRING+MINUS+"1} Actuals for 8 months",
            "{"+YEAR_STRING+MINUS+"1} Probables for remaining 4 months",
            "{"+YEAR_STRING+MINUS+"1} Probables for full year",
            "{"+YEAR_STRING+MINUS+"0} Budgeted amount"
    );

    List<String> BudgetReportColumnsForActuals = Arrays.asList(
            BudgetReportColumns.YEAR_4_ACTUALS,
            BudgetReportColumns.YEAR_3_ACTUALS,
            BudgetReportColumns.YEAR_2_ACTUALS,
            BudgetReportColumns.YEAR_1_ACTUALS_FOR_8_MONTHS,
            BudgetReportColumns.YEAR_1_PROBABLES_FOR_REMAINING_4_MONTHS,
            BudgetReportColumns.YEAR_1_PROBABLES_FOR_FULL_YEAR,
            BudgetReportColumns.YEAR_0_BUDGETED_AMOUNT
    );
}

