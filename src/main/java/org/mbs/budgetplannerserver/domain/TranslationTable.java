package org.mbs.budgetplannerserver.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "translation_table")
public class TranslationTable  extends BaseModel{
private String table_name;
private Long table_id;
private String language;
private String value;

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public Long getTable_id() {
        return table_id;
    }

    public void setTable_id(Long table_id) {
        this.table_id = table_id;
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
