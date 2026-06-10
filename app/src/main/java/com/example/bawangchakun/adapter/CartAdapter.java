package com.example.bawangchakun.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bawangchakun.R;
import com.example.bawangchakun.model.CartItem;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> items;
    private OnCartActionListener listener;

    public interface OnCartActionListener {
        void onQuantityChange(CartItem item, int newQuantity);
        void onDelete(CartItem item);
    }

    public CartAdapter(List<CartItem> items, OnCartActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.tvName.setText(item.getProductName());
        holder.tvSpec.setText(item.getCupSize() + "/" + item.getSweetness() + "/" + item.getIce());
        holder.tvPrice.setText("¥" + (item.getProductPrice() * item.getQuantity()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        holder.btnMinus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChange(item, item.getQuantity() - 1);
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChange(item, item.getQuantity() + 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSpec, tvPrice, tvQuantity, btnMinus, btnPlus;
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_cart_name);
            tvSpec = itemView.findViewById(R.id.tv_cart_spec);
            tvPrice = itemView.findViewById(R.id.tv_cart_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
        }
    }
}
