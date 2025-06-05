package com.example.aymara_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView; // <-- Importante
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // <-- Importante

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface CartActionListener {
        void onModificarCantidad(CartItem item, String accion, int nuevaCantidad);
        void onEliminarProducto(CartItem item);
    }

    private List<CartItem> carrito;
    private CartActionListener listener;
    private boolean mostrarBotonesCantidad;

    public CartAdapter(List<CartItem> carrito, CartActionListener listener, boolean mostrarBotonesCantidad) {
        this.carrito = carrito;
        this.listener = listener;
        this.mostrarBotonesCantidad = mostrarBotonesCantidad;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = carrito.get(position);
        holder.nombre.setText(item.getNombre_producto());
        holder.precio.setText(String.format("Precio: $%.2f", item.getPrecio_unitario()));
        holder.cantidad.setText(String.valueOf(item.getCantidad()));
        holder.total.setText(String.format("Total: $%.2f", item.getTotal_producto()));

        // Mostrar imagen del producto
        if (item.getProducto() != null && item.getProducto().getImagen() != null) {
            Glide.with(holder.itemView.getContext())
                .load(item.getProducto().getImagen())
                .into(holder.productImagen);
        } else {
            holder.productImagen.setImageResource(R.drawable.placeholder); // Usa un placeholder si no hay imagen
        }

        if (mostrarBotonesCantidad && listener != null) {
            holder.btnAumentar.setVisibility(View.VISIBLE);
            holder.btnDisminuir.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);

            holder.btnAumentar.setOnClickListener(v ->
                    listener.onModificarCantidad(item, "aumentar", item.getCantidad() + 1));

            holder.btnDisminuir.setOnClickListener(v -> {
                if (item.getCantidad() > 1) {
                    listener.onModificarCantidad(item, "disminuir", item.getCantidad() - 1);
                }
            });

            holder.btnEliminar.setOnClickListener(v ->
                    listener.onEliminarProducto(item));
        } else {
            holder.btnAumentar.setVisibility(View.GONE);
            holder.btnDisminuir.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);

            // Desactivar listeners por seguridad
            holder.btnAumentar.setOnClickListener(null);
            holder.btnDisminuir.setOnClickListener(null);
            holder.btnEliminar.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return carrito.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImagen; // <-- Nuevo
        TextView nombre, precio, cantidad, total;
        Button btnAumentar, btnDisminuir, btnEliminar;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImagen = itemView.findViewById(R.id.cart_product_imagen); // <-- Nuevo
            nombre = itemView.findViewById(R.id.nombreProducto);
            precio = itemView.findViewById(R.id.precioUnitario);
            cantidad = itemView.findViewById(R.id.cantidadProducto);
            total = itemView.findViewById(R.id.totalProducto);
            btnAumentar = itemView.findViewById(R.id.btnAumentar);
            btnDisminuir = itemView.findViewById(R.id.btnDisminuir);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
