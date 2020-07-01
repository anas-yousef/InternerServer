package com.example.internetserver;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MyServer {

    @GET("/")
    public Call<TokenResponse> callGetSlash();

    @GET("/users/{userName}/token/")
    public Call<TokenResponse> callGetUserToken(@Path("userName") String userName);

    @GET("user/")
    Call<UserResponse> callGetUser(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("user/edit/")
    Call<UserResponse> callUpdatePrettyName(@Body SetUserPrettyNameRequest request, @Header("Authorization") String token);
}
