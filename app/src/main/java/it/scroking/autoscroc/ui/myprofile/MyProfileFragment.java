package it.scroking.autoscroc.ui.myprofile;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.scroking.autoscroc.R;

public class MyProfileFragment extends Fragment {

    private ImageView userImageView;

    private TextView usernameTextView;
    private TextView emailTextView;

    private TextView nameTextView;
    private TextView surnameTextView;

    private TextView addressTextView;
    private TextView cityTextView;
    private TextView regionTextView;
    private TextView countryTextView;

    private TextView telTextView;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_profile, container, false);

        this.userImageView = root.findViewById(R.id.user_image);

        this.usernameTextView = root.findViewById(R.id.username);
        this.emailTextView = root.findViewById(R.id.email);

        this.nameTextView = root.findViewById(R.id.name);
        this.surnameTextView = root.findViewById(R.id.surname);

        this.addressTextView = root.findViewById(R.id.address);
        this.cityTextView = root.findViewById(R.id.city);
        this.regionTextView = root.findViewById(R.id.region);
        this.countryTextView = root.findViewById(R.id.country);

        this.telTextView = root.findViewById(R.id.tel);

        this.sharedPreferences = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);

        loadUserData();

        return root;
    }

    private void loadUserData() {
        //set image
        byte[] decodedString = Base64.decode(sharedPreferences.getString("img", ""), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        this.userImageView.setImageBitmap(decodedByte);

        //set username and email
        this.usernameTextView.setText(sharedPreferences.getString("username", ""));
        this.emailTextView.setText(sharedPreferences.getString("email", ""));

        //set name and surname
        this.nameTextView.setText(sharedPreferences.getString("name", ""));
        this.surnameTextView.setText(sharedPreferences.getString("surname", ""));

        //set address
        this.addressTextView.setText(sharedPreferences.getString("address", ""));
        this.cityTextView.setText(sharedPreferences.getString("city", ""));
        this.regionTextView.setText(sharedPreferences.getString("region", ""));
        this.countryTextView.setText(sharedPreferences.getString("country", ""));

        //set tel
        this.telTextView.setText(sharedPreferences.getString("tel", ""));
    }

}
