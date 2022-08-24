package org.mbs.budgetplannerserver.export.service;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

//        setColumnsWidth(sheet);
//        createHeaderRow(sheet, styles);
//        createStringsRow(sheet, styles);


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        out.close();
        wb.close();

        return out.toByteArray();
    }
}
