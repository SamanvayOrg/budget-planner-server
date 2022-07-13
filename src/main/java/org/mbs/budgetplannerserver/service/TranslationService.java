package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.contract.TranslationContract;
import org.mbs.budgetplannerserver.domain.JsonObject;
import org.mbs.budgetplannerserver.domain.Translation;
import org.mbs.budgetplannerserver.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TranslationService {
    private final TranslationRepository translationRepository;


    @Autowired
    public TranslationService(TranslationRepository translationRepository) {
        this.translationRepository = translationRepository;
    }

    @Transactional
    public JsonObject getTranslations() {
        Iterable<Translation> translations = translationRepository.findAll();
        JsonObject jsonObject = new JsonObject();
        translations.forEach(translation -> {
            jsonObject.with(translation.getModelName(), translation.getValue());
        });
        return jsonObject;
    }
}
