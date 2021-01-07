package it.scroking.autoscroc.ui.your_scrounge.your_rents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.scroking.autoscroc.R;
import it.scroking.autoscroc.services.SyncService;
import it.scroking.autoscroc.ui.adapters.OffertsAdapter;
import it.scroking.autoscroc.models.Offer;


public class YourRentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listViewCars;
    private SwipeRefreshLayout refresh;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SyncService.ACTION_SYNC_USER_RENT)) {
                String updatedRentJson = intent.getStringExtra("userRents");

                ArrayList<Offer> updateRent = new ArrayList<>(Arrays.asList(new Gson().fromJson(updatedRentJson, Offer[].class)));

                updateListView(updateRent);

                refresh.setRefreshing(false);
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_your_rents, container, false);

        this.listViewCars = root.findViewById(R.id.listViewCars);

        Log.d("local f1111", "call");

        this.refresh = root.findViewById(R.id.refreshLayout);
        this.refresh.setOnRefreshListener(this);

        this.loadRentsFromServer();

        return root;
    }

    private void updateListView(List<Offer> rentCarsList) {
        OffertsAdapter arrayAdapter = new OffertsAdapter(getContext(), rentCarsList);

        Log.d("local f1111", rentCarsList.toString());

        this.listViewCars.setAdapter(arrayAdapter);
    }

    private void loadRentsFromServer() {
        Intent intent = new Intent(getActivity(), SyncService.class);
        intent.setAction(SyncService.ACTION_SYNC_USER_RENT);
        getActivity().startService(intent);
    }

    @Override
    public void onRefresh() {
        loadRentsFromServer();
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SyncService.ACTION_SYNC_USER_RENT));
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }
}