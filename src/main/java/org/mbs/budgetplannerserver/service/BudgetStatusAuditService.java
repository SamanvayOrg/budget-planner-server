package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.BudgetStatus;
import org.mbs.budgetplannerserver.domain.BudgetStatusAudit;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.BudgetStatusAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class BudgetStatusAuditService {

    private final UserService userService;
    private final BudgetStatusAuditRepository budgetStatusAuditRepository;

    public BudgetStatusAuditService(UserService userService, BudgetStatusAuditRepository budgetStatusAuditRepository) {
        this.userService = userService;
        this.budgetStatusAuditRepository = budgetStatusAuditRepository;
    }

    public BudgetStatusAudit createAuditEntry(Budget budget, BudgetStatus currentBudgetStatus) {
        return budgetStatusAuditRepository.save(buildBudgetStatusAudit(budget, currentBudgetStatus));
    }

    public BudgetStatusAudit buildBudgetStatusAudit(Budget budget, BudgetStatus currentBudgetStatus) {
        User user = userService.getUser();
        Optional<BudgetStatusAudit> prevBudgetStatusAudit = budgetStatusAuditRepository.findFirstByBudgetOrderByCreatedAtDesc(budget);
        BudgetStatus prevBudgetStatus = prevBudgetStatusAudit.isPresent() ? prevBudgetStatusAudit.get().getCurrentBudgetStatus(): BudgetStatus.Draft;
        if(prevBudgetStatus.isTransitionAllowed(currentBudgetStatus)) {
            return new BudgetStatusAudit(budget, user, currentBudgetStatus, prevBudgetStatus);
        } else {
            throw new ResponseStatusException(BAD_REQUEST, "BudgetStatus change is not allowed from " + prevBudgetStatus + " to "+ currentBudgetStatus);
        }
    }

    private Supplier<ResponseStatusException> NOT_FOUND(String entity) {
        return () -> new ResponseStatusException(NOT_FOUND, entity+" not found");
    }
}
