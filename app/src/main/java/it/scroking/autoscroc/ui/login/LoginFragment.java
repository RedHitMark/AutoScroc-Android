package it.scroking.autoscroc.ui.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import it.scroking.autoscroc.MainActivity;
import it.scroking.autoscroc.R;
import it.scroking.autoscroc.services.UserService;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText usernameEditText;
    private EditText passwordEditText;

    private Button loginButton;

    private ProgressBar progressBar;

    private ResponseReceiver receiver = new ResponseReceiver();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        this.usernameEditText = root.findViewById(R.id.username_edit_text);
        this.passwordEditText = root.findViewById(R.id.password_edit_text);

        this.loginButton = root.findViewById(R.id.login_button);
        this.loginButton.setOnClickListener(this);

        this.progressBar = root.findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.GONE);

        return root;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(loginButton)) {
            doLogin();
        }
    }

    private void doLogin() {
        Intent intent = new Intent(getActivity(), UserService.class);
        intent.setAction(UserService.ACTION_LOGIN);

        intent.putExtra("username", this.usernameEditText.getText().toString());

        String generatedPassword = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = new byte[0];
            bytes = md.digest(passwordEditText.getText().toString().getBytes());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        intent.putExtra("password", generatedPassword);

        this.progressBar.setVisibility(View.VISIBLE);

        getActivity().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(UserService.ACTION_LOGIN));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(UserService.ACTION_GET_PROFILE));
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(UserService.ACTION_LOGIN)) {
                boolean loginSuccess = intent.getBooleanExtra("loginResponse", false);

                if (loginSuccess) {
                    Intent getProfileIntent = new Intent(getActivity(), UserService.class);
                    getProfileIntent.setAction(UserService.ACTION_GET_PROFILE);

                    getActivity().startService(getProfileIntent);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            } else if (intent != null && intent.getAction().equals(UserService.ACTION_GET_PROFILE)) {
                boolean getProfileSuccess = intent.getBooleanExtra("getProfileSuccess", false);

                if (getProfileSuccess) {
                    ((MainActivity) getActivity()).loadUser();
                    ((MainActivity) getActivity()).toHomeFragment(null);
                }

                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
