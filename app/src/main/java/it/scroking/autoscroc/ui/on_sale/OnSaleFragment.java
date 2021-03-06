package it.scroking.autoscroc.ui.on_sale;

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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.scroking.autoscroc.RentSaleDatabaseHelper;
import it.scroking.autoscroc.R;
import it.scroking.autoscroc.services.SyncService;
import it.scroking.autoscroc.ui.adapters.EndlessScrollListener;
import it.scroking.autoscroc.ui.adapters.OffertsAdapter;
import it.scroking.autoscroc.models.Offer;
import it.scroking.autoscroc.ui.dialogs.FilterDialog;

public class OnSaleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, FilterDialog.FilterListener {


    private ListView listViewCars;
    private SwipeRefreshLayout refresh;

    private RentSaleDatabaseHelper rentSaleDatabaseHelper;
    private int idPage;

    private List<Offer> sales;
    private boolean isFilterEnabled;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_on_sale, container, false);

        this.listViewCars = root.findViewById(R.id.listViewCars);

        this.refresh = root.findViewById(R.id.refreshLayout);
        this.refresh.setOnRefreshListener(this);

        this.idPage = 1;

        this.isFilterEnabled = false;

        this.rentSaleDatabaseHelper = new RentSaleDatabaseHelper(getContext());

        this.loadSalesFromLocalDb();
        this.loadSalesFromServer(); //async call

        return root;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                FilterDialog filterDialog = new FilterDialog();
                filterDialog.setTargetFragment(OnSaleFragment.this, 1);
                filterDialog.show(getParentFragmentManager(), "Filtra");
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    private void updateListView() {
        OffertsAdapter arrayAdapter = new OffertsAdapter(getContext(), this.sales);

        this.listViewCars.setAdapter(arrayAdapter);

        this.listViewCars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

                String RentJson = new Gson().toJson(sales.get(position));

                Bundle bundle = new Bundle();
                bundle.putString("RentJson", RentJson);
                navController.navigate(R.id.nav_rent, bundle);
            }
        });

        this.listViewCars.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if(!isFilterEnabled) {
                    Toast.makeText(getContext(), "Carico", Toast.LENGTH_SHORT).show();
                    loadSalesFromServer();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void loadSalesFromLocalDb() {
        this.sales = rentSaleDatabaseHelper.getAllSales();

        this.updateListView();
    }

    private void loadSalesFromServer() {
        Intent intent = new Intent(getActivity(), SyncService.class);
        intent.putExtra("idPage", this.idPage);
        intent.setAction(SyncService.ACTION_SYNC_SALES);
        getActivity().startService(intent);
    }

    @Override
    public void onRefresh() {
        idPage = 1;
        loadSalesFromServer();
    }




    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SyncService.ACTION_SYNC_SALES)) {
                String updatedSaleJson = intent.getStringExtra("updatedSales");

                List<Offer> newOffers = new ArrayList<>(Arrays.asList(new Gson().fromJson(updatedSaleJson, Offer[].class)));

                if (idPage == 1) {
                    sales = newOffers;
                } else {
                    sales.addAll(newOffers);
                }
                idPage++;
                updateListView();

                refresh.setRefreshing(false);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SyncService.ACTION_SYNC_SALES));
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public void onFilterClick(int km, int matriculationYear, double price, int hp, String engineType, int doors, String carType, String cylindersType, int numCylinders) {
        if(km>=0 || matriculationYear>=0|| price>=0 || hp>=0 || doors>=0 || !carType.equals("") || !engineType.equals("") || numCylinders >=0 ) {
            isFilterEnabled = true;

            Log.d("TAG", "aaa");

            this.sales = rentSaleDatabaseHelper.getRent(km, matriculationYear, price, hp, engineType, doors, carType, cylindersType, numCylinders);
            updateListView();
        } else {
            Log.d("TAG", "bbb");
            isFilterEnabled = false;
        }
    }
}