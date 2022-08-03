package org.mbs.budgetplannerserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mbs.budgetplannerserver.service.MunicipalityService;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "login_user")
public class User extends BaseModel {

    private String email;
    private String userName;
    private String name;
    private Boolean isAdmin;

    @ManyToOne(targetEntity = Municipality.class)
    @JoinColumn(name = "municipality_id")
    private Municipality municipality;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    @JsonIgnore
    public State getState() {
        return getMunicipality().getState();
    }

}
