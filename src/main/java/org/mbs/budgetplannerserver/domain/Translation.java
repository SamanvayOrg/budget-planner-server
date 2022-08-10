package org.mbs.budgetplannerserver.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "translation", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueKeyStateAndLanguage", columnNames = { "key", "state_id", "language" })
})
@SQLDelete(sql = "UPDATE translation SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class Translation extends BaseModel {
    private String key;
    private String language;
    private String value;

    @ManyToOne(targetEntity = State.class)
    @JoinColumn(name = "state_id")
    private State state;

    public String getKey() {
        return key;
    }

    public void setKey(String modelName) {
        this.key = modelName;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
