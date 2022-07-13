package org.mbs.budgetplannerserver.mapper;

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
}
