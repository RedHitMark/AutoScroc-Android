package it.scroking.autoscroc.api_client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIHandler {

    public static final int USER_SERVICE = 1;
    public static final int RENT_SERVICE = 2;
    public static final int SALES_SERVICE = 3;
    public static final int EXPLORER_SERVICE = 4;
    private static final String BASE_URL = "http://scroking.ddns.net:6999/api/v1.0/";
    private static APIHandler instance;
    private Retrofit retrofit;

    private APIHandler() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static APIHandler getInstance() {
        if (instance == null) {
            instance = new APIHandler();
        }

        return instance;
    }

    public ApiServiceInterface getApiService(int apiSelector) {
        switch (apiSelector) {
            case USER_SERVICE:
                return this.retrofit.create(UserServiceInterface.class);

            case RENT_SERVICE:
                return this.retrofit.create(RentServiceInterface.class);

            case SALES_SERVICE:
                return this.retrofit.create(SalesServiceInterface.class);

            case EXPLORER_SERVICE:
                return this.retrofit.create(ExplorerServiceInterface.class);

            default:
                return null;
        }
    }


}
