package it.scroking.autoscroc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import it.scroking.autoscroc.services.UserService;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initUI();

        this.sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);

        this.loadUser();

        this.initFabButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        this.mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_rents, R.id.nav_on_sale, R.id.nav_your_scrounge, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void initFabButton() {
        String userRole = this.sharedPreferences.getString("role", "client");

        this.fab = findViewById(R.id.fab);

        if (userRole.equals("admin")) {
            this.fab.setImageResource(R.drawable.car_icon_white);
            this.fab.setOnClickListener(view -> {
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

                navController.navigate(R.id.nav_new_offer);

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.LEFT, true);
            });
        } else {
            this.fab.setImageResource(R.drawable.ic_gift_solid_white);
            this.fab.setOnClickListener(view -> Snackbar.make(view, getString(R.string.esterEgg), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show());
        }
    }

    //Manage all user functions
    public void loadUser() {
        String username = sharedPreferences.getString("username", "");
        String name = sharedPreferences.getString("name", "");
        String surname = sharedPreferences.getString("surname", "");
        String img = sharedPreferences.getString("img", "");

        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();

        if (!username.equals("") && !name.equals("") && !surname.equals("") && !img.equals("")) {

            menu.findItem(R.id.nav_your_scrounge).setVisible(true);

            TextView navUsernameTextView = header.findViewById(R.id.username);
            TextView navNameSurnameTextView = header.findViewById(R.id.name_and_surname);


            header.findViewById(R.id.layout_not_login).setVisibility(View.GONE);
            header.findViewById(R.id.layout_login).setVisibility(View.VISIBLE);


            navUsernameTextView.setText(username);
            navNameSurnameTextView.setText(name + " " + surname);

            byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ((ImageView) header.findViewById(R.id.user_image)).setImageBitmap(decodedByte);

            /*//start unbounded service to revalidate user every 5 minute
            Intent intent = new Intent(this, MyService.class);
            //startService(intent);*/
        } else {

            menu.findItem(R.id.nav_your_scrounge).setVisible(false);

            header.findViewById(R.id.layout_not_login).setVisibility(View.VISIBLE);
            header.findViewById(R.id.layout_login).setVisibility(View.GONE);

            Toast.makeText(this, "Login scaduta, accedi di nuovo", Toast.LENGTH_LONG).show();
        }

        initFabButton();
    }

    public void toHomeFragment(View view) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navController.navigate(R.id.nav_home);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(Gravity.LEFT, true);
    }

    //
    public void toLoginFragment(View view) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navController.navigate(R.id.nav_login);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(Gravity.LEFT, true);
    }

    public void toRegisterFragment(View view) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navController.navigate(R.id.nav_register);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(Gravity.LEFT, true);
    }

    public void toMyProfileFragment(View view) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navController.navigate(R.id.nav_my_profile);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(Gravity.LEFT, true);
    }

    public void doLogout(View view) {
        Intent intent = new Intent(this, UserService.class);
        intent.setAction(UserService.ACTION_LOGOUT);

        startService(intent);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(UserService.ACTION_LOGOUT)) {
                boolean logoutSuccess = intent.getBooleanExtra("logoutSuccess", false);

                if (logoutSuccess) {
                    loadUser();
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, new IntentFilter(UserService.ACTION_LOGOUT));
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
    }

}
