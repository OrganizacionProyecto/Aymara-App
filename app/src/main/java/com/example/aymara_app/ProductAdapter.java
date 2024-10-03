package com.example.aymara_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
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
        holder.productImagen.setImageResource(product.getImagen());
        holder.favoriteButton.setSelected(product.isFavorite());
        holder.favoriteButton.setOnClickListener(v -> {
            boolean isSelected = !holder.favoriteButton.isSelected();
            holder.favoriteButton.setSelected(isSelected);
            product.setFavorite(isSelected);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImagen;
        TextView productNombre;
        TextView productDescripcion;
        ImageButton favoriteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImagen = itemView.findViewById(R.id.product_imagen);
            productNombre = itemView.findViewById(R.id.product_nombre);
            productDescripcion = itemView.findViewById(R.id.product_descripcion);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }
}
