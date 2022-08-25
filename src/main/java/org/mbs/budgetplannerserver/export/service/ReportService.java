package org.mbs.budgetplannerserver.export.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mbs.budgetplannerserver.domain.AmountType;
import org.mbs.budgetplannerserver.domain.Budget;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ReportService {
    private final BudgetExcelReportService budgetExcelReportService;
    public ReportService(BudgetExcelReportService budgetExcelReportService) {

       this.budgetExcelReportService = budgetExcelReportService;
    }
    public byte[] generateBudgetExcelReport(Integer year, AmountType amountType, Budget budget, String languageCode) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        return budgetExcelReportService.generateReport(wb, year, amountType, budget, languageCode);
    }

}
