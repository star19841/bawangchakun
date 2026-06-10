package com.example.bawangchakun.adapter;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bawangchakun.R;
import com.example.bawangchakun.model.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> categories;
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(int position, long categoryId);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvName.setText(category.getName());

        if (position == selectedPosition) {
            holder.llContainer.setBackgroundColor(Color.WHITE);
            holder.tvName.setTextColor(0xFF7B8B6F);
            holder.tvName.setTextSize(14);
            holder.tvName.getPaint().setFakeBoldText(true);
            holder.indicator.setVisibility(View.VISIBLE);
            holder.indicator.setBackgroundColor(0xFF7B8B6F);
        } else {
            holder.llContainer.setBackgroundColor(0xFFF5F0E8);
            holder.tvName.setTextColor(0xFF8B8B8B);
            holder.tvName.setTextSize(13);
            holder.tvName.getPaint().setFakeBoldText(false);
            holder.indicator.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onCategoryClick(selectedPosition, category.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selectedPosition);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llContainer;
        TextView tvName;
        View indicator;
        ViewHolder(View itemView) {
            super(itemView);
            llContainer = itemView.findViewById(R.id.ll_category);
            tvName = itemView.findViewById(R.id.tv_category_name);
            indicator = itemView.findViewById(R.id.indicator);
        }
    }
}
