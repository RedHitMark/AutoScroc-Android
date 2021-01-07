package it.scroking.autoscroc.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import it.scroking.autoscroc.CacheManager;
import it.scroking.autoscroc.ObjectSerializer;
import it.scroking.autoscroc.RentSaleDatabaseHelper;
import it.scroking.autoscroc.R;
import it.scroking.autoscroc.api_client.APIHandler;
import it.scroking.autoscroc.api_client.ExplorerServiceInterface;
import it.scroking.autoscroc.api_client.RentServiceInterface;
import it.scroking.autoscroc.api_client.SalesServiceInterface;
import it.scroking.autoscroc.models.Car;
import it.scroking.autoscroc.models.CarBrand;
import it.scroking.autoscroc.models.CarModel;
import it.scroking.autoscroc.models.CheckUserBody;
import it.scroking.autoscroc.models.Offer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SyncService extends IntentService {

    public static final String ACTION_SYNC_RENT = "SYNC_RENT";
    public static final String ACTION_SYNC_USER_RENT = "SYNC_USER_RENT";

    public static final String ACTION_SYNC_SALES = "SYNC_ON_SALE";

    public static final String ACTION_SYNC_BRAND = "SYNC_BRAND";
    public static final String ACTION_SYNC_MODELS = "SYNC_MODELS";
    public static final String ACTION_SYNC_CARS = "SYNC CARS";
    public static final String ACTION_SYNC_CAR = "SYNC CAR";
    public static final String ACTION_SYNC_USER_PURCHASES = "SYNC_USER_PURCHASES";

    private SharedPreferences userSharedPreferences;
    private SharedPreferences cacheSharedPreferences;


    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            this.userSharedPreferences = getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);
            this.cacheSharedPreferences = getApplicationContext().getSharedPreferences("Cache", Context.MODE_PRIVATE);

            final String action = intent.getAction();
            if (ACTION_SYNC_RENT.equals(action)) {
                syncRents(intent);
            } else if (ACTION_SYNC_USER_RENT.equals(action)) {
                syncUserRents(intent);
            } else if (ACTION_SYNC_SALES.equals(action)) {
                syncOnSale(intent);
            } else if (ACTION_SYNC_USER_PURCHASES.equals(action)) {
                syncUserPurchases(intent);
            } else if (ACTION_SYNC_BRAND.equals(action)) {
                syncBrand(intent);
            } else if (ACTION_SYNC_MODELS.equals(action)) {
                syncModels(intent);
            } else if (ACTION_SYNC_CARS.equals(action)) {
                syncCars(intent);
            } else if (ACTION_SYNC_CAR.equals(action)) {
                syncCar(intent);
            }
        }
    }

    private void syncUserPurchases(Intent intent) {
        SalesServiceInterface salesServiceInterface = (SalesServiceInterface) APIHandler.getInstance().getApiService(APIHandler.SALES_SERVICE);

        CheckUserBody checkUserBody = new CheckUserBody();
        checkUserBody.token = userSharedPreferences.getString("token", "");
        checkUserBody.uuid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Call<List<Offer>> call = salesServiceInterface.getUserPurchases(checkUserBody);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_SYNC_USER_PURCHASES);

        call.enqueue(new Callback<List<Offer>>() {
            @Override
            public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    List<Offer> userPurchases = response.body();

                    String userPurchasesJson = new Gson().toJson(userPurchases);

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("userPurchases", userPurchasesJson));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Offer>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();

                t.printStackTrace();
            }
        });
    }

    private void syncUserRents(Intent intent) {
        RentServiceInterface rentService = (RentServiceInterface) APIHandler.getInstance().getApiService(APIHandler.RENT_SERVICE);

        CheckUserBody checkUserBody = new CheckUserBody();
        checkUserBody.token = userSharedPreferences.getString("token", "");
        checkUserBody.uuid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Call<List<Offer>> call = rentService.getRents(checkUserBody);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_SYNC_USER_RENT);

        call.enqueue(new Callback<List<Offer>>() {
            @Override
            public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    List<Offer> userRents = response.body();

                    String userRentsJson = new Gson().toJson(userRents);

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("userRents", userRentsJson));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Offer>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();

                t.printStackTrace();
            }
        });
    }

    private void syncCar(Intent intent) {
        int id = intent.getIntExtra("id", 1);
        int idModel = intent.getIntExtra("idModel", 1);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_SYNC_CAR);

        if(CacheManager.carsMap != null && CacheManager.carsMap.containsKey(idModel) && CacheManager.carsMap.get(idModel).size() != 0) {
            List<Car> cars = CacheManager.carsMap.get(idModel);
            int i = 0;
            while (i < cars.size() && cars.get(i).getId() != id) {
                i++;
            }

            String carJson = new Gson().toJson(cars.get(i));

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("car", carJson));
        } else {
            ExplorerServiceInterface explorerServiceInterface = (ExplorerServiceInterface) APIHandler.getInstance().getApiService(APIHandler.EXPLORER_SERVICE);

            Call<Car> call = explorerServiceInterface.getCar(id);

            call.enqueue(new Callback<Car>() {
                @Override
                public void onResponse(Call<Car> call, Response<Car> response) {
                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                        Car car = response.body();

                        Toast.makeText(getApplicationContext(), R.string.update_success_message, Toast.LENGTH_SHORT).show();

                        String carJson = new Gson().toJson(car);

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("car", carJson));
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Car> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();

                    t.printStackTrace();
                }
            });
        }
    }

    private void syncCars(Intent intent) {
        int idModel = intent.getIntExtra("idModel", 1);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_SYNC_CARS);

        if(CacheManager.carsMap != null && CacheManager.carsMap.containsKey(idModel) && CacheManager.carsMap.get(idModel).size() != 0) {
            List<Car> cars = CacheManager.carsMap.get(idModel);

            String carsJson = new Gson().toJson(cars);

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("cars", carsJson));
        } else {
            ExplorerServiceInterface explorerServiceInterface = (ExplorerServiceInterface) APIHandler.getInstance().getApiService(APIHandler.EXPLORER_SERVICE);

            Call<List<Car>> call = explorerServiceInterface.getCars(idModel);

            call.enqueue(new Callback<List<Car>>() {
                @Override
                public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                        List<Car> cars = response.body();

                        Toast.makeText(getApplicationContext(), R.string.update_success_message, Toast.LENGTH_SHORT).show();

                        CacheManager.carsMap.put(idModel, cars);

                        try {
                            SharedPreferences.Editor editor = cacheSharedPreferences.edit();
                            editor.putString("carsMap", ObjectSerializer.serialize(CacheManager.carsMap));
                            editor.apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String carsJson = new Gson().toJson(cars);

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("cars", carsJson));
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Car>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();

                    t.printStackTrace();
                }
            });
        }
    }

    private void syncModels(Intent intent) {
        int idBrand = intent.getIntExtra("idBrand", 1);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_SYNC_MODELS);

        if (CacheManager.carModelsMap != null && CacheManager.carModelsMap.containsKey(idBrand) && CacheManager.carModelsMap.get(idBrand).size() != 0) {
            List<CarModel> carModels = CacheManager.carModelsMap.get(idBrand);

            String modelsJson = new Gson().toJson(carModels);

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("carModels", modelsJson));
        } else {
            ExplorerServiceInterface explorerServiceInterface = (ExplorerServiceInterface) APIHandler.getInstance().getApiService(APIHandler.EXPLORER_SERVICE);


            Call<List<CarModel>> call = explorerServiceInterface.getModels(idBrand);

            call.enqueue(new Callback<List<CarModel>>() {
                @Override
                public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                        List<CarModel> carModels = response.body();

                        CacheManager.carModelsMap.put(idBrand, carModels);

                        try {
                            SharedPreferences.Editor editor = cacheSharedPreferences.edit();
                            editor.putString("modelsMap", ObjectSerializer.serialize(CacheManager.carModelsMap));
                            editor.apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplicationContext(), R.string.update_success_message, Toast.LENGTH_SHORT).show();


                        String modelsJson = new Gson().toJson(carModels);

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("carModels", modelsJson));
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<CarModel>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();

                    t.printStackTrace();
                }
            });
        }
    }

    private void syncBrand(Intent intent) {
        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_SYNC_BRAND);

        if(CacheManager.carBrands != null && CacheManager.carBrands.size() != 0) {
            String brandsJson = new Gson().toJson(CacheManager.carBrands);

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("carBrands", brandsJson));
        } else {
            ExplorerServiceInterface explorerServiceInterface = (ExplorerServiceInterface) APIHandler.getInstance().getApiService(APIHandler.EXPLORER_SERVICE);

            Call<List<CarBrand>> call = explorerServiceInterface.getBrands();

            call.enqueue(new Callback<List<CarBrand>>() {
                @Override
                public void onResponse(Call<List<CarBrand>> call, Response<List<CarBrand>> response) {
                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                        List<CarBrand> carBrands = response.body();

                        CacheManager.carBrands = carBrands;


                        try {
                            SharedPreferences.Editor editor = cacheSharedPreferences.edit();
                            editor.putString("brandList", ObjectSerializer.serialize((Serializable) carBrands));
                            editor.apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplicationContext(), R.string.update_success_message, Toast.LENGTH_SHORT).show();


                        String brandsJson = new Gson().toJson(carBrands);

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("carBrands", brandsJson));
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<CarBrand>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();

                    t.printStackTrace();
                }
            });
        }
    }

    private void syncOnSale(Intent intent) {
        SalesServiceInterface salesServiceInterface = (SalesServiceInterface) APIHandler.getInstance().getApiService(APIHandler.SALES_SERVICE);

        int idPage = intent.getIntExtra("idPage", 1);
        Call<List<Offer>> call = salesServiceInterface.getSales(idPage);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_SYNC_SALES);

        call.enqueue(new Callback<List<Offer>>() {
            @Override
            public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    List<Offer> updatedSales = response.body();
                    RentSaleDatabaseHelper rentSaleDatabaseHelper = new RentSaleDatabaseHelper(getApplicationContext());

                    rentSaleDatabaseHelper.deleteTableSale();

                    for (int i = 0; i < updatedSales.size(); i++) {
                        rentSaleDatabaseHelper.addSale(updatedSales.get(i));
                    }

                    Toast.makeText(getApplicationContext(), R.string.update_success_message, Toast.LENGTH_SHORT).show();


                    String updatedSalesJson = new Gson().toJson(updatedSales);

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("updatedSales", updatedSalesJson));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Offer>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();

                t.printStackTrace();
            }
        });
    }

    private void syncRents(Intent intent) {
        RentServiceInterface rentService = (RentServiceInterface) APIHandler.getInstance().getApiService(APIHandler.RENT_SERVICE);

        int idPage = intent.getIntExtra("idPage", 1);
        Call<List<Offer>> call = rentService.getRents(idPage);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_SYNC_RENT);

        call.enqueue(new Callback<List<Offer>>() {
            @Override
            public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    List<Offer> updatedRents = response.body();
                    RentSaleDatabaseHelper rentSaleDatabaseHelper = new RentSaleDatabaseHelper(getApplicationContext());

                    rentSaleDatabaseHelper.deleteTableRent();

                    for (int i = 0; i < updatedRents.size(); i++) {
                        rentSaleDatabaseHelper.addRent(updatedRents.get(i));
                    }

                    Toast.makeText(getApplicationContext(), R.string.update_success_message, Toast.LENGTH_SHORT).show();


                    String updatedRentsJson = new Gson().toJson(updatedRents);

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("updatedRents", updatedRentsJson));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Offer>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();

                t.printStackTrace();
            }
        });
    }
}
