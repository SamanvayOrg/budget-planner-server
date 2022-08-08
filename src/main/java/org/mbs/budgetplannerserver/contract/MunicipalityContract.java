package org.mbs.budgetplannerserver.contract;

public class MunicipalityContract {

    private Long id;
    private String name;
    private String state;
    private String cityClass;

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCityClass() {
        return cityClass;
    }

    public void setCityClass(String cityClass) {
        this.cityClass = cityClass;
    }

}