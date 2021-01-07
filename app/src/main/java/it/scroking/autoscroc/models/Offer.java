package it.scroking.autoscroc.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Offer extends Car implements Serializable {

    @SerializedName("licensePlate")
    @Expose
    private String licensePlate;
    @SerializedName("matriculationYear")
    @Expose
    private int matriculationYear;
    @SerializedName("price")
    @Expose
    private double price;
    @SerializedName("km")
    @Expose
    private int km;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getMatriculationYear() {
        return matriculationYear;
    }

    public void setMatriculationYear(int matriculationYear) {
        this.matriculationYear = matriculationYear;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }
}