package com.example.aymara_app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView; // Asegúrate de que esta línea está presente

import com.bumptech.glide.Glide;
import com.example.aymara_app.network.ApiClient;
import com.example.aymara_app.network.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList = new ArrayList<>();
    private ApiService apiService;

    public ProductAdapter() {
        // Inicializar el ApiService
        apiService = ApiClient.getClient().create(ApiService.class);
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

        // Cambiar color del botón favorito según el estado
        if (product.isFavorite()) {
            holder.favoriteButton.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.red));
        } else {
            holder.favoriteButton.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.grey));
        }

        holder.favoriteButton.setOnClickListener(v -> {
            product.setFavorite(!product.isFavorite());
            notifyItemChanged(position);

            // Aquí podrías llamar a la API para agregar o quitar de favoritos
            if (product.isFavorite()) {
                addToFavorites(product); // Llamar a la API
            } else {
                removeFromFavorites(product); // Llamar a la API
            }
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

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImagen = itemView.findViewById(R.id.product_imagen);
            productNombre = itemView.findViewById(R.id.product_nombre);
            productDescripcion = itemView.findViewById(R.id.product_descripcion);
            productPrecio = itemView.findViewById(R.id.product_precio);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }

    private void addToFavorites(Product product) {
        Map<String, Integer> productId = new HashMap<>();
        productId.put("producto_id", product.getIdProducto());

        apiService.addToFavorites(productId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("ProductAdapter", "Producto añadido a favoritos");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ProductAdapter", "Error al añadir a favoritos: " + t.getMessage());
            }
        });
    }

    private void removeFromFavorites(Product product) {
        Map<String, Integer> productId = new HashMap<>();
        productId.put("producto_id", product.getIdProducto());

        apiService.removeFromFavorites(productId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("ProductAdapter", "Producto eliminado de favoritos");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ProductAdapter", "Error al eliminar de favoritos: " + t.getMessage());
            }
        });
    }
}
