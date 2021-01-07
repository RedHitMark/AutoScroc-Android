package it.scroking.autoscroc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.scroking.autoscroc.models.Car;
import it.scroking.autoscroc.models.CarBrand;
import it.scroking.autoscroc.models.CarModel;

public class CacheManager implements Serializable {
    public static List<CarBrand> carBrands = new ArrayList<>();
    public static HashMap<Integer, List<CarModel>> carModelsMap= new HashMap<>();
    public static HashMap<Integer, List<Car>> carsMap= new HashMap<>();
}
