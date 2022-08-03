
package org.mbs.budgetplannerserver.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "created_at",
    "email",
    "email_verified",
    "identities",
    "name",
    "nickname",
    "picture",
    "updated_at",
    "user_id",
    "last_ip",
    "last_login",
    "logins_count",
    "blocked_for",
    "guardian_authenticators"
})
public class Auth0UserResponse {

    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("email")
    private String email;
    @JsonProperty("email_verified")
    private boolean emailVerified;
    @JsonProperty("identities")
    private List<Identity> identities = new ArrayList<Identity>();
    @JsonProperty("name")
    private String name;
    @JsonProperty("nickname")
    private String nickname;
    @JsonProperty("picture")
    private String picture;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("last_ip")
    private String lastIp;
    @JsonProperty("last_login")
    private String lastLogin;
    @JsonProperty("logins_count")
    private long loginsCount;
    @JsonProperty("blocked_for")
    private List<Object> blockedFor = new ArrayList<Object>();
    @JsonProperty("guardian_authenticators")
    private List<Object> guardianAuthenticators = new ArrayList<Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Auth0UserResponse() {
    }

    /**
     * 
     * @param lastLogin
     * @param loginsCount
     * @param lastIp
     * @param blockedFor
     * @param userId
     * @param picture
     * @param createdAt
     * @param emailVerified
     * @param identities
     * @param name
     * @param nickname
     * @param guardianAuthenticators
     * @param email
     * @param updatedAt
     */
    public Auth0UserResponse(String createdAt, String email, boolean emailVerified, List<Identity> identities, String name, String nickname, String picture, String updatedAt, String userId, String lastIp, String lastLogin, long loginsCount, List<Object> blockedFor, List<Object> guardianAuthenticators) {
        super();
        this.createdAt = createdAt;
        this.email = email;
        this.emailVerified = emailVerified;
        this.identities = identities;
        this.name = name;
        this.nickname = nickname;
        this.picture = picture;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.lastIp = lastIp;
        this.lastLogin = lastLogin;
        this.loginsCount = loginsCount;
        this.blockedFor = blockedFor;
        this.guardianAuthenticators = guardianAuthenticators;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("email_verified")
    public boolean isEmailVerified() {
        return emailVerified;
    }

    @JsonProperty("email_verified")
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @JsonProperty("identities")
    public List<Identity> getIdentities() {
        return identities;
    }

    @JsonProperty("identities")
    public void setIdentities(List<Identity> identities) {
        this.identities = identities;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("nickname")
    public String getNickname() {
        return nickname;
    }

    @JsonProperty("nickname")
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @JsonProperty("picture")
    public String getPicture() {
        return picture;
    }

    @JsonProperty("picture")
    public void setPicture(String picture) {
        this.picture = picture;
    }

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("last_ip")
    public String getLastIp() {
        return lastIp;
    }

    @JsonProperty("last_ip")
    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    @JsonProperty("last_login")
    public String getLastLogin() {
        return lastLogin;
    }

    @JsonProperty("last_login")
    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    @JsonProperty("logins_count")
    public long getLoginsCount() {
        return loginsCount;
    }

    @JsonProperty("logins_count")
    public void setLoginsCount(long loginsCount) {
        this.loginsCount = loginsCount;
    }

    @JsonProperty("blocked_for")
    public List<Object> getBlockedFor() {
        return blockedFor;
    }

    @JsonProperty("blocked_for")
    public void setBlockedFor(List<Object> blockedFor) {
        this.blockedFor = blockedFor;
    }

    @JsonProperty("guardian_authenticators")
    public List<Object> getGuardianAuthenticators() {
        return guardianAuthenticators;
    }

    @JsonProperty("guardian_authenticators")
    public void setGuardianAuthenticators(List<Object> guardianAuthenticators) {
        this.guardianAuthenticators = guardianAuthenticators;
    }

}
