package org.mbs.budgetplannerserver.domain;

import org.springframework.core.convert.converter.Converter;

public class BudgetStatusStringToEnumConverter implements Converter<String, BudgetStatus> {
    @Override
    public BudgetStatus convert(String source) {
        return BudgetStatus.from(source.toUpperCase());
    }
}
