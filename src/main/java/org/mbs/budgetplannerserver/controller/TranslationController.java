package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.TranslationContract;
import org.mbs.budgetplannerserver.domain.JsonObject;
import org.mbs.budgetplannerserver.domain.Translation;
import org.mbs.budgetplannerserver.mapper.TranslationContractMapper;
import org.mbs.budgetplannerserver.service.TranslationService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class TranslationController {
    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @RequestMapping(value = "/api/translations", method = GET)
    public JsonObject getTranslation() {
        return new TranslationContractMapper().map(translationService.getTranslations());
    }

    @RequestMapping(value = "api/translation", method = POST)
    public TranslationContract updateTranslation(@RequestBody Translation translation){
        return new TranslationContractMapper().fromTranslation(translationService.save(translation));
    }

}
