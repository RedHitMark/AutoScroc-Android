package it.scroking.autoscroc.ui.offer_detail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.Gson;

import it.scroking.autoscroc.ui.dialogs.DialogMessageFragment;
import it.scroking.autoscroc.services.PurchaseService;
import it.scroking.autoscroc.R;
import it.scroking.autoscroc.models.Offer;

public class OfferDetailFragment extends Fragment implements View.OnClickListener, DialogMessageFragment.DialogListener {

    private DialogMessageFragment messageFragment;

    private TextView priceCar;
    private TextView kmCar;
    private TextView licencePlateCar;
    private TextView nameCar;

    private ImageView imageCar;

    private Button showDetails;
    private Button goToRentBuy;

    private Offer offer;
    private TextView registrationAlert;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_offer, container, false);


        imageCar = root.findViewById(R.id.imageCar);
        nameCar = root.findViewById(R.id.carNameResp);
        licencePlateCar = root.findViewById(R.id.licencePlateRes);
        kmCar = root.findViewById(R.id.kmCarResp);
        priceCar = root.findViewById(R.id.priceCarResp);
        showDetails = root.findViewById(R.id.showDetBtn);
        goToRentBuy = root.findViewById(R.id.goToBTN);

        registrationAlert = root.findViewById(R.id.you_must_register);


        String RentJson = getArguments().getString("RentJson");

        this.offer = new Gson().fromJson(RentJson, Offer.class);

        byte[] decodedString = Base64.decode(offer.getImg(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageCar.setImageBitmap(decodedByte);

        nameCar.setText(offer.getName());
        licencePlateCar.setText(offer.getLicensePlate());
        kmCar.setText(offer.getKm() + "");
        priceCar.setText(offer.getPrice() + "");
        showDetails.setOnClickListener(this);
        goToRentBuy.setOnClickListener(this);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");

        if(role.equals("user") || role.equals("admin")) {
            this.registrationAlert.setVisibility(View.GONE);
            goToRentBuy.setEnabled(true);
        } else {
            this.registrationAlert.setVisibility(View.VISIBLE);
            goToRentBuy.setEnabled(false);
        }



        return root;
    }

    public void gotoDet() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        Bundle bundle = new Bundle();
        bundle.putInt("id", this.offer.getId());
        bundle.putInt("idModel", this.offer.getIdModel());

        navController.navigate(R.id.nav_car_detail, bundle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goToBTN:
                messageFragment = new DialogMessageFragment();
                messageFragment.setTargetFragment(OfferDetailFragment.this, 1);
                messageFragment.show(getParentFragmentManager(), "Buy");
                break;
            case R.id.showDetBtn:
                gotoDet();
                break;
        }
    }

    @Override
    public void onDialogPositiveClick() {
        Intent intent = new Intent(getContext(), PurchaseService.class);
        intent.setAction(PurchaseService.ACTION_BUY_RENT);
        intent.putExtra("licensePlate", offer.getLicensePlate());
        getActivity().startService(intent);

        this.messageFragment.dismiss();
    }

    @Override
    public void onDialogNegativeClick() {
        this.messageFragment.dismiss();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null && intent.getAction().equals(PurchaseService.ACTION_BUY_RENT)) {
                boolean buyRentSuccess = intent.getBooleanExtra("buyRentResponse", false);
                if (buyRentSuccess) {
                    //NavController navController =
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(this.receiver, new IntentFilter(PurchaseService.ACTION_BUY_RENT));
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this.receiver);
    }
}
