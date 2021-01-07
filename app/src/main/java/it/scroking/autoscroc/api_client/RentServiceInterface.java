package it.scroking.autoscroc.api_client;

import java.util.List;

import it.scroking.autoscroc.models.BuyBody;
import it.scroking.autoscroc.models.CheckUserBody;
import it.scroking.autoscroc.models.MessageResponse;
import it.scroking.autoscroc.models.Offer;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RentServiceInterface extends ApiServiceInterface {
    @GET("rent/")
    Call<List<Offer>> getRents(@Query("idPage") int idPage);

    @POST("create-rent/")
    Call<MessageResponse> createRent(@Body Offer rent);

    @POST("user-rents/")
    Call<List<Offer>> getRents(@Body CheckUserBody checkUserBody);

    @POST("buy-rent/")
    Call<MessageResponse> buyRent(@Body BuyBody buyBody);

}
