package com.example.aymara_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList = new ArrayList<>();

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


        holder.favoriteButton.setOnClickListener(v -> {
            product.setFavorite(!product.isFavorite());
            notifyItemChanged(position);
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
            productImagen = itemView.findViewById(R.id.product_image);
            productNombre = itemView.findViewById(R.id.product_name);
            productDescripcion = itemView.findViewById(R.id.product_description);
            productPrecio = itemView.findViewById(R.id.product_price);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }
}
