package it.scroking.autoscroc.ui.explorer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.scroking.autoscroc.R;
import it.scroking.autoscroc.services.SyncService;
import it.scroking.autoscroc.models.Car;
import it.scroking.autoscroc.ui.adapters.CarsAdapter;


public class CarsFragment extends Fragment implements CarsAdapter.OnCarListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Car> carsList;

    private int idModel;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(SyncService.ACTION_SYNC_CARS)) {
                String brandJson = intent.getStringExtra("cars");

                carsList = new ArrayList<>(Arrays.asList(new Gson().fromJson(brandJson, Car[].class)));

                updateCarList();
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cars, container, false);

        this.recyclerView = root.findViewById(R.id.recycler_view);
        this.recyclerView.setHasFixedSize(true);

        this.layoutManager = new LinearLayoutManager(getContext());
        this.recyclerView.setLayoutManager(this.layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        //empty list
        this.carsList = new ArrayList<>();


        //this.idModel = getArguments() != null ? getArguments().getInt("idModel") : 1;
        this.idModel = getArguments().getInt("idModel");

        Intent intent = new Intent(getContext(), SyncService.class);
        intent.setAction(SyncService.ACTION_SYNC_CARS);
        intent.putExtra("idModel", this.idModel);
        getActivity().startService(intent);

        return root;
    }

    private void updateCarList() {
        this.mAdapter = new CarsAdapter(this.carsList, this);
        this.recyclerView.setAdapter(this.mAdapter);
    }

    @Override
    public void onCarClick(int position) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        Bundle bundle = new Bundle();
        bundle.putInt("id", this.carsList.get(position).getId());
        bundle.putInt("idModel", this.carsList.get(position).getIdModel());

        navController.navigate(R.id.nav_car_detail, bundle);
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SyncService.ACTION_SYNC_CARS));
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }
}
