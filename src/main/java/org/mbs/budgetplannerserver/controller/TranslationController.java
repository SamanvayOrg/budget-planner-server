package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.TranslationContract;
import org.mbs.budgetplannerserver.domain.JsonObject;
import org.mbs.budgetplannerserver.mapper.TranslationContractMapper;
import org.mbs.budgetplannerserver.service.TranslationService;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('read')") // ✨ 👈 New line ✨
    public JsonObject getTranslations() {
        return new TranslationContractMapper().map(translationService.getTranslations());
    }

    @RequestMapping(value = "api/translation", method = POST)
    @PreAuthorize("hasAuthority('write')") // ✨ 👈 New line ✨
    public TranslationContract createTranslation(@RequestBody TranslationContract translationContract) {
        return new TranslationContractMapper().fromTranslation(translationService.save(translationContract));
    }

}
