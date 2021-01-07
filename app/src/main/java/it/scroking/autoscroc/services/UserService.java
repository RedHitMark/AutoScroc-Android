package it.scroking.autoscroc.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import it.scroking.autoscroc.R;
import it.scroking.autoscroc.api_client.APIHandler;
import it.scroking.autoscroc.api_client.UserServiceInterface;
import it.scroking.autoscroc.models.CheckUserBody;
import it.scroking.autoscroc.models.LoginBody;
import it.scroking.autoscroc.models.LoginResponse;
import it.scroking.autoscroc.models.MessageResponse;
import it.scroking.autoscroc.models.RevalidateUserBody;
import it.scroking.autoscroc.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService extends IntentService {

    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_GET_PROFILE = "GET_PROFILE";
    public static final String ACTION_REVALIDATE_USER = "REVALIDATE_USER";
    public static final String ACTION_REGISTER = "REGISTER";
    public static final String ACTION_LOGOUT = "LOGOUT";

    private SharedPreferences sharedPreferences;


    public UserService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            this.sharedPreferences = getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);

            final String action = intent.getAction();
            if (ACTION_LOGIN.equals(action)) {
                this.login(intent);
            } else if (ACTION_GET_PROFILE.equals(action)) {
                this.getProfile(intent);
            } else if (ACTION_REVALIDATE_USER.equals(action)) {
                this.revalidate(intent);
            } else if (ACTION_REGISTER.equals(action)) {
                this.register(intent);
            } else if (ACTION_LOGOUT.equals(action)) {
                this.logout(intent);
            }
        }
    }

    private void revalidate(Intent intent) {
        UserServiceInterface userService = (UserServiceInterface) APIHandler.getInstance().getApiService(APIHandler.USER_SERVICE);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_REVALIDATE_USER);

        RevalidateUserBody body = new RevalidateUserBody();

        body.token = sharedPreferences.getString("token", "");
        body.uuid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        body.secret = sharedPreferences.getString("secret", "");

        Call<MessageResponse> call = userService.revalidate(body);

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("revalidateResponse", true));
                } else if (response.code() == 406) {
                    Toast.makeText(getApplicationContext(), "Impossibile revalidare l'utente", Toast.LENGTH_SHORT).show();
                } else {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("revalidateResponse", false));
                    Toast.makeText(getApplicationContext(), "Impossibile revalidare l'utente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("revalidateResponse", false));
            }
        });
    }

    private void register(Intent intent) {
        UserServiceInterface userService = (UserServiceInterface) APIHandler.getInstance().getApiService(APIHandler.USER_SERVICE);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_REGISTER);

        User newUser = new Gson().fromJson(intent.getStringExtra("user"), User.class);

        Call<MessageResponse> call = newUser.getRole().equals("client") ? userService.registerUser(newUser) : userService.registerAdmin(newUser);

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("registerResponse", true));
                } else if (response.code() == 406) {
                    Toast.makeText(getApplicationContext(), "Email not valid", Toast.LENGTH_SHORT).show();
                } else {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("registerResponse", false));
                    Toast.makeText(getApplicationContext(), "Impossibile registrare", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("registerResponse", false));
            }
        });
    }

    private void login(Intent intent) {
        LoginBody loginBody = new LoginBody();

        loginBody.username = intent.getStringExtra("username");
        loginBody.password = intent.getStringExtra("password");
        loginBody.uuid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        UserServiceInterface userService = (UserServiceInterface) APIHandler.getInstance().getApiService(APIHandler.USER_SERVICE);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_LOGIN);

        Call<LoginResponse> call = userService.login(loginBody);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null && !response.body().token.equals("")) {
                    Toast.makeText(getApplicationContext(), response.body().token, Toast.LENGTH_LONG).show();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", response.body().token);
                    editor.apply();

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("loginResponse", true));
                } else {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("loginResponse", false));
                    Toast.makeText(getApplicationContext(), "Username o password errate", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.server_unreachable_message, Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("loginResponse", false));
            }
        });
    }

    private void getProfile(Intent intent) {
        UserServiceInterface userService = (UserServiceInterface) APIHandler.getInstance().getApiService(APIHandler.USER_SERVICE);

        CheckUserBody checkUserBody = new CheckUserBody();

        checkUserBody.token = sharedPreferences.getString("token", "");
        checkUserBody.uuid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_GET_PROFILE);

        Call<User> call = userService.getUserProfile(checkUserBody);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    User user = response.body();

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("img", user.getImg());
                    editor.putString("username", user.getUsername());
                    editor.putString("email", user.getEmail());
                    editor.putString("name", user.getName());
                    editor.putString("surname", user.getSurname());
                    editor.putString("city", user.getCity());
                    editor.putString("username", user.getUsername());
                    editor.putString("region", user.getRegion());
                    editor.putString("country", user.getCountry());
                    editor.putString("role", user.getRole());
                    editor.putString("tel", user.getTel());

                    String toEncode = (user.getUsername() + user.getEmail());
                    String generatedSecret = "";
                    try {
                        MessageDigest md = MessageDigest.getInstance("SHA-512");
                        byte[] bytes = new byte[0];
                        bytes = md.digest(toEncode.getBytes());

                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < bytes.length; i++) {
                            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                        }
                        generatedSecret = sb.toString();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    editor.putString("secret", generatedSecret);

                    editor.apply();
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("getProfileSuccess", true));
                } else {
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("getProfileSuccess", false));
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("getProfileSuccess", false));
            }
        });
    }


    private void logout(Intent intent) {

        UserServiceInterface userService = (UserServiceInterface) APIHandler.getInstance().getApiService(APIHandler.USER_SERVICE);

        CheckUserBody body = new CheckUserBody();

        body.token = sharedPreferences.getString("token", "");
        body.uuid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_LOGOUT);

        Call<MessageResponse> call = userService.logout(body);
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                //doesn't care about response
                //let user logout anyway
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("img", "");
                editor.putString("username", "");
                editor.putString("email", "");
                editor.putString("name", "");
                editor.putString("surname", "");
                editor.putString("city", "");
                editor.putString("username", "");
                editor.putString("region", "");
                editor.putString("country", "");
                editor.putString("role", "");
                editor.putString("tel", "");

                editor.apply();
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("logoutSuccess", true));
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                t.printStackTrace();
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent.putExtra("logoutSuccess", false));
            }
        });
    }
}
