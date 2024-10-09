package com.example.aymara_app.network;

import com.example.aymara_app.Product;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;
import java.util.Map;

public interface ApiService {
    @GET("api/tablas/productos/")
    Call<List<Product>> getProducts();

    @GET("api/list_favorites/")
    Call<List<Product>> getFavorites();

    // Agregar un producto a favoritos
    @POST("api/add_to_favorites/")
    Call<ResponseBody> addToFavorites(@Body Map<String, Integer> productId);

    // Eliminar un producto de favoritos
    @POST("api/remove_from_favorites/")
    Call<ResponseBody> removeFromFavorites(@Body Map<String, Integer> productId);

    @GET("api/auth/user/")
    Call<ResponseBody> getUserDetails();

    @PATCH("api/change_email/")
    Call<ResponseBody> changeEmail(@Body Map<String, String> body);

    @PATCH("api/change-direccion/")
    Call<ResponseBody> changeAddress(@Body Map<String, String> body);

    @PATCH("api/change-username/")
    Call<ResponseBody> changeUsername(@Body Map<String, String> body);

    @DELETE("api/auth/delete_account/")
    Call<ResponseBody> deleteAccount();

    @POST("api/auth/logout/")
    Call<ResponseBody> logout();

}


