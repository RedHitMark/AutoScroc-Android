package it.scroking.autoscroc.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import it.scroking.autoscroc.R;
import it.scroking.autoscroc.api_client.APIHandler;
import it.scroking.autoscroc.api_client.RentServiceInterface;
import it.scroking.autoscroc.api_client.SalesServiceInterface;
import it.scroking.autoscroc.models.BuyBody;
import it.scroking.autoscroc.models.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PurchaseService extends IntentService {

    public static final String ACTION_BUY_RENT = "BUY_RENT";
    public static final String ACTION_PURCHASE = "PURCHASE";

    private SharedPreferences sharedPreferences;


    public PurchaseService() {
        super("PurchaseService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            this.sharedPreferences = getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);

            final String action = intent.getAction();
            if (ACTION_BUY_RENT.equals(action)) {
                buyRent(intent);
            } else if (ACTION_PURCHASE.equals(action)) {
                purchase(intent);
            }
        }
    }

    private void buyRent(Intent intent) {
        RentServiceInterface rentService = (RentServiceInterface) APIHandler.getInstance().getApiService(APIHandler.RENT_SERVICE);

        BuyBody buyBody = new BuyBody();
        buyBody.token = sharedPreferences.getString("token", "");
        buyBody.uuid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        buyBody.licensePlate = intent.getStringExtra("licensePlate");

        Call<MessageResponse> call = rentService.buyRent(buyBody);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_BUY_RENT);

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("buyRentResponse", true));
                    Toast.makeText(getApplicationContext(), "Scroco effettuato con successo", Toast.LENGTH_SHORT).show();
                } else {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("buyRentResponse", false));
                    Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("buyRentResponse", false));
                t.printStackTrace();
            }
        });
    }

    private void purchase(Intent intent) {
        SalesServiceInterface salesServiceInterface = (SalesServiceInterface) APIHandler.getInstance().getApiService(APIHandler.SALES_SERVICE);

        BuyBody buyBody = new BuyBody();
        buyBody.token = sharedPreferences.getString("token", "");
        buyBody.uuid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        buyBody.licensePlate = intent.getStringExtra("licensePlate");

        Call<MessageResponse> call = salesServiceInterface.purchase(buyBody);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_PURCHASE);

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("purchaseResponse", true));
                    Toast.makeText(getApplicationContext(), "Scroco effettuato con successo", Toast.LENGTH_SHORT).show();
                } else {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("purchaseResponse", false));
                    Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("purchaseResponse", false));
                Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }


}
