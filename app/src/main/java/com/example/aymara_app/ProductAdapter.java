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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

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

        Context context = holder.itemView.getContext();
        String token = getAccessToken(context);

        if (isLoggedIn) {
            holder.favoriteButton.setVisibility(View.VISIBLE);
            boolean isFavorite = isProductFavorite(product.getIdProducto(), context);
            holder.favoriteButton.setImageResource(isFavorite ? R.drawable.favorito_color : R.drawable.favorito);

            holder.favoriteButton.setOnClickListener(v -> {
                boolean currentFavorite = isProductFavorite(product.getIdProducto(), context);

                if (currentFavorite) {
                    removeFromFavorites(product, context);
                    holder.favoriteButton.setImageResource(R.drawable.favorito);
                    saveFavoriteState(product.getIdProducto(), false, context);
                } else {
                    addToFavorites(product, context);
                    holder.favoriteButton.setImageResource(R.drawable.favorito_color);
                    saveFavoriteState(product.getIdProducto(), true, context);
                }
            });
        } else {
            holder.favoriteButton.setVisibility(View.GONE); // Ocultar el botón si no está logueado
        }


        holder.btnAgregarCarrito.setOnClickListener(v -> {
            if (token.isEmpty()) {
                Toast.makeText(context, "Debes iniciar sesión para agregar productos al carrito.", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("producto_id", product.getIdProducto());
            payload.put("cantidad", 1);

            Log.d("Carrito", "Payload: " + payload);

            apiService.agregarAlCarrito(payload, "Bearer " + token).enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("Carrito", "Error al agregar al carrito: " + response.code() + " - " + errorBody);
                            Toast.makeText(context, "Error al agregar al carrito: " + response.code(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("Carrito", "Error parsing errorBody", e);
                            Toast.makeText(context, "Error al agregar al carrito", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show();
                    Log.e("Carrito", "Fallo: " + t.getMessage());
                }
            });
        });
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

            if (idDelProducto <= 0) {
                Log.e("ProductAdapter", "ID del producto inválido: " + idDelProducto);
                return;
            }

            Map<String, Integer> productId = new HashMap<>();
            productId.put("producto_id", idDelProducto);

            apiService.addToFavorites(productId, "Bearer " + token).enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Producto añadido a favoritos", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("ProductAdapter", "Producto agregado a favoritos " + response.code() + " - " + errorBody);
                            Toast.makeText(context, "Agregado a favoritos: " + response.code(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("ProductAdapter", "Error parsing errorBody", e);
                            Toast.makeText(context, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("ProductAdapter", "Error de conexión: " + t.getMessage());
                    Toast.makeText(context, "Error de red al añadir a favoritos", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void removeFromFavorites(Product product, Context context) {
        String token = getAccessToken(context);

        if (!token.isEmpty()) {
            int idDelProducto = product.getIdProducto();

            Log.d("ProductAdapter", "Eliminando de favoritos el producto con ID: " + idDelProducto);

            apiService.removeFromFavorites(idDelProducto, "Bearer " + token).enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Producto eliminado de favoritos", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("ProductAdapter", "Error al eliminar de favoritos: " + response.code() + " - " + errorBody);
                            Toast.makeText(context, "Eliminado de favoritos: " + response.code(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("ProductAdapter", "Error parsing errorBody", e);
                            Toast.makeText(context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("ProductAdapter", "Error de red al eliminar de favoritos: " + t.getMessage());
                    Toast.makeText(context, "Error de red al eliminar de favoritos", Toast.LENGTH_SHORT).show();
                }
            });
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