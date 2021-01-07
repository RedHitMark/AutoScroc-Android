package it.scroking.autoscroc.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import it.scroking.autoscroc.R;
import it.scroking.autoscroc.api_client.APIHandler;
import it.scroking.autoscroc.api_client.RentServiceInterface;
import it.scroking.autoscroc.api_client.SalesServiceInterface;
import it.scroking.autoscroc.models.BuyBody;
import it.scroking.autoscroc.models.MessageResponse;
import it.scroking.autoscroc.models.Offer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreationService extends IntentService {

    public static final String ACTION_CREATE_RENT = "CREATE_RENT";
    public static final String ACTION_CREATE_SALE = "CREATE_SALE";

    private SharedPreferences sharedPreferences;


    public CreationService() {
        super("CreationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            this.sharedPreferences = getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);

            final String action = intent.getAction();
            if (ACTION_CREATE_RENT.equals(action)) {
                createRent(intent);
            } else if (ACTION_CREATE_SALE.equals(action)) {
                createSale(intent);
            }
        }
    }

    private void createRent(Intent intent) {
        RentServiceInterface rentService = (RentServiceInterface) APIHandler.getInstance().getApiService(APIHandler.RENT_SERVICE);

        Offer rent = new Offer();
        rent.setLicensePlate(intent.getStringExtra("licensePlate"));
        rent.setKm(intent.getIntExtra("km", 1));
        rent.setPrice(intent.getDoubleExtra("price",1));
        rent.setMatriculationYear(intent.getIntExtra("matriculationYear", 1900));
        rent.setId(intent.getIntExtra("id", 1));

        Log.d("TAG", intent.getIntExtra("id", 1)+"");

        Call<MessageResponse> call = rentService.createRent(rent);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_CREATE_RENT);

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("createRentSuccess", true));
                } else {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("createRentSuccess", false));
                    Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("createRentSuccess", false));
                t.printStackTrace();
            }
        });
    }

    private void createSale(Intent intent) {
        SalesServiceInterface salesServiceInterface = (SalesServiceInterface) APIHandler.getInstance().getApiService(APIHandler.SALES_SERVICE);

        Offer sale = new Offer();
        sale.setLicensePlate(intent.getStringExtra("licensePlate"));
        sale.setKm(intent.getIntExtra("km", 1));
        sale.setPrice(intent.getDoubleExtra("price",1));
        sale.setMatriculationYear(intent.getIntExtra("matriculationYear", 1900));
        sale.setId(intent.getIntExtra("id", 1));

        Call<MessageResponse> call = salesServiceInterface.createSale(sale);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_CREATE_SALE);

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("createSaleSuccess", true));
                } else {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("createSaleSuccess", false));
                    Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("createSaleSuccess", false));
                Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

}
