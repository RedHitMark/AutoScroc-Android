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
import it.scroking.autoscroc.models.CarBrand;
import it.scroking.autoscroc.ui.adapters.BrandsAdapter;

public class BrandsFragment extends Fragment implements BrandsAdapter.OnBrandListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<CarBrand> carBrandsList;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(SyncService.ACTION_SYNC_BRAND)) {
                String brandJson = intent.getStringExtra("carBrands");

                ArrayList<CarBrand> carBrands = new ArrayList<>(Arrays.asList(new Gson().fromJson(brandJson, CarBrand[].class)));

                carBrandsList = carBrands;

                updateBrandList();
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_explorer, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        getActivity().startService(new Intent(getContext(), SyncService.class).setAction(SyncService.ACTION_SYNC_BRAND));

        return root;
    }

    private void updateBrandList() {
        // specify an adapter (see also next example)
        mAdapter = new BrandsAdapter(this.carBrandsList, this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBrandClick(int position) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        Bundle bundle = new Bundle();
        bundle.putInt("idBrand", this.carBrandsList.get(position).getId());
        navController.navigate(R.id.nav_models, bundle);
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SyncService.ACTION_SYNC_BRAND));
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }
}