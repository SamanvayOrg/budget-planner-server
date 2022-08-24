package org.mbs.budgetplannerserver.export.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mbs.budgetplannerserver.domain.AmountType;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.export.data.CustomCellStyle;
import org.mbs.budgetplannerserver.service.BudgetLineService;
import org.mbs.budgetplannerserver.service.BudgetService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class ReportService {

    private final BudgetService budgetService;
    private final BudgetLineService budgetLineService;
    private final StylesGenerator stylesGenerator;

    public ReportService(BudgetService budgetService, BudgetLineService budgetLineService, StylesGenerator stylesGenerator) {
        this.budgetService = budgetService;
        this.budgetLineService = budgetLineService;
        this.stylesGenerator = stylesGenerator;
    }

    public byte[] generateBudgetExcelReport(Integer year, AmountType amountType, Budget budget) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();

        return generateReport(wb, year, amountType, budget);
    }

    private byte[] generateReport(Workbook wb, Integer year, AmountType amountType, Budget budget) throws IOException {
        Map<CustomCellStyle, CellStyle> styles = stylesGenerator.prepareStyles(wb);
        Sheet sheet = wb.createSheet(amountType + "_" +budget.getFinancialYearString());

        setColumnsWidth(sheet, 10);//TODO is the number of columns to be set according to budget and amountType
        createHeaderRow(sheet, styles, 10);
//        createStringsRow(sheet, styles);


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        out.close();
        wb.close();

        return out.toByteArray();
    }

    private void setColumnsWidth(Sheet sheet, int columns) {
        sheet.setColumnWidth(0, 256 * 15);
        sheet.setColumnWidth(1, 256 * 15 * 3);
        sheet.setColumnWidth(2, 256 * 15);
        for (int columnIndex=3; columnIndex < columns; columnIndex++) {
            sheet.setColumnWidth(columnIndex, 256 * 20);
        }
    }

    private void createHeaderRow(Sheet sheet, Map<CustomCellStyle, CellStyle> styles, int columns) {
        Row row = sheet.createRow(0);
        for (int columnNumber=0; columnNumber < columns; columnNumber++) {
            Cell cell = row.createCell(columnNumber);
            cell.setCellValue("Column $columnNumber");
            cell.setCellStyle(styles.get(CustomCellStyle.GREY_CENTERED_BOLD_ARIAL_WITH_BORDER));
        }
    }
}
