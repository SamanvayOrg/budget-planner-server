package org.mbs.budgetplannerserver.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mbs.budgetplannerserver.domain.code.Language;

import java.util.Arrays;
import java.util.List;

public class StateContract {

    public Long id;
    public String name;
    public List<Language> languages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.languages = Arrays.asList(mapper.readValue(languages, Language[].class));
    }
}
