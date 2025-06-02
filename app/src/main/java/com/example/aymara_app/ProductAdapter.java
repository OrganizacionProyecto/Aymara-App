package com.example.aymara_app;

import android.widget.Button;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.aymara_app.network.ApiClient;
import com.example.aymara_app.network.ApiService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import com.google.gson.Gson;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList = new ArrayList<>();
    private ApiService apiService;
    private boolean isLoggedIn;

    public ProductAdapter(boolean isLoggedIn, Context context) {
        apiService = ApiClient.getClient().create(ApiService.class);
        this.isLoggedIn = isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
        notifyDataSetChanged();
    }

    public void setProductList(List<Product> products, Context context) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productNombre.setText(product.getNombre());
        holder.productDescripcion.setText(product.getDescripcion());
        holder.productPrecio.setText("$" + product.getPrecio());

        Glide.with(holder.itemView.getContext())
                .load(product.getImagen())
                .into(holder.productImagen);


        if (isLoggedIn) {
            holder.btnAgregarCarrito.setVisibility(View.VISIBLE);

            holder.btnAgregarCarrito.setOnClickListener(v -> {
                Context context = holder.itemView.getContext();
                String token = getAccessToken(context);

                if (token.isEmpty()) {
                    Toast.makeText(context, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> payload = new HashMap<>();
                payload.put("producto_id", product.getIdProducto());
                payload.put("cantidad", 1); // O dejá elegir la cantidad

                Log.d("Carrito", "Payload: " + payload.toString());

                apiService.agregarAlCarrito(payload, "Bearer " + token).enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                Log.e("Carrito", "Error al agregar al carrito: " + response.code() + " - " + errorBody);
                            } catch (Exception e) {
                                Log.e("Carrito", "Error parsing errorBody", e);
                            }
                            Toast.makeText(context, "Error al agregar al carrito", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show();
                        Log.e("Carrito", "Fallo: " + t.getMessage());
                    }
                });
            });
        } else {
            holder.btnAgregarCarrito.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImagen;
        TextView productNombre, productDescripcion, productPrecio;
        ImageButton favoriteButton;
        Button btnAgregarCarrito;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImagen = itemView.findViewById(R.id.product_imagen);
            productNombre = itemView.findViewById(R.id.product_nombre);
            productDescripcion = itemView.findViewById(R.id.product_descripcion);
            productPrecio = itemView.findViewById(R.id.product_precio);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            btnAgregarCarrito = itemView.findViewById(R.id.btnAgregarCarrito);
        }
    }

    private String getAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        return prefs.getString("access_token", "");
    }

    private void addToFavorites(Product product, Context context) {
        String token = getAccessToken(context);

        if (!token.isEmpty()) {
            int idDelProducto = product.getIdProducto();

            Log.d("ProductAdapter", "Añadiendo a favoritos el producto con ID: " + idDelProducto);

            if (idDelProducto <= 0) {
                Log.e("ProductAdapter", "ID del producto inválido: " + idDelProducto);
                return;
            }

            Map<String, Integer> productId = new HashMap<>();
            productId.put("producto_id", idDelProducto);

            Gson gson = new Gson();
            String json = gson.toJson(productId);
            RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

            apiService.addToFavorites((Map<String, Integer>) requestBody, "Bearer " + token).enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d("ProductAdapter", "Producto añadido a favoritos");
                        Toast.makeText(context, "Producto añadido a favoritos", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ProductAdapter", "Error al añadir a favoritos: " + response.message());
                        Toast.makeText(context, "Error al añadir a favoritos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("ProductAdapter", "Error de conexión: " + t.getMessage());
                }
            });
        } else {
            Log.e("ProductAdapter", "Token no encontrado");
        }
    }

    private void removeFromFavorites(Product product, Context context) {
        String token = getAccessToken(context);

        if (!token.isEmpty()) {
            Map<String, Integer> productId = new HashMap<>();
            productId.put("producto_id", product.getIdProducto());

            apiService.removeFromFavorites(productId.size(), "Bearer " + token).enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d("ProductAdapter", "Producto eliminado de favoritos");
                        Toast.makeText(context, "Producto eliminado de favoritos", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ProductAdapter", "Error al eliminar de favoritos: " + response.code() + " " + response.message());
                        Toast.makeText(context, "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("ProductAdapter", "Error al eliminar de favoritos: " + t.getMessage());
                }
            });
        } else {
            Log.e("ProductAdapter", "Token no encontrado");
        }
    }

    private void saveFavoriteState(int productId, boolean isFavorite, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("favorito_" + productId, isFavorite);
        editor.apply();
    }


    private boolean isProductFavorite(int productId, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("favorito_" + productId, false);
    }
}
