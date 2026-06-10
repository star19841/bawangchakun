package com.example.bawangchakun;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bawangchakun.R;
import com.example.bawangchakun.adapter.OrderAdapter;
import com.example.bawangchakun.db.DatabaseHelper;
import com.example.bawangchakun.model.Order;
import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> finish());

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        long userId = sp.getLong("user_id", -1);

        List<Order> orders = dbHelper.getOrders(userId);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        RecyclerView rvOrders = findViewById(R.id.rv_orders);

        if (orders.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            OrderAdapter adapter = new OrderAdapter(orders, dbHelper);
            rvOrders.setLayoutManager(new LinearLayoutManager(this));
            rvOrders.setAdapter(adapter);
        }
    }
}
