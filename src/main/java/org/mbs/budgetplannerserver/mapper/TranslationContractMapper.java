package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.TranslationContract;
import org.mbs.budgetplannerserver.domain.Translation;

public class TranslationContractMapper {

    public TranslationContract map(Translation translation) {
        TranslationContract translationContract = new TranslationContract();
        translationContract.setModelName(translation.getModelName());
        translationContract.setValue(translation.getValue());
        return translationContract;
    }
}
