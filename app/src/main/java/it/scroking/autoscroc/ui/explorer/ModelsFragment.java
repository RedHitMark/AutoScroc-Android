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
import it.scroking.autoscroc.models.CarModel;
import it.scroking.autoscroc.ui.adapters.ModelsAdapter;

public class ModelsFragment extends Fragment implements ModelsAdapter.OnModelListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private int idBrand;

    private List<CarModel> carModels;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null && intent.getAction().equals(SyncService.ACTION_SYNC_MODELS)) {
                String modelsJson = intent.getStringExtra("carModels");

                carModels = new ArrayList<>(Arrays.asList(new Gson().fromJson(modelsJson, CarModel[].class)));

                updateModelsList();
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_models, container, false);


        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        this.carModels = new ArrayList<>();


        this.idBrand = getArguments() != null ? getArguments().getInt("idBrand") : 1;


        Intent intent = new Intent(getContext(), SyncService.class);
        intent.setAction(SyncService.ACTION_SYNC_MODELS);
        intent.putExtra("idBrand", this.idBrand);

        getActivity().startService(intent);

        return root;
    }

    private void updateModelsList() {
        mAdapter = new ModelsAdapter(this.carModels, this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onModelClick(int position) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        Bundle bundle = new Bundle();
        bundle.putInt("idModel", this.carModels.get(position).getId());
        navController.navigate(R.id.nav_cars, bundle);
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(this.broadcastReceiver, new IntentFilter(SyncService.ACTION_SYNC_MODELS));
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this.broadcastReceiver);
    }
}
