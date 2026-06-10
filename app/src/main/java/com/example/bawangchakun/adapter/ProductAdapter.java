package com.example.bawangchakun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bawangchakun.R;
import com.example.bawangchakun.model.Product;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_PRODUCT = 1;

    private List<Object> items;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCart(Product product);
    }

    public ProductAdapter(List<Object> items, OnProductClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_PRODUCT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvHeader.setText((String) items.get(position));
        } else if (holder instanceof ProductViewHolder) {
            Product product = (Product) items.get(position);
            ProductViewHolder vh = (ProductViewHolder) holder;
            vh.tvName.setText(product.getName());
            vh.tvDesc.setText(product.getDescription());
            vh.tvPrice.setText("¥" + product.getPrice());

            // Load image from drawable
            Context context = vh.itemView.getContext();
            String imageName = product.getImage();
            if (imageName != null && !imageName.isEmpty()) {
                int resId = context.getResources().getIdentifier(
                        imageName, "drawable", context.getPackageName());
                if (resId != 0) {
                    vh.ivImage.setImageResource(resId);
                } else {
                    vh.ivImage.setImageResource(R.drawable.product_img_bg);
                }
            } else {
                vh.ivImage.setImageResource(R.drawable.product_img_bg);
            }

            vh.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(product);
            });
            vh.btnAdd.setOnClickListener(v -> {
                if (listener != null) listener.onAddToCart(product);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        HeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_category_header);
        }
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvDesc, tvPrice, btnAdd;
        ProductViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_product_image);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvDesc = itemView.findViewById(R.id.tv_product_desc);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            btnAdd = itemView.findViewById(R.id.btn_add_cart);
        }
    }
}
