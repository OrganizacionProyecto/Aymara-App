package com.example.aymara_app.network;

import com.example.aymara_app.RegisterRequest;
import com.example.aymara_app.Product;
import com.example.aymara_app.RegisterResponse;
import okhttp3.RequestBody;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Header;
import java.util.List;
import java.util.Map;
public interface ApiService {
    @GET("api/tablas/productos/")
    Call<List<Product>> getProducts();

    @POST("api/add_to_favorites/")
    Call<ResponseBody> addToFavorites(@Body Map<String, Integer> productId);

    @POST("api/remove_from_favorites/")
    Call<ResponseBody> removeFromFavorites(@Body Map<String, Integer> productId);

    @GET("api/list_favorites/")
    Call<List<Product>> getFavorites(@Header("Authorization") String token);

    @GET("api/auth/user/")
    Call<ResponseBody> getUserDetails(@Header("Authorization") String token);

    @PATCH("api/change_email/")
    Call<ResponseBody> changeEmail(@Header("Authorization") String token, @Body Map<String, String> body);

    @PATCH("api/change-direccion/")
    Call<ResponseBody> changeAddress(@Header("Authorization") String token, @Body Map<String, String> body);

    @PATCH("api/change-username/")
    Call<ResponseBody> changeUsername(@Header("Authorization") String token, @Body Map<String, String> body);

    @POST("api/change-password/")
    Call<ResponseBody> changePassword(@Header("Authorization") String authorization, @Body RequestBody body);
    @DELETE("api/auth/delete_account/")
    Call<ResponseBody> deleteAccount(@Header("Authorization") String token);

    @POST("api/auth/logout/")
    Call<ResponseBody> logout(@Header("Authorization") String token);

    @POST("api/auth/signup/")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

}


