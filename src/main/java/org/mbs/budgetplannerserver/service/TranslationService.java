package org.mbs.budgetplannerserver.service;

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
    public Iterable<Translation> getTranslations() {
        return translationRepository.findAll();
    }

    @Transactional
    public Translation save(Translation translation){
        return translationRepository.save(translation);
    }
}
