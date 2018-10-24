package com.example.siddhiworld.jsonparsestages;

public class Stages {

    private String name;
    private String positiveO2;
    private String negativeO2;
    private String altitude;
    private String method;
    private String description;

    public Stages(String name1, String positiveO2, String negativeO2, String altitude, String method, String description1) {
        this.name=name1;
        this.positiveO2=positiveO2;
        this.negativeO2=negativeO2;
        this.altitude=altitude;
        this.method=method;
        this.description=description1;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPositiveO2() {
        return positiveO2;
    }

    public void setPositiveO2(String positiveO2) {
        this.positiveO2 = positiveO2;
    }

    public String getNegativeO2() {
        return negativeO2;
    }

    public void setNegativeO2(String negativeO2) {
        this.negativeO2 = negativeO2;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return ",\n Name1 =" + name +
                ",\n positiveO2 =" + positiveO2 +
                ",\n negativeO2=" + negativeO2 +
                ",\n altitude =" + altitude +
                ",\n method =" + method +
                ",\n description1=" + description;
    }
}
