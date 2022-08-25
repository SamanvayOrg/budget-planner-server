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

    String EMPTY_STRING = "";
    String REPLACE_PATTERN = "\\{(.*?)\\}";
    String YEAR_STRING = "yearString";
    String MINUS = "-";
    String TITLE_TEXT = " Budget Estimate of the Receipts and Payments for the financial year ";
    String SUB_TITLE_TEXT = "(Form No. 84) (See Rule No. 406)";

    String OPENING_BALANCE = "OPENING BALANCE";
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
}

