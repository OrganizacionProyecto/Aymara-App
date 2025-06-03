package com.example.aymara_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders = new ArrayList<>();

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView orderId, orderDetails;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderId = itemView.findViewById(android.R.id.text1);
            orderDetails = itemView.findViewById(android.R.id.text2);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderId.setText("Pedido #" + order.getId());
        holder.orderDetails.setText(order.getFechaCreacion() + " - $" + order.getTotal() + " (" + order.getMetodoPago() + ")");
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }
}