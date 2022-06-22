package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.domain.BudgetLine;
import org.mbs.budgetplannerserver.repository.DetailedHeadRepository;
import org.mbs.budgetplannerserver.repository.FunctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetLineService {

    private FunctionRepository functionRepository;
    private DetailedHeadRepository detailedHeadRepository;

    @Autowired
    public BudgetLineService(FunctionRepository functionRepository, DetailedHeadRepository detailedHeadRepository) {
        this.functionRepository = functionRepository;
        this.detailedHeadRepository = detailedHeadRepository;
    }

    public BudgetLine createBudgetLine(String functionCode, String detailedHeadCode, String name) {
        BudgetLine budgetLine = new BudgetLine();
        budgetLine.setFunction(functionRepository.findByFullCode(functionCode));
        budgetLine.setDetailedHead(detailedHeadRepository.findByFullCode(detailedHeadCode));
        budgetLine.setName(name);
        return budgetLine;
    }
}
