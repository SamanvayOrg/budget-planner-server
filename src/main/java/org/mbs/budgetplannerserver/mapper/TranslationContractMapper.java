package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.TranslationContract;
import org.mbs.budgetplannerserver.domain.JsonObject;
import org.mbs.budgetplannerserver.domain.Translation;

import java.util.ArrayList;
import java.util.List;

public class TranslationContractMapper {

    public JsonObject map(Iterable<Translation> translations) {
        JsonObject jsonObject = new JsonObject();
        translations.forEach(translation -> {
             jsonObject.with(translation.getKey(), translation.getValue());
        });
        return jsonObject;
    }

    public TranslationContract fromTranslation(Translation translation){
        TranslationContract translationContract = new TranslationContract();
        translationContract.setId(translation.getId());
        translationContract.setKey(translation.getKey());
        translationContract.setValue(translation.getValue());
        translationContract.setStateId(translation.getState().getId());
        translationContract.setLanguage(translation.getLanguage());
        return translationContract;
    }

    public Iterable<TranslationContract> getAll(Iterable<Translation> translations) {
        List<TranslationContract> translationContracts = new ArrayList<>();
        translations.forEach(translation -> {
            translationContracts.add(fromTranslation(translation));
        });
        return translationContracts;
    }
}