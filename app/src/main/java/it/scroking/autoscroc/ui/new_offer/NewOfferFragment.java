package it.scroking.autoscroc.ui.new_offer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.scroking.autoscroc.RentSaleDatabaseHelper;

import it.scroking.autoscroc.R;

import it.scroking.autoscroc.models.Car;
import it.scroking.autoscroc.models.CarBrand;
import it.scroking.autoscroc.models.CarModel;

import it.scroking.autoscroc.models.Offer;
import it.scroking.autoscroc.services.CreationService;
import it.scroking.autoscroc.services.SyncService;
import it.scroking.autoscroc.ui.dialogs.FilterDialog;
import it.scroking.autoscroc.ui.on_sale.OnSaleFragment;

public class NewOfferFragment extends Fragment  implements View.OnClickListener {

    private RadioGroup  radioGroup;
    private RadioButton addRent;
    private RadioButton addSale;

    private Spinner brandSpinner;
    private Spinner modelSpinner;
    private Spinner engineSpinner;

    private EditText carLicencePlate;
    private NumberPicker yearCarPicer;
    private EditText carKm;
    private EditText carPrice;

    private List<CarBrand> carBrandsList;
    private List<CarModel> carModelList;
    private List<Car> carList;

    private Button addButton;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_add_offer, container, false);

        this.radioGroup = root.findViewById(R.id.rentRadioGroup);
        this.addRent = root.findViewById(R.id.rentRadio);
        this.addSale = root.findViewById(R.id.salesRadio);

        this.brandSpinner = root.findViewById(R.id.brandSpinner);
        this.modelSpinner = root.findViewById(R.id.modelSpinner);
        this.engineSpinner = root.findViewById(R.id.mengineSpinner);
        this.carLicencePlate = root.findViewById(R.id.licencePlateInput);
        this.yearCarPicer = root.findViewById(R.id.yearCarPicker);
        this.carKm = root.findViewById(R.id.km);
        this.carPrice = root.findViewById(R.id.priceInput);


        yearCarPicerInit();

        this.addButton = root.findViewById(R.id.addButton);
        this.addButton.setOnClickListener(this);
       // updateBrandSpinner();



        getActivity().startService(new Intent(getContext(), SyncService.class).setAction(SyncService.ACTION_SYNC_BRAND));



        return root;
    }

    private void yearCarPicerInit() {

        yearCarPicer.setMinValue(1900);
        yearCarPicer.setMaxValue(2020);

        NumberPicker.OnValueChangeListener onValueChangeListener =
                (numberPicker, i, i1) -> {

                };

        yearCarPicer.setOnValueChangedListener(onValueChangeListener);
    }


    private void updateModelsList() {
        this.modelSpinner.setAdapter(new ArrayAdapter<CarModel>(getContext(),android.R.layout.simple_list_item_1,carModelList));

        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), SyncService.class);
                intent.setAction(SyncService.ACTION_SYNC_CARS);
                intent.putExtra("idModel", carModelList.get(position).getId());

                getActivity().startService(intent);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateBrandSpinner() {
        this.brandSpinner.setAdapter(new ArrayAdapter<CarBrand>(getContext(), android.R.layout.simple_list_item_1,carBrandsList));

        brandSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), SyncService.class);
                intent.setAction(SyncService.ACTION_SYNC_MODELS);
                intent.putExtra("idBrand", carBrandsList.get(position).getId());

                getActivity().startService(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateCarDetailList() {
        engineSpinner.setAdapter(new ArrayAdapter<Car>(getContext(),android.R.layout.simple_list_item_1,carList));
    }





    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(SyncService.ACTION_SYNC_BRAND)) {
                String brandJson = intent.getStringExtra("carBrands");

                ArrayList<CarBrand> carBrands = new ArrayList<>(Arrays.asList(new Gson().fromJson(brandJson, CarBrand[].class)));

                carBrandsList = carBrands;

                updateBrandSpinner();

            }
            if (intent != null && intent.getAction() != null && intent.getAction().equals(SyncService.ACTION_SYNC_MODELS)) {
                String modelsJson = intent.getStringExtra("carModels");

               ArrayList<CarModel> carModels = new ArrayList<>(Arrays.asList(new Gson().fromJson(modelsJson, CarModel[].class)));

               carModelList = carModels;

                updateModelsList();
            }

            if (intent != null && intent.getAction() != null && intent.getAction().equals(SyncService.ACTION_SYNC_CARS)) {
                String carssJson = intent.getStringExtra("cars");

                ArrayList<Car> carDetails = new ArrayList<>(Arrays.asList(new Gson().fromJson(carssJson, Car[].class)));

                carList = carDetails;

                Log.d("DIO",carList.toString());

                updateCarDetailList();
            }

            if (intent != null && intent.getAction() != null && intent.getAction().equals(CreationService.ACTION_CREATE_RENT)) {
                Toast.makeText(getContext(), "cfcc", Toast.LENGTH_LONG).show();
            }
            if (intent != null && intent.getAction() != null && intent.getAction().equals(CreationService.ACTION_CREATE_SALE)) {
                Toast.makeText(getContext(), "cfcc", Toast.LENGTH_LONG).show();
            }
        }
    };



    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SyncService.ACTION_SYNC_BRAND));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SyncService.ACTION_SYNC_MODELS));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SyncService.ACTION_SYNC_CARS));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(CreationService.ACTION_CREATE_RENT));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(CreationService.ACTION_CREATE_SALE));

    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButton:
                doCreation();
                break;

        }
    }

    private void doCreation() {
        try {
            Intent intent = new Intent(getContext(), CreationService.class);

            intent.putExtra("licensePlate", carLicencePlate.getText().toString());
            intent.putExtra("km", Integer.parseInt(carKm.getText().toString()));
            intent.putExtra("price",Double.parseDouble(carPrice.getText().toString()));
            intent.putExtra("matriculationYear",yearCarPicer.getValue());
            intent.putExtra("id", carList.get(engineSpinner.getSelectedItemPosition()).getId());

            Log.d("TAG", carList.get(engineSpinner.getSelectedItemPosition()).getId() + "");

            switch (this.radioGroup.getCheckedRadioButtonId()) {
                case R.id.rentRadio:
                    intent.setAction(CreationService.ACTION_CREATE_RENT);
                    break;
                case R.id.salesRadio:
                    intent.setAction(CreationService.ACTION_CREATE_SALE);
                    break;


            }
            getActivity().startService(intent);
        }catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Compila tutti i campi", Toast.LENGTH_LONG).show();
        }

    }






    private void doReset(){
        brandSpinner.setSelection(0);
        addRent.setChecked(false);
        addSale.setChecked(false);
        yearCarPicer.setValue(1900);
        carKm.setText(null);
        carLicencePlate.setText(null);
        carPrice.setText(null);
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_reset, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                doReset();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}





