package it.scroking.autoscroc.ui.explorer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import it.scroking.autoscroc.R;
import it.scroking.autoscroc.services.SyncService;
import it.scroking.autoscroc.models.Car;

public class CarDetailFragment extends Fragment {

    private Car car;
    private int id;
    private int idModel;

    private ImageView img;

    private TextView name;
    private TextView carType;

    private TextView power;
    private TextView trasmission;

    private TextView engine;
    private TextView cc;
    private TextView cil;

    private TextView topSpeed;
    private TextView acc;
    private TextView weight;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_car_detail, container, false);

        this.img = root.findViewById(R.id.imageCar);

        this.name = root.findViewById(R.id.name);
        this.carType = root.findViewById(R.id.car_type);

        this.power = root.findViewById(R.id.power);

        this.trasmission = root.findViewById(R.id.trasmission);

        this.engine = root.findViewById(R.id.engine);
        this.cc = root.findViewById(R.id.cc);
        this.cil = root.findViewById(R.id.cil);

        this.topSpeed = root.findViewById(R.id.top_speed);
        this.acc = root.findViewById(R.id.acc);
        this.weight = root.findViewById(R.id.weight);

        this.car = new Car();

        this.id = getArguments() != null ? getArguments().getInt("id") : 1;
        this.idModel = getArguments() != null ? getArguments().getInt("idModel") : 1;


        Intent intent = new Intent(getContext(), SyncService.class);
        intent.setAction(SyncService.ACTION_SYNC_CAR);
        intent.putExtra("id", this.id);
        intent.putExtra("idModel", this.idModel);

        getActivity().startService(intent);

        return root;
    }

    private void updateCar() {
        byte[] decodedString = Base64.decode(this.car.getImg(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        this.img.setImageBitmap(decodedByte);

        this.name.setText(this.car.getName());
        this.carType.setText(this.car.getCarType() + " - " + this.car.getDoors() + " doors");

        this.power.setText(this.car.getHp() + " hp - " + this.car.getTorque() + " Nm");

        this.trasmission.setText(this.car.getTrasmission());

        this.engine.setText(this.car.getEngineType());
        this.cc.setText(this.car.getCc() + "cc");
        this.cil.setText(this.car.getNumCylinders() + " cilindri " + this.car.getCylindersType());

        this.topSpeed.setText(this.car.getTopSpeed() + " km/h");
        this.acc.setText(this.car.getAcc() + " s");
        this.weight.setText(this.car.getWeight() + " kg");
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(SyncService.ACTION_SYNC_CAR)) {
                String carJson = intent.getStringExtra("car");

                Log.d("Car", carJson);

                car = new Gson().fromJson(carJson, Car.class);

                updateCar();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SyncService.ACTION_SYNC_CAR));
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }
}
