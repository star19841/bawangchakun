package com.example.bawangchakun.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bawangchakun.R;
import com.example.bawangchakun.model.Order;
import com.example.bawangchakun.model.OrderItem;
import com.example.bawangchakun.db.DatabaseHelper;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<Order> orders;
    private DatabaseHelper dbHelper;

    public OrderAdapter(List<Order> orders, DatabaseHelper dbHelper) {
        this.orders = orders;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvOrderId.setText("订单号: " + order.getId());
        holder.tvStatus.setText(order.getStatus());
        holder.tvTime.setText(order.getCreateTime());
        holder.tvTotal.setText("¥" + order.getTotalPrice());

        List<OrderItem> items = dbHelper.getOrderItems(order.getId());
        StringBuilder sb = new StringBuilder();
        for (OrderItem item : items) {
            sb.append(item.getProductName())
              .append(" x").append(item.getQuantity())
              .append("  ").append(item.getCupSize()).append("/").append(item.getSweetness()).append("/").append(item.getIce())
              .append("\n");
        }
        holder.tvItems.setText(sb.toString().trim());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvTime, tvItems, tvTotal;
        ViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvTime = itemView.findViewById(R.id.tv_order_time);
            tvItems = itemView.findViewById(R.id.tv_order_items);
            tvTotal = itemView.findViewById(R.id.tv_order_total);
        }
    }
}
