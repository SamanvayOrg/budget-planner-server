package org.mbs.budgetplannerserver.contract;

public class StateContract {

    public String name;

    public String languages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }
}
