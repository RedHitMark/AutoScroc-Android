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

public interface SalesServiceInterface extends ApiServiceInterface {
    @GET("sales/")
    Call<List<Offer>> getSales(@Query("idPage") int idPage);

    @POST("create-sale/")
    Call<MessageResponse> createSale(@Body Offer sale);

    @POST("user-purchases/")
    Call<List<Offer>> getUserPurchases(@Body CheckUserBody checkUserBody);

    @POST("purchase/")
    Call<MessageResponse> purchase(@Body BuyBody buyBody);
}
