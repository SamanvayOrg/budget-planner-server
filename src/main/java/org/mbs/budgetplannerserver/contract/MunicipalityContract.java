package org.mbs.budgetplannerserver.contract;

public class MunicipalityContract {
    private String name;
    private String state;
    private String cityClass;
    private Long population;

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

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }
}
