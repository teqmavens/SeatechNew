package teq.development.seatech.ServerCall;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by vibrantappz on 12/18/2017.
 */

public interface apiCall {

    @FormUrlEncoded
    @POST("login.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> userLogin(
            @Field("username") String user,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("my-profile.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> getProfile(
            @Field("id") String id,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("change-password.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> changePwd(
            @Field("old_password") String old_password,
            @Field("password1") String password1,
            @Field("password2") String password2,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("logout.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> logout(
            @Field("id") String id,
            @Header("sessionID") String sessionid
    );

    @FormUrlEncoded
    @POST("forget-password.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> frgtPwd(
            @Field("email") String email,
            @Header("sessionID") String sessionid
    );

    @POST("edit.json")
    @Headers("tokenID: 1234567890234dfg456")
    Call<ResponseBody> updateProfile(
            @Body RequestBody files,
            @Header("sessionID") String sessionid
    );
}
