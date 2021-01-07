package it.scroking.autoscroc.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.Gson;

import it.scroking.autoscroc.R;
import it.scroking.autoscroc.RentSaleDatabaseHelper;
import it.scroking.autoscroc.models.Offer;
import it.scroking.autoscroc.services.SyncService;


public class HomeFragment extends Fragment  implements  View.OnClickListener {

    private Offer rentCar;
    private Offer saleCar;


    private RentSaleDatabaseHelper rentSaleDatabaseHelper;

    private CardView rentCardView;
    private ImageView rentImage;
    private TextView rentName;
    private TextView rentKm;
    private TextView rentPrice;

    private CardView saleCardView;
    private ImageView saleImg;
    private TextView saleName;
    private TextView saleKm;
    private TextView salePrice;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);


        this.rentCardView = root.findViewById(R.id.rentCardView);
        this.rentImage = root.findViewById(R.id.rentImage);
        this.rentName = root.findViewById(R.id.rentName);
        this.rentKm = root.findViewById(R.id.rentKm);
        this.rentPrice = root.findViewById(R.id.rentPrice);

        this.saleCardView = root.findViewById(R.id.saleCardView);
        this.saleImg = root.findViewById(R.id.saleImage);
        this.saleName = root.findViewById(R.id.saleName);
        this.saleKm = root.findViewById(R.id.saleKm);
        this.salePrice = root.findViewById(R.id.salePrice);


        this.rentCar = new Offer();
        this.rentCar.setImg("");
        this.saleCar = new Offer();

        this.rentSaleDatabaseHelper = new RentSaleDatabaseHelper(getContext());

        this.loadBestRentFromLocalDb();
        this.loadBestsaleFromLocalDb();

        this.getValuePositionRent();
        this.getValuePositionSale();


        this.rentCardView.setOnClickListener(this);
        this.saleCardView.setOnClickListener(this);


        asyncServicesLoad();

        return root;
    }

    private void asyncServicesLoad() {
        getActivity().startService(new Intent(getContext(), SyncService.class).setAction(SyncService.ACTION_SYNC_RENT));
        getActivity().startService(new Intent(getContext(), SyncService.class).setAction(SyncService.ACTION_SYNC_SALES));
    }



    private void getValuePositionRent(){
        if(this.rentCar.getImg() ==  null){
            this.rentCar.setImg("");
        }
        byte[] decodedString = Base64.decode(this.rentCar.getImg(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        this.rentImage.setImageBitmap(decodedByte);
        this.rentName.setText(rentCar.getName());
        this.rentKm.setText(rentCar.getKm()+"Km");
        this.rentPrice.setText(rentCar.getPrice()+"€");
    }

    private void getValuePositionSale(){
        if(this.saleCar.getImg() ==  null){
            this.saleCar.setImg("");
        }

        byte[] decodedString = Base64.decode(this.saleCar.getImg(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        this.saleImg.setImageBitmap(decodedByte);
        this.saleName.setText(saleCar.getName());
        this.saleKm.setText(saleCar.getKm()+"Km");
        this.salePrice.setText(String.valueOf(saleCar.getPrice()));
    }

    private void loadBestRentFromLocalDb(){
        this.rentCar = rentSaleDatabaseHelper.getBestRent();
    }
    private void loadBestsaleFromLocalDb(){
        this.saleCar = rentSaleDatabaseHelper.getBestSale();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rentCardView:

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

                String RentJson = new Gson().toJson(rentCar);

                Bundle bundle = new Bundle();
                bundle.putString("RentJson", RentJson);
                navController.navigate(R.id.nav_rent, bundle);

                break;
            case R.id.saleCardView:

                NavController navControllerSecond = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

                String RentJsonSecond = new Gson().toJson(saleCar);

                Bundle bundleSecond = new Bundle();
                bundleSecond.putString("RentJson", RentJsonSecond);
                navControllerSecond.navigate(R.id.nav_rent, bundleSecond);

                break;
        }

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SyncService.ACTION_SYNC_SALES)) {
                loadBestsaleFromLocalDb();
                getValuePositionSale();
            }
            if (intent.getAction().equals(SyncService.ACTION_SYNC_RENT)) {
                loadBestRentFromLocalDb();
                getValuePositionRent();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SyncService.ACTION_SYNC_SALES));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SyncService.ACTION_SYNC_RENT));
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }
}