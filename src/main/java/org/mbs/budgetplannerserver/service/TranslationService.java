package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.domain.TranslationTable;
import org.mbs.budgetplannerserver.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {
    private final TranslationRepository translationRepository;

    @Autowired
    public TranslationService(TranslationRepository translationRepository) {
        this.translationRepository = translationRepository;
    }

    public Iterable<TranslationTable> getTranslationTable(){
        return translationRepository.findAll();
    }
}
