package org.mbs.budgetplannerserver.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "translation")
public class Translation extends BaseModel {
    private String modelName;
    private Long modelId;
    private String language;
    private String value;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
