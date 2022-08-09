package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.TranslationContract;
import org.mbs.budgetplannerserver.domain.JsonObject;
import org.mbs.budgetplannerserver.domain.Translation;

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
        translationContract.setKey(translation.getKey());
        translationContract.setValue(translation.getValue());
        translationContract.setStateId(translation.getState().getId());
        translationContract.setLanguage(translation.getLanguage());
        return translationContract;
    }
}