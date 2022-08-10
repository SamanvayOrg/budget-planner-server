package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.contract.TranslationContract;
import org.mbs.budgetplannerserver.domain.Translation;
import org.mbs.budgetplannerserver.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
public class TranslationService {
    private final TranslationRepository translationRepository;
    private final StateService stateService;
    private final UserService userService;


    @Autowired
    public TranslationService(TranslationRepository translationRepository, StateService stateService, UserService userService) {
        this.translationRepository = translationRepository;
        this.stateService = stateService;
        this.userService = userService;
    }

    @Transactional
    public Iterable<Translation> getTranslations() {
        return translationRepository.findByStateId(userService.getUser().getMunicipality().getState().getId());
    }

    @Transactional
    public Translation save(TranslationContract translationContract) {
        Translation translation = new Translation();
        translation.setKey(translationContract.getKey());
        translation.setValue(translationContract.getValue());
        translation.setLanguage(translationContract.getLanguage());
        translation.setState(stateService.getById(translationContract.getStateId()));
        return translationRepository.save(translation);
    }

    @Transactional
    public Translation update(Long translationId, TranslationContract translationContract) {
        Translation translation = translationRepository.findById(translationId).orElseThrow(EntityNotFoundException::new);
        translation.setKey(translationContract.getKey());
        translation.setValue(translationContract.getValue());
        translation.setLanguage(translationContract.getLanguage());
        translation.setState(stateService.getById(translationContract.getStateId()));
        return translationRepository.save(translation);
    }

    public Translation delete(Long translationId) {
        Translation translation = translationRepository.findById(translationId).orElseThrow(EntityNotFoundException::new);
        translationRepository.delete(translation);
        return translation;
    }

}
