package org.mbs.budgetplannerserver.util;

import org.mbs.budgetplannerserver.domain.State;
import org.mbs.budgetplannerserver.domain.Translation;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TranslationSearchHelper {
    public static final String EMPTY_STRING = "";
    private final Map<String, String> translationsMap;
    private final String language;
    private final State state;


    public TranslationSearchHelper(Iterable<Translation> translations, String language, State state) {
        this.translationsMap = StreamSupport.stream(Spliterators.spliteratorUnknownSize(translations.iterator(), Spliterator.ORDERED), true)
                .filter(t -> t.getLanguage().equals(language) && t.getState().equals(state))
                .collect(Collectors.toMap(Translation::getKey, Translation::getValue));
        this.language = language;
        this.state = state;
    }

    public Map<String, String> getTranslationsMap() {
        return translationsMap;
    }

    public String getLanguage() {
        return language;
    }

    public State getState() {
        return state;
    }

    public String getTranslationValue(String key) {
        if(StringUtils.hasText(key)) {
            String value = translationsMap.get(key);
            return value == null ? key : value;
        } else {
            return EMPTY_STRING;
        }
    }
}
