package org.mbs.budgetplannerserver.contract;

public class UserContract {

    private Long id;
    private String email;
    private String name;
    private String userName;
    private Long municipalityId;
    private Boolean isAdmin = false;

    // Optional. When absent the role is derived from isAdmin, which is how the Super
    // Admin's create-an-admin endpoint and every pre-existing client still behave. When
    // present it is authoritative, and isAdmin is derived from it instead — so a client
    // cannot request the Admin role while passing isAdmin=false to dodge the privilege
    // check. See UserService#roleNameFor.
    private String role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
