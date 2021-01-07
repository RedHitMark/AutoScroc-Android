package it.scroking.autoscroc.api_client;

import java.util.List;

import it.scroking.autoscroc.models.Car;
import it.scroking.autoscroc.models.CarBrand;
import it.scroking.autoscroc.models.CarModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ExplorerServiceInterface extends ApiServiceInterface {
    @GET("explorer/brands")
    Call<List<CarBrand>> getBrands();

    @GET("explorer/models")
    Call<List<CarModel>> getModels(@Query("idBrand") int idBrand);

    @GET("explorer/cars")
    Call<List<Car>> getCars(@Query("idModel") int idModels);

    @GET("explorer/car")
    Call<Car> getCar(@Query("id") int id);
}
