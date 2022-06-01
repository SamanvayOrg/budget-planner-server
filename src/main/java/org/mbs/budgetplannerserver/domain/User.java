package org.mbs.budgetplannerserver.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "login_user")
public class User extends BaseModel{
    private String userName;
    private String name;

    @ManyToOne(targetEntity = Municipality.class)
    @JoinColumn(name = "municipality_id")
    private Municipality municipality;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public State getState() {
        return getMunicipality().getState();
    }
}
