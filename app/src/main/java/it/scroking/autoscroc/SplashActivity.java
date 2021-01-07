package it.scroking.autoscroc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.Buffer;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import it.scroking.autoscroc.models.Car;
import it.scroking.autoscroc.models.CarBrand;
import it.scroking.autoscroc.models.CarModel;
import it.scroking.autoscroc.models.Offer;
import it.scroking.autoscroc.services.SyncService;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MainActivity.class));

        this.sharedPreferences = getSharedPreferences("Cache", Context.MODE_PRIVATE);

        //asyncServicesLoad();

        loadCache();

        finish();
    }

    private void loadCache() {
        try {
            String brandListSerialized = this.sharedPreferences.getString("brandList", "");
            String modelsMapSerialized = this.sharedPreferences.getString("modelsMap", "");
            String carsMapSerialized = this.sharedPreferences.getString("carsMap", "");

            CacheManager.carBrands = (List<CarBrand>)ObjectSerializer.deserialize(brandListSerialized);
            CacheManager.carModelsMap = (HashMap<Integer, List<CarModel>>)ObjectSerializer.deserialize(modelsMapSerialized);
            CacheManager.carsMap = (HashMap<Integer, List<Car>>) ObjectSerializer.deserialize(carsMapSerialized);

            if(CacheManager.carModelsMap == null) {
                CacheManager.carModelsMap = new HashMap<Integer, List<CarModel>>();
            }
            if(CacheManager.carsMap == null) {
                CacheManager.carsMap = new HashMap<Integer, List<Car>>();
            }
        } catch (IOException e) {
            Log.d("TAG", "Err");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void asyncServicesLoad() {
        startService(new Intent(this, SyncService.class).setAction(SyncService.ACTION_SYNC_RENT));
        startService(new Intent(this, SyncService.class).setAction(SyncService.ACTION_SYNC_SALES));
    }

}

