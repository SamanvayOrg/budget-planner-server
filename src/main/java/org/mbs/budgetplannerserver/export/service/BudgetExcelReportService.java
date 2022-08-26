package org.mbs.budgetplannerserver.export.service;

import lombok.NonNull;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.mbs.budgetplannerserver.contract.BudgetContract;
import org.mbs.budgetplannerserver.contract.BudgetLineContract;
import org.mbs.budgetplannerserver.domain.AmountType;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.export.data.BudgetExcelReportConstants;
import org.mbs.budgetplannerserver.export.data.CustomCellStyle;
import org.mbs.budgetplannerserver.export.data.CustomCellStyleAndValue;
import org.mbs.budgetplannerserver.mapper.BudgetContractMapper;
import org.mbs.budgetplannerserver.service.BudgetLineService;
import org.mbs.budgetplannerserver.service.BudgetService;
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

import static java.util.stream.Collectors.groupingBy;

@Service
public class BudgetExcelReportService implements BudgetExcelReportConstants {
    private final BudgetService budgetService;
    private final BudgetLineService budgetLineService;
    private final StylesGenerator stylesGenerator;
    private final TranslationService translationService;
    public BudgetExcelReportService(BudgetService budgetService, BudgetLineService budgetLineService,
                                    StylesGenerator stylesGenerator, TranslationService translationService) {
        this.budgetService = budgetService;
        this.budgetLineService = budgetLineService;
        this.stylesGenerator = stylesGenerator;
        this.translationService = translationService;
    }

    public byte[] generateReport(Workbook wb, Integer year, AmountType amountType, Budget budget, String languageCode) throws IOException {
        Map<CustomCellStyle, CellStyle> styles = stylesGenerator.prepareStyles(wb);
        Sheet sheet = wb.createSheet(amountType + " " +budget.getFinancialYearString());
        TranslationSearchHelper translationSearchHelper = new TranslationSearchHelper(translationService.getTranslations(),
                languageCode, budget.getMunicipality().getState());
        setColumnsWidth(sheet, BudgetReportHeaderForBudgeted.size());
        createTitleRow(sheet, styles, budget, year, translationSearchHelper);
        createSubTitleRow(sheet, styles, budget, year, translationSearchHelper);
        createMergedCenteredTextRowWithoutBorder(sheet, styles, EMPTY_STRING, SUB_TITLE_ROW+1);
        createHeaderRow(sheet, styles, BudgetReportHeaderForBudgeted, year, translationSearchHelper);
        createOpeningBalanceRow(sheet, styles, budget, translationSearchHelper);
        createBudgetRows(sheet, styles, BudgetReportColumnsForBudgeted, year, budget, translationSearchHelper);
//TODO        createTotalsRow(sheet, styles);
//TODO        createFooterRow(sheet, styles);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        out.close();
        wb.close();
        return out.toByteArray();
    }

    private void createBudgetRows(Sheet sheet, Map<CustomCellStyle, CellStyle> styles,
                                  List<String> budgetReportColumns, Integer year, Budget budget,
                                  TranslationSearchHelper translationSearchHelper) {
        BudgetContract budgetContract = new BudgetContractMapper().map(budget);
        Map<String, Map<String, List<BudgetLineContract>>> mapOfBLC = budgetContract.getBudgetLines().stream()
                .sorted(Comparator.comparing(BudgetLineContract::getMajorHeadGroupDisplayOrder))
                .collect(groupingBy(BudgetLineContract::getMajorHeadGroup, groupingBy(BudgetLineContract::getMajorHead)));
        int rowIndex=OPENING_BALANCE_ROW+1;
        HashMap<String, BigDecimal> budgetTotals = new HashMap<>();
        int majorHeadGroupDisplayOrder=1;
        for (Map.Entry<String,Map<String, List<BudgetLineContract>>> entry : mapOfBLC.entrySet()) {
            String majorHeadGroup = entry.getKey();
            createMajorHeadGroupRow(rowIndex++, sheet, styles, majorHeadGroup, majorHeadGroupDisplayOrder++, translationSearchHelper);
            HashMap<String, BigDecimal> majorHeadGroupColumnTotals = new HashMap<>();
            int majorHeadDisplayOrder=1;
            for (Map.Entry<String, List<BudgetLineContract>> sEntry : entry.getValue().entrySet()) {
                String majorHead = sEntry.getKey();
                createMajorHeadRow(rowIndex++, sheet, styles, majorHead, majorHeadDisplayOrder++, translationSearchHelper);
                HashMap<String, BigDecimal> majorHeadColumnTotals = new HashMap<>();
                int budgetRowIndex=1;
                for (BudgetLineContract blc : sEntry.getValue()) {
                    createBudgetRow(rowIndex++, sheet, styles, blc, budgetRowIndex++, translationSearchHelper,
                            majorHeadColumnTotals);
                }
                createTotalsRow(rowIndex++, sheet, styles, majorHead, translationSearchHelper, majorHeadColumnTotals);
                addTotals(majorHeadColumnTotals, majorHeadGroupColumnTotals);
            }
            createTotalsRow(rowIndex++, sheet, styles, majorHeadGroup, translationSearchHelper, majorHeadGroupColumnTotals);
            addTotals(majorHeadGroupColumnTotals, budgetTotals);
        }
        createTotalsRow(rowIndex++, sheet, styles, EMPTY_STRING, translationSearchHelper, budgetTotals);
    }

    private void addTotals(HashMap<String, BigDecimal> majorHeadColumnTotals, HashMap<String, BigDecimal> majorHeadGroupColumnTotals) {
        majorHeadColumnTotals.forEach( (key,value) -> {
            addColumnValueToTotals(majorHeadGroupColumnTotals, key, value);
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
        CustomCellStyle centerAligned = CustomCellStyle.CENTER_ALIGNED;
        CellStyle cellStyle = styles.get(centerAligned);
        cellStyle.setWrapText(true);
        ArrayList<CustomCellStyleAndValue> customCellStyleAndValueArrayList = new ArrayList<>();
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                getTranslatedValue(getSerialNumber(displayOrder, SerialNumberTypes.SMALL), translations)));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                getTranslatedValue(majorHead, translations)));
        setColumnValuesAndStyleForRow(sheet, rowIndex, customCellStyleAndValueArrayList);
    }

    private void createBudgetRow(int rowIndex, Sheet sheet, Map<CustomCellStyle, CellStyle> styles,
                                 BudgetLineContract budgetLineContract, int budgetRowIndex,
                                 TranslationSearchHelper translations, HashMap<String, BigDecimal> majorHeadColumnTotals) {
        CustomCellStyle rightAligned = CustomCellStyle.RIGHT_ALIGNED;
        CellStyle cellStyle = styles.get(rightAligned);
        cellStyle.setWrapText(false);
        ArrayList<CustomCellStyleAndValue> customCellStyleAndValueArrayList = new ArrayList<>();
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                getTranslatedValue(getSerialNumber(budgetRowIndex, SerialNumberTypes.NUMBERS), translations)));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(styles.get(CustomCellStyle.CENTER_ALIGNED),
                getTranslatedValue(budgetLineContract.getName(), translations)));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                budgetLineContract.getCode()));
        BudgetReportColumnsForBudgeted.forEach( columnName -> {
            BigDecimal value = getColumnValue(budgetLineContract, columnName);
            addColumnValueToTotals(majorHeadColumnTotals, columnName, value);
            customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                    value == null ? EMPTY_STRING : value.toPlainString()));
        });
        setColumnValuesAndStyleForRow(sheet, rowIndex, customCellStyleAndValueArrayList);
    }

    private void createTotalsRow(int rowIndex, Sheet sheet, Map<CustomCellStyle, CellStyle> styles,
                                 String name, TranslationSearchHelper translations, HashMap<String, BigDecimal> columnTotals) {
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
        BudgetReportColumnsForBudgeted.forEach( columnName -> {
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

    private void createOpeningBalanceRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles, Budget budget,
                                         TranslationSearchHelper translations) {
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
//        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);


        ArrayList<CustomCellStyleAndValue> customCellStyleAndValueArrayList = new ArrayList<>();
        for (int i = 4; i > INT_CONSTANT_ZERO; i--) {
            customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                    budget.getPreviousYearBudgets().getBudgetForYear(i).getOpeningBalance().toPlainString()));
        }
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
               budget.getPreviousYearBudgets().getBudgetForYear(1).getClosingBalance().toPlainString()));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                budget.getOpeningBalance().toPlainString()));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                budget.getClosingBalance().toPlainString()));

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
                                TranslationSearchHelper translations) {
        String munName = translations.getTranslationValue(budget.getMunicipality().getName());
        String title = translations.getTranslationValue(TITLE_TEXT);
        createMergedCenteredTextRowWithoutBorder(sheet, styles,
                munName+title+budget.getFinancialYearString(), TITLE_ROW);

    }

    private void createSubTitleRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles, Budget budget, Integer year,
                                   TranslationSearchHelper translations) {
        String subTitle = translations.getTranslationValue(SUB_TITLE_TEXT);
        createMergedCenteredTextRowWithoutBorder(sheet, styles, subTitle, SUB_TITLE_ROW);
    }

    private void createMergedCenteredTextRowWithoutBorder(Sheet sheet, Map<CustomCellStyle, CellStyle> styles,
                                                          String value, int targetRow) {
        Row row = sheet.createRow(targetRow);
        // Merges the cells
        CellRangeAddress cellRangeAddress = new CellRangeAddress(targetRow, targetRow, STARTING_COLUMN_NUM,
                STARTING_COLUMN_NUM+BudgetReportHeaderForBudgeted.size()-1);
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

    private BigDecimal getColumnValue(BudgetLineContract budgetLine, String columnName) {
        switch(columnName) {
            case BudgetReportColumns.YEAR_4_ACTUALS: return budgetLine.getYearMinus2Actuals();
            case BudgetReportColumns.YEAR_3_ACTUALS: return budgetLine.getYearMinus1Actuals();
            case BudgetReportColumns.YEAR_2_ACTUALS: return budgetLine.getPreviousYearActuals();
            case BudgetReportColumns.YEAR_1_ACTUALS_FOR_8_MONTHS: return budgetLine.getCurrentYear8MonthsActuals();
            case BudgetReportColumns.YEAR_1_PROBABLES_FOR_REMAINING_4_MONTHS: return budgetLine.getCurrentYear4MonthsProbables();
            case BudgetReportColumns.YEAR_1_PROBABLES_FOR_FULL_YEAR: {
                if(budgetLine.getCurrentYear8MonthsActuals() != null && budgetLine.getCurrentYear4MonthsProbables() != null) {
                    return budgetLine.getCurrentYear8MonthsActuals().add(budgetLine.getCurrentYear4MonthsProbables());
                } else {
                    if(budgetLine.getCurrentYear8MonthsActuals() != null) {
                        return budgetLine.getCurrentYear8MonthsActuals();
                    } else {
                        return budgetLine.getCurrentYear4MonthsProbables();
                    }
                }
            }
            case BudgetReportColumns.YEAR_0_BUDGETED_AMOUNT: return budgetLine.getBudgetedAmount();
            default: throw new RuntimeException("Mapping not found for columnName" + columnName);
        }
    }

    private String getSerialNumber(int index, SerialNumberTypes serialNumberTypes) {
        switch (serialNumberTypes) {
            case CAPITALS: return Character.toString(64+index);
            case SMALL: return Character.toString(96+index);
            default: return String.valueOf(index);
        }
    }
}
