package com.example.aymara_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface CartActionListener {
        void onModificarCantidad(CartItem item, String accion, int nuevaCantidad);
        void onEliminarProducto(CartItem item);
    }

    private List<CartItem> carrito;
    private CartActionListener listener;

    public CartAdapter(List<CartItem> carrito, CartActionListener listener) {
        this.carrito = carrito;
        this.listener = listener;
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

        holder.btnAumentar.setOnClickListener(v ->
                listener.onModificarCantidad(item, "aumentar", item.getCantidad() + 1));

        holder.btnDisminuir.setOnClickListener(v -> {
            if (item.getCantidad() > 1) {
                listener.onModificarCantidad(item, "disminuir", item.getCantidad() - 1);
            }
        });

        holder.btnEliminar.setOnClickListener(v ->
                listener.onEliminarProducto(item));
    }

    @Override
    public int getItemCount() {
        return carrito.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, precio, cantidad, total;
        Button btnAumentar, btnDisminuir, btnEliminar;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
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
