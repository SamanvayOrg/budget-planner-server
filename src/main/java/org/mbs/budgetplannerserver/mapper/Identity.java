package org.mbs.budgetplannerserver.mapper;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "user_id",
    "provider",
    "connection",
    "isSocial"
})
public class Identity {

    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("provider")
    private String provider;
    @JsonProperty("connection")
    private String connection;
    @JsonProperty("isSocial")
    private boolean isSocial;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Identity() {
    }

    /**
     * 
     * @param isSocial
     * @param provider
     * @param connection
     * @param userId
     */
    public Identity(String userId, String provider, String connection, boolean isSocial) {
        super();
        this.userId = userId;
        this.provider = provider;
        this.connection = connection;
        this.isSocial = isSocial;
    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("provider")
    public String getProvider() {
        return provider;
    }

    @JsonProperty("provider")
    public void setProvider(String provider) {
        this.provider = provider;
    }

    @JsonProperty("connection")
    public String getConnection() {
        return connection;
    }

    @JsonProperty("connection")
    public void setConnection(String connection) {
        this.connection = connection;
    }

    @JsonProperty("isSocial")
    public boolean isIsSocial() {
        return isSocial;
    }

    @JsonProperty("isSocial")
    public void setIsSocial(boolean isSocial) {
        this.isSocial = isSocial;
    }

}
