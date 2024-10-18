package com.example.aymara_app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;
import org.json.JSONException;
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

    public void setProductList(List<Product> products) {
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
            holder.favoriteButton.setVisibility(View.VISIBLE);
            holder.favoriteButton.setColorFilter(product.isFavorite() ?
                    holder.itemView.getContext().getResources().getColor(R.color.red) :
                    holder.itemView.getContext().getResources().getColor(R.color.grey));

            holder.favoriteButton.setOnClickListener(v -> {
                product.setFavorite(!product.isFavorite());
                notifyItemChanged(position);
                if (product.isFavorite()) {
                    addToFavorites(product, holder.itemView.getContext());
                } else {
                    removeFromFavorites(product, holder.itemView.getContext());
                }
            });
        } else {
            holder.favoriteButton.setVisibility(View.GONE);
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

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImagen = itemView.findViewById(R.id.product_imagen);
            productNombre = itemView.findViewById(R.id.product_nombre);
            productDescripcion = itemView.findViewById(R.id.product_descripcion);
            productPrecio = itemView.findViewById(R.id.product_precio);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }

    private String getAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        return prefs.getString("access_token", ""); // Devuelve una cadena vacía si no hay token
    }

    private void addToFavorites(Product product, Context context) {
        String token = getAccessToken(context);

        if (!token.isEmpty()) {
            int idDelProducto = product.getIdProducto();

            // Verifica el ID antes de enviarlo
            Log.d("ProductAdapter", "Añadiendo a favoritos el producto con ID: " + idDelProducto);

            if (idDelProducto <= 0) {
                Log.e("ProductAdapter", "ID del producto inválido: " + idDelProducto);
                return; // No hacer nada si el ID es inválido
            }

            Map<String, Integer> productId = new HashMap<>();
            productId.put("producto_id", idDelProducto);

            Gson gson = new Gson();
            String json = gson.toJson(productId);
            RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

            apiService.addToFavorites(requestBody, "Bearer " + token).enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d("ProductAdapter", "Producto añadido a favoritos");
                    } else {
                        Log.e("ProductAdapter", "Error al añadir a favoritos: " + response.message());
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

            apiService.removeFromFavorites(productId, token).enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d("ProductAdapter", "Producto eliminado de favoritos");
                    } else {
                        Log.e("ProductAdapter", "Error al eliminar de favoritos: " + response.message());
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
}
