package com.application.model.entities;

import java.util.UUID;

public class City {
    private UUID cityId;
    private String cityName;
    private String cityZIPCode;

    public City(
            UUID cityId,
            String cityName,
            String cityZIPCode) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.cityZIPCode = cityZIPCode;
    }

    public UUID getCityId() {
        return cityId;
    }

    public void setCityId(UUID cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityZIPCode() {
        return cityZIPCode;
    }

    public void setCityZIPCode(String cityZIPCode) {
        this.cityZIPCode = cityZIPCode;
    }

}
