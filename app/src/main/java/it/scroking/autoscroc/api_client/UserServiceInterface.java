package it.scroking.autoscroc.api_client;

import it.scroking.autoscroc.models.CheckUserBody;
import it.scroking.autoscroc.models.LoginBody;
import it.scroking.autoscroc.models.LoginResponse;
import it.scroking.autoscroc.models.MessageResponse;
import it.scroking.autoscroc.models.RevalidateUserBody;
import it.scroking.autoscroc.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserServiceInterface extends ApiServiceInterface {

    @POST("login/")
    Call<LoginResponse> login(@Body LoginBody loginBody);

    @POST("register_user/")
    Call<MessageResponse> registerUser(@Body User user);

    @POST("register_admin/")
    Call<MessageResponse> registerAdmin(@Body User user);

    @POST("get_profile/")
    Call<User> getUserProfile(@Body CheckUserBody checkUserBody);

    @POST("revalidate_user/")
    Call<MessageResponse> revalidate(@Body RevalidateUserBody revalidateUserBody);

    @POST("logout/")
    Call<MessageResponse> logout(@Body CheckUserBody checkUserBody);
}
