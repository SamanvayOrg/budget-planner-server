package org.mbs.budgetplannerserver.export.service;

import lombok.NonNull;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.mbs.budgetplannerserver.domain.*;
import org.mbs.budgetplannerserver.domain.code.MajorHead;
import org.mbs.budgetplannerserver.domain.code.MajorHeadGroup;
import org.mbs.budgetplannerserver.export.data.BudgetExcelReportConstants;
import org.mbs.budgetplannerserver.export.data.CustomCellStyle;
import org.mbs.budgetplannerserver.export.data.CustomCellStyleAndValue;
import org.mbs.budgetplannerserver.service.TranslationService;
import org.mbs.budgetplannerserver.util.BudgetStringUtils;
import org.mbs.budgetplannerserver.util.TranslationSearchHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.mbs.budgetplannerserver.domain.PreviousYears.PREV_YEAR;

@Service
public class BudgetExcelReportService implements BudgetExcelReportConstants {
    private final StylesGenerator stylesGenerator;
    private final TranslationService translationService;
    public BudgetExcelReportService(StylesGenerator stylesGenerator, TranslationService translationService) {
        this.stylesGenerator = stylesGenerator;
        this.translationService = translationService;
    }

    public byte[] generateReport(Workbook wb, Integer year, AmountType amountType, Budget budget, String languageCode) throws IOException {
        switch (amountType) {
            case BUDGETED:
                return generateReport(wb, year, amountType.name(), BudgetReportHeaderForBudgeted, BudgetReportColumnsForBudgeted, budget, languageCode);
            case ESTIMATES:
                return generateReport(wb, year, amountType.name(), BudgetReportHeaderForEstimates, BudgetReportColumnsForEstimates, budget, languageCode);
            case ACTUALS:
                return generateReport(wb, year, amountType.name(), BudgetReportHeaderForActuals, BudgetReportColumnsForActuals, budget, languageCode);
            default:
                throw new RuntimeException("UnSupported value specified for AmountType "+ amountType);
        }
    }

    public byte[] generateReport(Workbook wb, Integer year, String reportType, List<String> budgetReportHeaders, List<String> budgetReportColumns,
                                 Budget budget, String languageCode) throws IOException {
        Map<CustomCellStyle, CellStyle> styles = stylesGenerator.prepareStyles(wb);
        Sheet sheet = wb.createSheet(reportType + " " +budget.getFinancialYearString());
        TranslationSearchHelper translationSearchHelper = new TranslationSearchHelper(translationService.getTranslations(),
                languageCode, budget.getMunicipality().getState());
        setColumnsWidth(sheet, budgetReportHeaders.size());
        createTitleRow(sheet, styles, budget, year, translationSearchHelper, budgetReportHeaders);
        createSubTitleRow(sheet, styles, budget, year, translationSearchHelper, budgetReportHeaders);
        createMergedCenteredTextRowWithoutBorder(sheet, styles, EMPTY_STRING, SUB_TITLE_ROW+1, budgetReportHeaders);
        createHeaderRow(sheet, styles, budgetReportHeaders, year, translationSearchHelper);
        HashMap<String, BigDecimal> budgetTotals = new HashMap<>();
        HashMap<String, BigDecimal> majorHeadColumnTotals = new HashMap<>();
        createOpeningBalanceRow(sheet, styles, budgetReportColumns, budget, translationSearchHelper, majorHeadColumnTotals);
        addTotals(majorHeadColumnTotals, budgetTotals);
        int rowIndex = createBudgetRows(sheet, styles, budgetReportColumns, budget, translationSearchHelper, budgetTotals);
        createTotalsRow(rowIndex++, sheet, styles, EMPTY_STRING, translationSearchHelper, budgetTotals, budgetReportColumns);
//TODO        createFooterRow(sheet, styles);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        out.close();
        wb.close();
        return out.toByteArray();
    }

    private int createBudgetRows(Sheet sheet, Map<CustomCellStyle, CellStyle> styles,
                                 List<String> budgetReportColumns, Budget budget,
                                 TranslationSearchHelper translationSearchHelper, HashMap<String, BigDecimal> budgetTotals) {
        Map<MajorHeadGroup, Map<MajorHead, List<BudgetLine>>> allMajorHeadGroupsMap = budget
                .getBudgetLines()
                .stream()
                .collect(groupingBy(BudgetLine::getMajorHeadGroup,
                        LinkedHashMap::new,
                        groupingBy(BudgetLine::getMajorHead, LinkedHashMap::new, Collectors.toList())));

        int rowIndex=OPENING_BALANCE_ROW+1;
        int majorHeadGroupDisplayOrder=1;
        List<MajorHeadGroup> allMajorHeadGroups = allMajorHeadGroupsMap.keySet().stream().sorted(Comparator.comparing(MajorHeadGroup::getDisplayOrder)).collect(Collectors.toList());
        for(MajorHeadGroup majorHeadGroup: allMajorHeadGroups) {
            createMajorHeadGroupRow(rowIndex++, sheet, styles, majorHeadGroup.getName(), majorHeadGroupDisplayOrder++, translationSearchHelper);
            HashMap<String, BigDecimal> majorHeadGroupColumnTotals = new HashMap<>();
            int majorHeadDisplayOrder=1;
            Map<MajorHead, List<BudgetLine>> majorHeadMap = allMajorHeadGroupsMap.get(majorHeadGroup);
            List<MajorHead> allMajorHeads = majorHeadMap.keySet().stream().sorted(Comparator.comparing(MajorHead::getDisplayOrder)).collect(Collectors.toList());

            for(MajorHead majorHead: allMajorHeads) {
                createMajorHeadRow(rowIndex++, sheet, styles, majorHead.getName(), majorHeadDisplayOrder++, translationSearchHelper);
                HashMap<String, BigDecimal> majorHeadColumnTotals = new HashMap<>();
                int budgetRowIndex=1;
                List<BudgetLine> budgetLines = majorHeadMap.get(majorHead).stream().sorted(Comparator.comparing(BudgetLine::getName)).collect(Collectors.toList());
                for (BudgetLine budgetLine : budgetLines) {
                    createBudgetRow(rowIndex++, sheet, styles, budgetLine, budgetRowIndex++, translationSearchHelper,
                            majorHeadColumnTotals, budgetReportColumns);
                }
                createTotalsRow(rowIndex++, sheet, styles, majorHead.getName(), translationSearchHelper, majorHeadColumnTotals, budgetReportColumns);
                addTotals(majorHeadColumnTotals, majorHeadGroupColumnTotals);
            }
            createTotalsRow(rowIndex++, sheet, styles, majorHeadGroup.getName(), translationSearchHelper, majorHeadGroupColumnTotals, budgetReportColumns);

            if(majorHeadGroup.getName().equals(REVENUE_RECEIPT) || majorHeadGroup.getName().equals(ASSETS)) {
                addTotals(majorHeadGroupColumnTotals, budgetTotals);
            } else if(majorHeadGroup.getName().equals(EXPENSES) || majorHeadGroup.getName().equals(LIABILITY)) {
                subTotals(majorHeadGroupColumnTotals, budgetTotals);
            } else {
                throw new RuntimeException("Error computing Total, unknown majorHeadGroup value "+majorHeadGroup);
            }
        };
        return rowIndex;
    }

    private void addTotals(HashMap<String, BigDecimal> majorHeadColumnTotals, HashMap<String, BigDecimal> majorHeadGroupColumnTotals) {
        majorHeadColumnTotals.forEach( (key,value) -> {
            addColumnValueToTotals(majorHeadGroupColumnTotals, key, value);
        });
    }

    private void subTotals(HashMap<String, BigDecimal> majorHeadColumnTotals, HashMap<String, BigDecimal> majorHeadGroupColumnTotals) {
        majorHeadColumnTotals.forEach( (key,value) -> {
            subColumnValueFromTotals(majorHeadGroupColumnTotals, key, value);
        });
    }


    private void createMajorHeadGroupRow(int rowIndex, Sheet sheet, Map<CustomCellStyle, CellStyle> styles,
                                         String majorHeadGroup, int majorHeadGroupDisplayOrder,
                                         TranslationSearchHelper translations) {
        CustomCellStyle leftAligned = CustomCellStyle.LEFT_ALIGNED;
        CellStyle cellStyle = styles.get(leftAligned);
        cellStyle.setWrapText(true);
        ArrayList<CustomCellStyleAndValue> customCellStyleAndValueArrayList = new ArrayList<>();
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                getTranslatedValue(getSerialNumber(majorHeadGroupDisplayOrder, SerialNumberTypes.CAPITALS), translations)));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                getTranslatedValue(majorHeadGroup, translations)));
        setColumnValuesAndStyleForRow(sheet, rowIndex, customCellStyleAndValueArrayList);
    }

    private void createMajorHeadRow(int rowIndex, Sheet sheet, Map<CustomCellStyle, CellStyle> styles,
                                         String majorHead, int displayOrder,
                                         TranslationSearchHelper translations) {
        CustomCellStyle centerAligned = CustomCellStyle.LEFT_ALIGNED;
        CellStyle cellStyle = styles.get(centerAligned);
        cellStyle.setWrapText(true);
        ArrayList<CustomCellStyleAndValue> customCellStyleAndValueArrayList = new ArrayList<>();
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(styles.get(CustomCellStyle.CENTER_ALIGNED),
                getTranslatedValue(getSerialNumber(displayOrder, SerialNumberTypes.SMALL), translations)));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                getTranslatedValue(majorHead, translations)));
        setColumnValuesAndStyleForRow(sheet, rowIndex, customCellStyleAndValueArrayList);
    }

    private void createBudgetRow(int rowIndex, Sheet sheet, Map<CustomCellStyle, CellStyle> styles,
                                 BudgetLine budgetLine, int budgetRowIndex,
                                 TranslationSearchHelper translations, HashMap<String, BigDecimal> majorHeadColumnTotals,
                                 List<String> budgetReportColumns) {
        CustomCellStyle rightAligned = CustomCellStyle.RIGHT_ALIGNED;
        CellStyle cellStyle = styles.get(rightAligned);
        cellStyle.setWrapText(false);
        ArrayList<CustomCellStyleAndValue> customCellStyleAndValueArrayList = new ArrayList<>();
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                getTranslatedValue(getSerialNumber(budgetRowIndex, SerialNumberTypes.NUMBERS), translations)));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(styles.get(CustomCellStyle.LEFT_ALIGNED),
                getTranslatedValue(budgetLine.getName(), translations)));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                budgetLine.getFullCode()));
        budgetReportColumns.forEach(columnName -> {
            BigDecimal value = getColumnValue(budgetLine, columnName);
            addColumnValueToTotals(majorHeadColumnTotals, columnName, value);
            customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                    value == null ? EMPTY_STRING : value.toPlainString()));
        });
        setColumnValuesAndStyleForRow(sheet, rowIndex, customCellStyleAndValueArrayList);
    }

    private void createTotalsRow(int rowIndex, Sheet sheet, Map<CustomCellStyle, CellStyle> styles,
                                 String name, TranslationSearchHelper translations, HashMap<String, BigDecimal> columnTotals,
                                 List<String> budgetReportColumns) {
        CustomCellStyle greyCenteredBoldArialWithBorder = CustomCellStyle.LIGHT_GREY_LEFT_ALIGNED_ARIAL_WITH_BORDER;
        CellStyle cellStyle = styles.get(greyCenteredBoldArialWithBorder);
        cellStyle.setWrapText(true);
        ArrayList<CustomCellStyleAndValue> customCellStyleAndValueArrayList = new ArrayList<>();
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                EMPTY_STRING));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                getTranslatedValue((name+ TOTAL).trim(), translations)));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                EMPTY_STRING));
        CellStyle totalsCellStyle = styles.get(CustomCellStyle.RIGHT_ALIGNED_BOLD);
        totalsCellStyle.setWrapText(false);
        budgetReportColumns.forEach(columnName -> {
            BigDecimal value = columnTotals.get(columnName);
            customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(totalsCellStyle,
                    value == null ? EMPTY_STRING : value.toPlainString()));
        });
        setColumnValuesAndStyleForRow(sheet, rowIndex, customCellStyleAndValueArrayList);
    }

    private void addColumnValueToTotals(HashMap<String, BigDecimal> columnTotals, String columnName, BigDecimal value) {
        BigDecimal oldValue = columnTotals.get(columnName);
        if(oldValue == null) {
            oldValue = new BigDecimal(INT_CONSTANT_ZERO);
        }
        if(value != null) {
            oldValue = oldValue.add(value);
        }
        columnTotals.put(columnName, oldValue);
    }

    private void subColumnValueFromTotals(HashMap<String, BigDecimal> columnTotals, String columnName, BigDecimal value) {
        BigDecimal oldValue = columnTotals.get(columnName);
        if(oldValue == null) {
            oldValue = new BigDecimal(INT_CONSTANT_ZERO);
        }
        if(value != null) {
            oldValue = oldValue.subtract(value);
        }
        columnTotals.put(columnName, oldValue);
    }


    private void createOpeningBalanceRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles, List<String> budgetReportColumns,
                                         Budget budget, TranslationSearchHelper translations, HashMap<String, BigDecimal> majorHeadColumnTotals) {
        CustomCellStyle rightAlignedBold = CustomCellStyle.RIGHT_ALIGNED_BOLD;
        CellStyle cellStyle = styles.get(rightAlignedBold);

        Row row = sheet.createRow(OPENING_BALANCE_ROW);
        // Merges the cells
        CellRangeAddress cellRangeAddress = new CellRangeAddress(OPENING_BALANCE_ROW, OPENING_BALANCE_ROW, STARTING_COLUMN_NUM,
                OPENING_BALANCE_START_NUM-1);
        sheet.addMergedRegion(cellRangeAddress);

        // Creates the cell
        Cell cell = CellUtil.createCell(row, STARTING_COLUMN_NUM, translations.getTranslationValue(OPENING_BALANCE));
        cell.setCellStyle(styles.get(CustomCellStyle.GREY_CENTERED_BOLD_ARIAL_WITH_BORDER));

        ArrayList<CustomCellStyleAndValue> customCellStyleAndValueArrayList = new ArrayList<>();

        budgetReportColumns.forEach(columnName -> {
            BigDecimal value = getOpeningBalanceValue(budget, columnName);
            addColumnValueToTotals(majorHeadColumnTotals, columnName, value);
            customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                    value == null ? EMPTY_STRING : value.toPlainString()));
        });
        for (int i = INT_CONSTANT_ZERO, columnNumber = OPENING_BALANCE_START_NUM; columnNumber <
                OPENING_BALANCE_START_NUM+customCellStyleAndValueArrayList.size(); columnNumber++, i++) {
            cell = row.createCell(columnNumber);
            cell.setCellValue(customCellStyleAndValueArrayList.get(i).getValue());
            cell.setCellStyle(customCellStyleAndValueArrayList.get(i).getStyle());
        }

    }

    private void setColumnsWidth(Sheet sheet, int columns) {
        sheet.setColumnWidth(STARTING_COLUMN_NUM, 256 * 15);
        sheet.setColumnWidth(STARTING_COLUMN_NUM+1, 256 * 15 * 4);
        sheet.setColumnWidth(STARTING_COLUMN_NUM+2, 256 * 15);
        for (int columnIndex=STARTING_COLUMN_NUM+3; columnIndex < STARTING_COLUMN_NUM+columns; columnIndex++) {
            sheet.setColumnWidth(columnIndex, 256 * 20);
        }
    }

    private void createTitleRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles, Budget budget, Integer year,
                                TranslationSearchHelper translations, List<String> columnHeaders) {
        String munName = translations.getTranslationValue(budget.getMunicipality().getName());
        String title = translations.getTranslationValue(TITLE_TEXT);
        createMergedCenteredTextRowWithoutBorder(sheet, styles,
                munName+title+budget.getFinancialYearString(), TITLE_ROW, columnHeaders);

    }

    private void createSubTitleRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles, Budget budget, Integer year,
                                   TranslationSearchHelper translations, List<String> columnHeaders) {
        String subTitle = translations.getTranslationValue(SUB_TITLE_TEXT);
        createMergedCenteredTextRowWithoutBorder(sheet, styles, subTitle, SUB_TITLE_ROW, columnHeaders);
    }

    private void createMergedCenteredTextRowWithoutBorder(Sheet sheet, Map<CustomCellStyle, CellStyle> styles,
                                                          String value, int targetRow, List<String> columnHeaders) {
        Row row = sheet.createRow(targetRow);
        // Merges the cells
        CellRangeAddress cellRangeAddress = new CellRangeAddress(targetRow, targetRow, STARTING_COLUMN_NUM,
                STARTING_COLUMN_NUM+columnHeaders.size()-1);
        sheet.addMergedRegion(cellRangeAddress);

        // Creates the cell
        Cell cell = CellUtil.createCell(row, STARTING_COLUMN_NUM, value);
        CustomCellStyle centeredBoldArialWithoutBorder = CustomCellStyle.CENTERED_BOLD_ARIAL_WITHOUT_BORDER;
        CellStyle cellStyle = styles.get(centeredBoldArialWithoutBorder);
        cellStyle.setWrapText(true);
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
        cell.setCellStyle(cellStyle);
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
    }

    private void createHeaderRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles, List<String> headers, Integer year,
                                 TranslationSearchHelper translations) {
        CustomCellStyle greyCenteredBoldArialWithBorder = CustomCellStyle.GREY_CENTERED_BOLD_ARIAL_WITH_BORDER;
        CellStyle cellStyle = styles.get(greyCenteredBoldArialWithBorder);
        cellStyle.setWrapText(true);

        ArrayList<CustomCellStyleAndValue> customCellStyleAndValueArrayList = new ArrayList<>();
        for (int columnNumber = STARTING_COLUMN_NUM; columnNumber < STARTING_COLUMN_NUM+headers.size(); columnNumber++) {
            String columnValuePattern = headers.get(columnNumber);
            String replacedValue = getReplacedValue(year, getTranslatedValue(columnValuePattern, translations));
            customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle, replacedValue));
        }
        setColumnValuesAndStyleForRow(sheet, HEADER_ROW, customCellStyleAndValueArrayList);
    }

    private String getTranslatedValue(String columnValuePattern, TranslationSearchHelper translations) {
        return translations.getTranslationValue(columnValuePattern);
    }

    private String getReplacedValue(Integer year, String columnValuePattern) {
        Pattern pattern = Pattern.compile(REPLACE_PATTERN);
        Matcher matcher = pattern.matcher(columnValuePattern);
        String replacedValue = columnValuePattern;
        if(matcher.find()) {
            String yearString = matcher.group(1);
            if(yearString.contains(YEAR_STRING)) {
                int minus = Integer.parseInt(yearString
                        .substring(yearString.indexOf(MINUS)+1));
                BudgetStringUtils.getBudgetYearString(year, minus);
                replacedValue = matcher.replaceAll(BudgetStringUtils.getBudgetYearString(year, minus));
            }
        }
        return replacedValue;
    }

    private void setColumnValuesAndStyleForRow(@NonNull Sheet sheet, @NonNull Integer rowNum,
                                               @NonNull ArrayList<CustomCellStyleAndValue> customCellStyleAndValueArrayList) {
        Row row = sheet.createRow(rowNum);
        for (int columnNumber = STARTING_COLUMN_NUM; columnNumber < STARTING_COLUMN_NUM+ customCellStyleAndValueArrayList.size();
             columnNumber++) {
            Cell cell = row.createCell(columnNumber);
            cell.setCellValue(customCellStyleAndValueArrayList.get(columnNumber).getValue());
            cell.setCellStyle(customCellStyleAndValueArrayList.get(columnNumber).getStyle());
        }
    }

    private BigDecimal getColumnValue(BudgetLine budgetLine, String columnName) {
        switch(columnName) {
            case BudgetReportColumns.YEAR_4_ACTUALS: return budgetLine.getPreviousActuals(PreviousYears.PREV_YEAR_MINUS_3);
            case BudgetReportColumns.YEAR_3_ACTUALS: return budgetLine.getPreviousActuals(PreviousYears.PREV_YEAR_MINUS_2);
            case BudgetReportColumns.YEAR_2_ACTUALS: return budgetLine.getPreviousActuals(PreviousYears.PREV_YEAR_MINUS_1);
            case BudgetReportColumns.YEAR_1_BUDGETED_AMOUNT: return budgetLine.getBudgetedAmount();
            case BudgetReportColumns.YEAR_1_ACTUALS_FOR_8_MONTHS: return budgetLine.getPreviousEightMonthActuals(PREV_YEAR);
            case BudgetReportColumns.YEAR_1_PROBABLES_FOR_REMAINING_4_MONTHS: return budgetLine.getPreviousFourMonthProbables(PREV_YEAR);
            case BudgetReportColumns.YEAR_1_PROBABLES_FOR_FULL_YEAR: {
                if(budgetLine.getPreviousEightMonthActuals(PREV_YEAR) != null && budgetLine.getPreviousFourMonthProbables(PREV_YEAR) != null) {
                    return budgetLine.getPreviousEightMonthActuals(PREV_YEAR).add(budgetLine.getPreviousFourMonthProbables(PREV_YEAR));
                } else {
                    if(budgetLine.getPreviousEightMonthActuals(PREV_YEAR) != null) {
                        return budgetLine.getPreviousEightMonthActuals(PREV_YEAR);
                    } else {
                        return budgetLine.getPreviousFourMonthProbables(PREV_YEAR);
                    }
                }
            }
            case BudgetReportColumns.YEAR_0_BUDGETED_AMOUNT: return budgetLine.getBudgetedAmount();
            case BudgetReportColumns.YEAR_1_ACTUALS: return budgetLine.getPreviousActuals(PREV_YEAR);
            case BudgetReportColumns.YEAR_0_ACTUALS_FOR_8_MONTHS: return budgetLine.getEightMonthActualAmount();
            case BudgetReportColumns.YEAR_0_PROBABLES_FOR_REMAINING_4_MONTHS: return budgetLine.getFourMonthProbableAmount();
            case BudgetReportColumns.YEAR_0_PROBABLES_FOR_FULL_YEAR: {
                if(budgetLine.getEightMonthActualAmount() != null && budgetLine.getFourMonthProbableAmount() != null) {
                    return budgetLine.getEightMonthActualAmount().add(budgetLine.getFourMonthProbableAmount());
                } else {
                    if(budgetLine.getEightMonthActualAmount() != null) {
                        return budgetLine.getEightMonthActualAmount();
                    } else {
                        return budgetLine.getFourMonthProbableAmount();
                    }
                }
            }
            case BudgetReportColumns.YEAR_0_ACTUALS: return budgetLine.getActualAmount();
            default: throw new RuntimeException("Mapping not found for columnName" + columnName);
        }
    }

    private BigDecimal getOpeningBalanceValue(Budget budget, String columnName) {
        switch(columnName) {
            case BudgetReportColumns.YEAR_4_ACTUALS: return budget.getPreviousYearBudgets().getBudgetForYear(4).getOpeningBalance();
            case BudgetReportColumns.YEAR_3_ACTUALS: return budget.getPreviousYearBudgets().getBudgetForYear(3).getOpeningBalance();
            case BudgetReportColumns.YEAR_2_ACTUALS: return budget.getPreviousYearBudgets().getBudgetForYear(2).getOpeningBalance();
            case BudgetReportColumns.YEAR_1_BUDGETED_AMOUNT:
            case BudgetReportColumns.YEAR_1_PROBABLES_FOR_REMAINING_4_MONTHS:
                return budget.getPreviousYearBudgets().getBudgetForYear(1).getClosingBalance();
            case BudgetReportColumns.YEAR_1_ACTUALS_FOR_8_MONTHS:
            case BudgetReportColumns.YEAR_1_ACTUALS:
                return budget.getPreviousYearBudgets().getBudgetForYear(1).getOpeningBalance();
            case BudgetReportColumns.YEAR_1_PROBABLES_FOR_FULL_YEAR:
            case BudgetReportColumns.YEAR_0_ACTUALS_FOR_8_MONTHS:
            case BudgetReportColumns.YEAR_0_ACTUALS:
                return budget.getOpeningBalance();
            case BudgetReportColumns.YEAR_0_BUDGETED_AMOUNT:
            case BudgetReportColumns.YEAR_0_PROBABLES_FOR_REMAINING_4_MONTHS:
            case BudgetReportColumns.YEAR_0_PROBABLES_FOR_FULL_YEAR:
                return budget.getClosingBalance();
            default: throw new RuntimeException("OpeningBalance Mapping not found for columnName" + columnName);
        }
    }

    private String getSerialNumber(int index, SerialNumberTypes serialNumberTypes) {
        switch (serialNumberTypes) {
            case CAPITALS: return Character.toString(CHAR_UPPER_CASE_A +index);
            case SMALL: return Character.toString(CHAR_LOWER_CASE_A +index);
            default: return String.valueOf(index);
        }
    }
}
