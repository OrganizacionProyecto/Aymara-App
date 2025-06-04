package com.example.aymara_app.network;

import com.example.aymara_app.CartResponse;
import com.example.aymara_app.PedidoResponse;
import com.example.aymara_app.RegisterRequest;
import com.example.aymara_app.RegisterResponse;
import com.example.aymara_app.Product;
import com.example.aymara_app.Categoria;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.*;

public interface ApiService {

        // ---------- PRODUCTS ----------
        @GET("api/products/productos/")
        Call<List<Product>> getProducts();

        @GET("api/products/productos/{id}/")
        Call<Product> getProductDetail(@Path("id") int productId);

        // ---------- FAVORITOS ----------
        @GET("api/products/favoritos/")
        Call<List<Product>> getFavorites(@Header("Authorization") String token);

        @POST("api/products/favoritos/")
        Call<ResponseBody> addToFavorites(@Body Map<String, Integer> body, @Header("Authorization") String token);

        @DELETE("api/products/favoritos/{id}/")
        Call<ResponseBody> removeFromFavorites(@Path("id") int favoritoId, @Header("Authorization") String token);

        // ---------- CARRITO ----------
        @GET("api/cart/carrito/")
        Call<CartResponse> getCart(@Header("Authorization") String token);

        @POST("api/cart/carrito/agregar/")
        Call<ResponseBody> agregarAlCarrito(
                @Body Map<String, Object> body,
                @Header("Authorization") String authHeader
        );

        @PUT("api/cart/carrito/modificar/{producto_id}/")
        Call<ResponseBody> updateCartItem(
                @Path("producto_id") int productoId,
                @Body Map<String, Object> body,
                @Header("Authorization") String token
        );
        @DELETE("api/cart/carrito/eliminar/{producto_id}/")
        Call<ResponseBody> deleteCartItem(
                @Path("producto_id") int productoId,
                @Header("Authorization") String token
        );
        //---------- CATEGORIAS --------

        @GET("api/products/categorias/")
        Call<List<Categoria>> getNombre();

        // ---------- PEDIDOS ----------


        @GET("api/cart/pedido/historial/")
        Call<ResponseBody> getHistorialPedidos(@Header("Authorization") String token);

        @GET("api/factura/{pedidoId}/")
        Call<ResponseBody> getFactura(@Path("pedidoId") int pedidoId);

        // ---------- AUTENTICACIÓN / USUARIOS ----------
        @POST("api/users/")
        Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

        @POST("api/users/auth/logout/")
        Call<ResponseBody> logout(@Header("Authorization") String token);

        @GET("api/users/me/")
        Call<ResponseBody> getUserDetails(@Header("Authorization") String token);

        @PATCH("api/users/change_email/")
        Call<ResponseBody> changeEmail(@Header("Authorization") String token, @Body Map<String, String> body);

        @PATCH("api/users/change-direccion/")
        Call<ResponseBody> changeAddress(@Header("Authorization") String token, @Body Map<String, String> body);

        @PATCH("api/users/me/")
        Call<ResponseBody> updateUser(@Header("Authorization") String token, @Body RequestBody body);


        @POST("api/users/change_password/")
        Call<ResponseBody> changePassword(@Header("Authorization") String token, @Body RequestBody body);

        @DELETE("api/users/deactivate_account/")
        Call<ResponseBody> deleteAccount(@Header("Authorization") String token);

        @POST("api/auth/token/refresh/")
        Call<ResponseBody> refreshToken();

        @POST("api/cart/pedido/crear/")
        Call<PedidoResponse> createPedido(
                @Body Map<String, Object> datosPedido,
                @Header("Authorization") String authHeader
        );
}


