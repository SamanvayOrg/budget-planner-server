package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.domain.Translation;
import org.mbs.budgetplannerserver.service.TranslationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
@RestController
public class TranslationController {
    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @RequestMapping(value = "/api/translations", method = GET)
    public Iterable<Translation> getTranslationTable() {
        return  translationService.getTranslationTable();
    }

}
