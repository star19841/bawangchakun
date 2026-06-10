package com.example.bawangchakun.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bawangchakun.ChatMessage;
import com.example.bawangchakun.ProductDetailActivity;
import com.example.bawangchakun.R;
import com.example.bawangchakun.model.Product;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final List<ChatMessage> messages;
    private final Context context;

    public ChatAdapter(List<ChatMessage> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = viewType == ChatMessage.TYPE_USER
                ? R.layout.item_chat_user
                : R.layout.item_chat_ai;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (message.getType() == ChatMessage.TYPE_USER) {
            TextView tvUserMsg = holder.itemView.findViewById(R.id.tv_user_msg);
            tvUserMsg.setText(message.getContent());
        } else {
            TextView tvAiMsg = holder.itemView.findViewById(R.id.tv_ai_msg);
            tvAiMsg.setText(message.getContent());

            RecyclerView rvRecommend = holder.itemView.findViewById(R.id.rv_recommend);
            List<Product> products = message.getRecommendedProducts();
            if (products != null && !products.isEmpty()) {
                rvRecommend.setVisibility(View.VISIBLE);
                rvRecommend.setLayoutManager(new LinearLayoutManager(context));
                rvRecommend.setAdapter(new RecommendProductAdapter(products, product -> {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("product_id", product.getId());
                    context.startActivity(intent);
                }));
            } else {
                rvRecommend.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
