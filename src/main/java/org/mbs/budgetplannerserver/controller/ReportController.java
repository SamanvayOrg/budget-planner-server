package org.mbs.budgetplannerserver.controller;

import org.apache.commons.lang3.StringUtils;
import org.mbs.budgetplannerserver.domain.AmountType;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.Year;
import org.mbs.budgetplannerserver.export.service.ReportService;
import org.mbs.budgetplannerserver.service.BudgetService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.function.Supplier;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ReportController {

    private final BudgetService budgetService;
    private final ReportService reportService;

    public ReportController(BudgetService budgetService, ReportService reportService) {
        this.budgetService = budgetService;
        this.reportService = reportService;
    }

    @RequestMapping(value = "/api/report/budget", method = GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('read')") // ✨ 👈 New line ✨
    public ResponseEntity<byte[]> getBudgetReportByYear(@RequestParam("year") Integer year, @RequestParam("amountType") AmountType amountType) throws IOException {
        Budget budget = budgetService.getBudgetForFinancialYear(new Year(year).getYear()).orElseThrow(NOT_FOUND());
        byte[] report = reportService.generateBudgetExcelReport(year, amountType, budget);
        return createResponseEntity(report, generateReportNameString(year, amountType, budget));
    }

    private String generateReportNameString(Integer year, AmountType amountType, Budget budget) {
        return StringUtils.joinWith("_", budget.getMunicipality().getName(), amountType, year);
    }

    private ResponseEntity<byte[]> createResponseEntity(byte[] report,
            String fileName)  {
        String headerValue = String.format("attachment; filename=\"%s\"", fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(report);
    }

    private Supplier<ResponseStatusException> NOT_FOUND() {
        return () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
    }

}
