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
import java.util.HashSet;
import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList = new ArrayList<>();
    private ApiService apiService;
    private boolean isLoggedIn;

    private HashSet<Integer>favoriteProducts = new HashSet<>();

    private Context context;

    public ProductAdapter(boolean isLoggedIn, Context context) {
        apiService = ApiClient.getClient().create(ApiService.class);
        this.isLoggedIn = isLoggedIn;
        this.context = context;
        loadFavoriteState();
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
        notifyDataSetChanged();
    }

    public void setProductList(List<Product> products, Context context) {
        this.productList = products;
        notifyDataSetChanged();
    }

    private void loadFavoriteState(){
        SharedPreferences prefs = context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        Set<String> favoriteIds = prefs.getStringSet("favorite_products", new HashSet<>());
        for (String id : favoriteIds) {
            try {
                favoriteProducts.add(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                Log.e("ProductAdapter", "Error al parsear ID de favorito: " + id, e);
            }
        }
        notifyDataSetChanged();
    }

    private void saveFavoriteState() {
        SharedPreferences prefs = context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> favoriteIds = new HashSet<>();
        for (Integer id : favoriteProducts) {
            favoriteIds.add(String.valueOf(id));
        }
        editor.putStringSet("favorite_products", favoriteIds);
        editor.apply();
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

        if (favoriteProducts.contains(product.getIdProducto())) {
            holder.favoriteButton.setImageResource(R.drawable.favorito_color);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.favorito_rojo);
        }

        holder.favoriteButton.setOnClickListener(v -> {
            int productId = product.getIdProducto();
            if (favoriteProducts.contains(productId)) {
                favoriteProducts.remove(productId);
                holder.favoriteButton.setImageResource(R.drawable.favorito_rojo);
                removeFromFavorites(product, context);
            } else {
                favoriteProducts.add(productId);
                holder.favoriteButton.setImageResource(R.drawable.favorito_color);
                addToFavorites(product, context);
            }
            saveFavoriteState();
        });


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

    // En ProductAdapter.java

    private void removeFromFavorites(Product product, Context context) {
        String token = getAccessToken(context);

        if (!token.isEmpty()) {
            int idDelProducto = product.getIdProducto();
            if (idDelProducto <= 0) {
                Log.e("ProductAdapter", "ID del producto inválido para eliminar: " + idDelProducto);
                return;
            }

            // *** AHORA SÍ, ESTA ES LA CORRECCIÓN CLAVE EN removeFromFavorites ***
            // Crea un Map con el ID del producto
            Map<String, Integer> productIdMap = new HashMap<>(); // Renombrado a productIdMap para claridad
            productIdMap.put("producto_id", idDelProducto); // <-- ¡Aquí pasamos el ID real!

            // Convierte el Map a JSON y luego a RequestBody
            Gson gson = new Gson();
            String json = gson.toJson(productIdMap);
            RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

            // Llama a la API Service pasando el RequestBody correcto
            apiService.removeFromFavorites(requestBody, "Bearer " + token).enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d("ProductAdapter", "Producto eliminado de favoritos");
                        Toast.makeText(context, "Producto eliminado de favoritos", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ProductAdapter", "Error al eliminar de favoritos: " + response.code() + " " + response.message());
                        Toast.makeText(context, "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show();
                        // *** RECOMENDACIÓN: Revertir estado local si la API falla ***
                        favoriteProducts.add(idDelProducto); // Lo vuelve a añadir al set si falló en el backend
                        saveFavoriteState(); // Guarda el estado revertido
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("ProductAdapter", "Error al eliminar de favoritos: " + t.getMessage());
                    Toast.makeText(context, "Error de red al eliminar favoritos", Toast.LENGTH_SHORT).show();
                    // *** RECOMENDACIÓN: Revertir estado local si la conexión falla ***
                    favoriteProducts.add(idDelProducto); // Lo vuelve a añadir al set si falló la conexión
                    saveFavoriteState(); // Guarda el estado revertido
                }
            });
        } else {
            Log.e("ProductAdapter", "Token no encontrado. No se puede eliminar de favoritos.");
            Toast.makeText(context, "Inicia sesión para gestionar favoritos", Toast.LENGTH_SHORT).show();
            // Si no hay token, asume que la operación no se realizó y revierte el cambio visual
            favoriteProducts.add(product.getIdProducto()); // Vuelve a añadirlo localmente
            saveFavoriteState(); // Guarda el estado revertido
        }
    }

}
