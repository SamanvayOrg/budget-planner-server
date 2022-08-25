package org.mbs.budgetplannerserver.export.service;

import lombok.NonNull;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.mbs.budgetplannerserver.domain.AmountType;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.export.data.BudgetExcelReportConstants;
import org.mbs.budgetplannerserver.export.data.CustomCellStyle;
import org.mbs.budgetplannerserver.export.data.CustomCellStyleAndValue;
import org.mbs.budgetplannerserver.service.BudgetLineService;
import org.mbs.budgetplannerserver.service.BudgetService;
import org.mbs.budgetplannerserver.util.BudgetStringUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BudgetExcelExcelReportService implements BudgetExcelReportConstants {
    private final BudgetService budgetService;
    private final BudgetLineService budgetLineService;
    private final StylesGenerator stylesGenerator;

    public BudgetExcelExcelReportService(BudgetService budgetService, BudgetLineService budgetLineService, StylesGenerator stylesGenerator) {
        this.budgetService = budgetService;
        this.budgetLineService = budgetLineService;
        this.stylesGenerator = stylesGenerator;
    }

    public byte[] generateReport(Workbook wb, Integer year, AmountType amountType, Budget budget) throws IOException {
        Map<CustomCellStyle, CellStyle> styles = stylesGenerator.prepareStyles(wb);
        Sheet sheet = wb.createSheet(amountType + " " +budget.getFinancialYearString());

        setColumnsWidth(sheet, BudgetReportHeaderForBudgeted.size());
        createTitleRow(sheet, styles, budget, year);
        createSubTitleRow(sheet, styles, budget, year);
        createMergedCenteredTextRowWithoutBorder(sheet, styles, EMPTY_STRING, SUB_TITLE_ROW+1);
        createHeaderRow(sheet, styles, BudgetReportHeaderForBudgeted.size(), year);
        createOpeningBalanceRow(sheet, styles, budget);

//        createStringsRow(sheet, styles);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        out.close();
        wb.close();

        return out.toByteArray();
    }

    private void createOpeningBalanceRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles, Budget budget) {
        CustomCellStyle rightAligned = CustomCellStyle.RIGHT_ALIGNED;
        CellStyle cellStyle = styles.get(rightAligned);
        budget.getOpeningBalance();

        Row row = sheet.createRow(OPENING_BALANCE_ROW);
        // Merges the cells
        CellRangeAddress cellRangeAddress = new CellRangeAddress(OPENING_BALANCE_ROW, OPENING_BALANCE_ROW, STARTING_COLUMN_NUM,
                OPENING_BALANCE_START_NUM-1);
        sheet.addMergedRegion(cellRangeAddress);

        // Creates the cell
        Cell cell = CellUtil.createCell(row, STARTING_COLUMN_NUM, OPENING_BALANCE);
        cell.setCellStyle(cellStyle);
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);


        ArrayList<CustomCellStyleAndValue> customCellStyleAndValueArrayList = new ArrayList<>();
        for (int i = 4; i > 0; i--) {
            customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                    budget.getPreviousYearBudgets().getBudgetForYear(i).getOpeningBalance().toPlainString()));
        }
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
               budget.getPreviousYearBudgets().getBudgetForYear(1).getClosingBalance().toPlainString()));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                budget.getOpeningBalance().toPlainString()));
        customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle,
                budget.getClosingBalance().toPlainString()));

        for (int i = 0, columnNumber = OPENING_BALANCE_START_NUM; columnNumber < OPENING_BALANCE_START_NUM+ customCellStyleAndValueArrayList.size();
             columnNumber++, i++) {
            cell = row.createCell(columnNumber);
            cell.setCellValue(customCellStyleAndValueArrayList.get(i).getValue());
            cell.setCellStyle(customCellStyleAndValueArrayList.get(i).getStyle());
        }

    }

    private void setColumnsWidth(Sheet sheet, int columns) {
        sheet.setColumnWidth(STARTING_COLUMN_NUM, 256 * 15);
        sheet.setColumnWidth(STARTING_COLUMN_NUM+1, 256 * 15 * 3);
        sheet.setColumnWidth(STARTING_COLUMN_NUM+2, 256 * 15);
        for (int columnIndex=STARTING_COLUMN_NUM+3; columnIndex < STARTING_COLUMN_NUM+columns; columnIndex++) {
            sheet.setColumnWidth(columnIndex, 256 * 20);
        }
    }

    private void createTitleRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles, Budget budget, Integer year) {
        createMergedCenteredTextRowWithoutBorder(sheet, styles,
                budget.getMunicipality().getName()+TITLE_TEXT+budget.getFinancialYearString(), TITLE_ROW);

    }

    private void createSubTitleRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles, Budget budget, Integer year) {
        createMergedCenteredTextRowWithoutBorder(sheet, styles, SUB_TITLE_TEXT, SUB_TITLE_ROW);
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
        cellStyle.setWrapText(false);
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
        cell.setCellStyle(cellStyle);
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
    }

    private void createHeaderRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles, int columns, Integer year) {
        CustomCellStyle greyCenteredBoldArialWithBorder = CustomCellStyle.GREY_CENTERED_BOLD_ARIAL_WITH_BORDER;
        CellStyle cellStyle = styles.get(greyCenteredBoldArialWithBorder);
        cellStyle.setWrapText(true);

        ArrayList<CustomCellStyleAndValue> customCellStyleAndValueArrayList = new ArrayList<>();
        for (int columnNumber = STARTING_COLUMN_NUM; columnNumber < STARTING_COLUMN_NUM+columns; columnNumber++) {
            String columnValuePattern = BudgetReportHeaderForBudgeted.get(columnNumber);
            String replacedValue = getReplacedValue(year, columnValuePattern);
            customCellStyleAndValueArrayList.add(new CustomCellStyleAndValue(cellStyle, replacedValue));
        }
        setColumnValuesAndStyleForRow(sheet, HEADER_ROW, customCellStyleAndValueArrayList);
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
}
