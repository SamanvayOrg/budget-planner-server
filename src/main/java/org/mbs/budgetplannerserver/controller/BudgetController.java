package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.BudgetContract;
import org.mbs.budgetplannerserver.contract.BudgetStatusAuditContract;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.BudgetStatus;
import org.mbs.budgetplannerserver.domain.Year;
import org.mbs.budgetplannerserver.mapper.BudgetContractMapper;
import org.mbs.budgetplannerserver.mapper.BudgetStatusAuditContractMapper;
import org.mbs.budgetplannerserver.service.BudgetLineService;
import org.mbs.budgetplannerserver.service.BudgetService;
import org.mbs.budgetplannerserver.service.BudgetStatusAuditService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class BudgetController {
    private final BudgetService budgetService;
    private final BudgetLineService budgetLineService;
    private final BudgetStatusAuditService budgetStatusAuditService;

    public BudgetController(BudgetService budgetService, BudgetLineService budgetLineService,
                            BudgetStatusAuditService budgetStatusAuditService) {
        this.budgetService = budgetService;
        this.budgetLineService = budgetLineService;
        this.budgetStatusAuditService = budgetStatusAuditService;
    }

    @RequestMapping(value = "/api/budget", method = GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('read')") // ✨ 👈 New line ✨
    public BudgetContract getBudgetByYear(@RequestParam("year") Integer year) {
        Budget budget = budgetService.getBudgetForFinancialYear(new Year(year).getYear()).orElseThrow(NOT_FOUND());
        return new BudgetContractMapper().map(budget);
    }

    @RequestMapping(value = "/api/budget", method = POST)
    @PreAuthorize("hasAuthority('write')") // ✨ 👈 New line ✨
    public void create(@RequestParam("year") Integer year) {
        budgetService.getOrCreate(year, 0, true);
    }

    @RequestMapping(value = "/api/budget/actuals", method = POST)
    @PreAuthorize("hasAuthority('write')") // ✨ 👈 New line ✨
    public void updateActuals(@RequestBody BudgetContract budgetContract) {
        Budget budget = budgetService.getOrCreate(Integer.parseInt(budgetContract.getBudgetYear().substring(0, 4)), 2, false);
        budgetService.save(new BudgetContractMapper().withUpdatedActuals(budgetContract, budget, budgetLineService));
    }

    @RequestMapping(value = "/api/budget/estimates", method = POST)
    @PreAuthorize("hasAuthority('write')") // ✨ 👈 New line ✨
    public void updateEstimates(@RequestBody BudgetContract budgetContract) {
        Budget budget = budgetService.getOrCreate(Integer.parseInt(budgetContract.getBudgetYear().substring(0, 4)), 1, false);
        budgetService.save(new BudgetContractMapper().withUpdatedEstimates(budgetContract, budget, budgetLineService));
    }

    @RequestMapping(value = "/api/budget/budgeted", method = POST)
    @PreAuthorize("hasAuthority('write')") // ✨ 👈 New line ✨
    public BudgetContract updateBudgeted(@RequestBody BudgetContract budgetContract) {
        Budget budget = budgetService.findById(budgetContract.getId()).orElseThrow(NOT_FOUND());
        return new BudgetContractMapper().map(budgetService
                .save(new BudgetContractMapper().withUpdatedBudgeted(budgetContract, budget, budgetLineService)));
    }

    @RequestMapping(value = "/api/budget/current", method = GET)
    @PreAuthorize("hasAuthority('read')") // ✨ 👈 New line ✨
    public BudgetContract currentBudget() {
        Budget budget = budgetService.getCurrentBudget().orElseThrow(NOT_FOUND());
        return new BudgetContractMapper().map(budget);
    }
    @RequestMapping(value = "/api/budget/latest", method = GET)
    @PreAuthorize("hasAuthority('read')") // ✨ 👈 New line ✨
    public BudgetContract latestBudget() {
        Budget budget = budgetService.getLatestBudget().orElseThrow(NOT_FOUND());
        return new BudgetContractMapper().map(budget);
    }

    @RequestMapping(value = "/api/budgets", method = GET)
    @PreAuthorize("hasAuthority('read')") // ✨ 👈 New line ✨
    public List<BudgetContract> allBudgets() {
        List<Budget> budgets = budgetService.getAllBudgets();
        return budgets.stream().sorted(Comparator.comparingInt(Budget::getFinancialYear))
                .map(budget -> new BudgetContractMapper().map(budget)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/api/budget", method = DELETE)
    @ResponseBody
    @PreAuthorize("hasAuthority('write')") // ✨ 👈 New line ✨
    public BudgetContract deleteBudgetByYear(@RequestParam("year") Integer year) {
        Budget budget = budgetService.deleteBudgetForFinancialYear(new Year(year).getYear());
        return new BudgetContractMapper().map(budget);
    }

    @RequestMapping(value = "/api/budget/{id}/properties", method = PUT)
    @PreAuthorize("hasAnyAuthority('write')") // ✨ 👈 New line ✨
    public BudgetContract updatePopulationAndOpeningBalance(@PathVariable Long id, @RequestBody BudgetPropertiesContract budgetPropertiesContract) {
        Budget budget = budgetService.findById(id).orElseThrow(NOT_FOUND());
        return new BudgetContractMapper().map(budgetService.updateProperties(budget, budgetPropertiesContract));
    }

    @RequestMapping(value = "/api/budget/{id}/status", method = PUT)
    @PreAuthorize("hasAnyAuthority('write')") // ✨ 👈 New line ✨
    public BudgetStatusAuditContract updateBudgetStatus(@PathVariable Long id, @RequestParam BudgetStatus budgetStatus) {
        Budget budget = budgetService.findById(id).orElseThrow(NOT_FOUND());
        return new BudgetStatusAuditContractMapper().fromBudgetStatusAudit(budgetStatusAuditService.createAuditEntry(budget, budgetStatus));
    }

    private Supplier<ResponseStatusException> NOT_FOUND() {
        return () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
    }
}
