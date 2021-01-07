package it.scroking.autoscroc.ui.register;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import it.scroking.autoscroc.MainActivity;
import it.scroking.autoscroc.R;
import it.scroking.autoscroc.services.UserService;
import it.scroking.autoscroc.models.User;


public class RegisterFragment extends Fragment implements View.OnClickListener {
    private static final int GALLERY_REQUEST_CODE = 1;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UserService.ACTION_REGISTER)) {
                boolean registerSuccess = intent.getBooleanExtra("registerResponse", false);

                if (registerSuccess) {
                    Toast.makeText(getContext(), R.string.registration_success, Toast.LENGTH_LONG);

                    ((MainActivity) getActivity()).toLoginFragment(null);
                } else {
                    Toast.makeText(getContext(), R.string.registration_not_success, Toast.LENGTH_LONG);
                }
            }
        }
    };
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button fileChoserButton;
    private ImageView userImagePreview;
    private String encodedImage;
    private EditText streetEditText;
    private EditText cityEditText;
    private EditText regionEditText;
    private EditText countryEditText;
    private EditText telEditText;
    private Button registerButton;
    private RadioGroup radioRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);

        this.usernameEditText = root.findViewById(R.id.username);
        this.emailEditText = root.findViewById(R.id.email);

        this.nameEditText = root.findViewById(R.id.name_edit_text);
        this.surnameEditText = root.findViewById(R.id.surname_edit_text);

        this.passwordEditText = root.findViewById(R.id.password_edit_text);
        this.confirmPasswordEditText = root.findViewById(R.id.confirm_password_edit_text);

        this.userImagePreview = root.findViewById(R.id.user_image_preview);

        this.fileChoserButton = root.findViewById(R.id.image_chooser_button);
        this.fileChoserButton.setOnClickListener(this);
        this.userImagePreview.setOnClickListener(this);

        this.streetEditText = root.findViewById(R.id.street);
        this.cityEditText = root.findViewById(R.id.city);
        this.regionEditText = root.findViewById(R.id.region);
        this.countryEditText = root.findViewById(R.id.country);

        this.telEditText = root.findViewById(R.id.tel);

        this.radioRole = root.findViewById(R.id.radioRole);
        this.radioRole.check(R.id.radioClient);


        this.registerButton = root.findViewById(R.id.register);
        this.registerButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == GALLERY_REQUEST_CODE) {
                try {
                    Uri selectedImageUri = data.getData();
                    InputStream imageStream = getContext().getContentResolver().openInputStream(selectedImageUri);

                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    selectedImage = Bitmap.createScaledBitmap(selectedImage, selectedImage.getWidth() / 10, selectedImage.getHeight() / 10, false);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] byteArrayArray = baos.toByteArray();
                    this.encodedImage = Base64.encodeToString(byteArrayArray, Base64.DEFAULT);

                    this.userImagePreview.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_chooser_button:
                pickFromGallery();
                break;

            case R.id.register:
                doRegister();

        }
    }

    private void doRegister() {
        String username = this.usernameEditText.getText().toString();
        String email = this.emailEditText.getText().toString();

        String name = this.nameEditText.getText().toString();
        String surnmae = this.surnameEditText.getText().toString();

        String password = this.passwordEditText.getText().toString();
        String confirmPassword = this.confirmPasswordEditText.getText().toString();

        String street = this.streetEditText.getText().toString();
        String city = this.cityEditText.getText().toString();
        String region = this.regionEditText.getText().toString();
        String country = this.countryEditText.getText().toString();

        String tel = this.telEditText.getText().toString();

        String role = "";

        switch (this.radioRole.getCheckedRadioButtonId()) {
            case R.id.radioClient:
                role = "client";
                break;

            case R.id.radioCompany:
                role = "company";
                break;
        }


        if (!password.equals("") && password.equals(confirmPassword)) {
            User newUser = new User();

            newUser.setUsername(username);
            newUser.setEmail(email);

            newUser.setName(name);
            newUser.setSurname(surnmae);

            String generatedPassword = "";
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                byte[] bytes = new byte[0];
                bytes = md.digest(password.getBytes());

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < bytes.length; i++) {
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                generatedPassword = sb.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            newUser.setPassword(generatedPassword);

            newUser.setAddress(street);
            newUser.setCity(city);
            newUser.setRegion(region);
            newUser.setCountry(country);

            newUser.setImg(this.encodedImage);

            newUser.setTel(tel);

            newUser.setRole(role);

            String userJson = new Gson().toJson(newUser);
            Intent intent = new Intent(getContext(), UserService.class);
            intent.setAction(UserService.ACTION_REGISTER);
            intent.putExtra("user", userJson);
            getActivity().startService(intent);
        } else {
            Toast.makeText(getContext(), R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
        }
    }

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(UserService.ACTION_REGISTER));
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

}
