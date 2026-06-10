package com.example.bawangchakun;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GuidePagerAdapter extends RecyclerView.Adapter<GuidePagerAdapter.ViewHolder> {

    private final int[] icons;
    private final String[] titles;
    private final String[] descriptions;

    public GuidePagerAdapter(int[] icons, String[] titles, String[] descriptions) {
        this.icons = icons;
        this.titles = titles;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guide_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ivIcon.setImageResource(icons[position]);
        holder.tvTitle.setText(titles[position]);
        holder.tvDesc.setText(descriptions[position]);
    }

    @Override
    public int getItemCount() {
        return icons.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvDesc;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_guide_icon);
            tvTitle = itemView.findViewById(R.id.tv_guide_title);
            tvDesc = itemView.findViewById(R.id.tv_guide_desc);
        }
    }
}
