package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.TranslationContract;
import org.mbs.budgetplannerserver.domain.JsonObject;
import org.mbs.budgetplannerserver.domain.Translation;

public class TranslationContractMapper {

    public JsonObject map(Iterable<Translation> translations) {
        JsonObject jsonObject = new JsonObject();
        translations.forEach(translation -> {
             jsonObject.with(translation.getModelName(), translation.getValue());
        });
        return jsonObject;
    }

    public TranslationContract fromTranslation(Translation translation){
        TranslationContract translationContract = new TranslationContract();
        translationContract.setModelName(translation.getModelName());
        translationContract.setValue(translation.getValue());
        return translationContract;
    }
}