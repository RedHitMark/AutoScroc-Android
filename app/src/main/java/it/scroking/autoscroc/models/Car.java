package it.scroking.autoscroc.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Car implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("idBrand")
    @Expose
    private int idBrand;
    @SerializedName("idModel")
    @Expose
    private int idModel;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("carType")
    @Expose
    private String carType;
    @SerializedName("engineType")
    @Expose
    private String engineType;
    @SerializedName("doors")
    @Expose
    private int doors;
    @SerializedName("trasmission")
    @Expose
    private String trasmission;
    @SerializedName("hp")
    @Expose
    private int hp;
    @SerializedName("kw")
    @Expose
    private Object kw;
    @SerializedName("torque")
    @Expose
    private int torque;
    @SerializedName("cc")
    @Expose
    private int cc;
    @SerializedName("numCylinders")
    @Expose
    private int numCylinders;
    @SerializedName("cylindersType")
    @Expose
    private String cylindersType;
    @SerializedName("topSpeed")
    @Expose
    private int topSpeed;
    @SerializedName("acc")
    @Expose
    private double acc;
    @SerializedName("weight")
    @Expose
    private int weight;
    @SerializedName("img")
    @Expose
    private String img;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdBrand() {
        return idBrand;
    }

    public void setIdBrand(int idBrand) {
        this.idBrand = idBrand;
    }

    public int getIdModel() {
        return idModel;
    }

    public void setIdModel(int idModel) {
        this.idModel = idModel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public int getDoors() {
        return doors;
    }

    public void setDoors(int doors) {
        this.doors = doors;
    }

    public String getTrasmission() {
        return trasmission;
    }

    public void setTrasmission(String trasmission) {
        this.trasmission = trasmission;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public Object getKw() {
        return kw;
    }

    public void setKw(Object kw) {
        this.kw = kw;
    }

    public int getTorque() {
        return torque;
    }

    public void setTorque(int torque) {
        this.torque = torque;
    }

    public int getCc() {
        return cc;
    }

    public void setCc(int cc) {
        this.cc = cc;
    }

    public int getNumCylinders() {
        return numCylinders;
    }

    public void setNumCylinders(int numCylinders) {
        this.numCylinders = numCylinders;
    }

    public String getCylindersType() {
        return cylindersType;
    }

    public void setCylindersType(String cylindersType) {
        this.cylindersType = cylindersType;
    }

    public int getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(int topSpeed) {
        this.topSpeed = topSpeed;
    }

    public double getAcc() {
        return acc;
    }

    public void setAcc(double acc) {
        this.acc = acc;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return name;

    }
}
