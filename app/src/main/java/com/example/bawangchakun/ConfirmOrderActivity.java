package com.example.bawangchakun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bawangchakun.R;
import com.example.bawangchakun.adapter.CartAdapter;
import com.example.bawangchakun.db.DatabaseHelper;
import com.example.bawangchakun.model.CartItem;
import java.util.ArrayList;
import java.util.List;

public class ConfirmOrderActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private long currentUserId;
    private List<CartItem> orderItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        dbHelper = DatabaseHelper.getInstance(this);
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        currentUserId = sp.getLong("user_id", -1);

        TextView tvBack = findViewById(R.id.tv_back);
        RecyclerView rvItems = findViewById(R.id.rv_order_items);
        TextView tvTotal = findViewById(R.id.tv_order_total);
        Button btnConfirm = findViewById(R.id.btn_confirm);

        tvBack.setOnClickListener(v -> finish());

        orderItems.addAll(dbHelper.getCartItems(currentUserId));

        CartAdapter adapter = new CartAdapter(orderItems, null);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);

        double total = 0;
        for (CartItem item : orderItems) {
            total += item.getProductPrice() * item.getQuantity();
        }
        tvTotal.setText("合计: ¥" + total);

        btnConfirm.setOnClickListener(v -> {
            if (orderItems.isEmpty()) {
                Toast.makeText(this, "没有商品", Toast.LENGTH_SHORT).show();
                return;
            }
            dbHelper.createOrder(currentUserId, orderItems);
            Toast.makeText(this, "下单成功！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ConfirmOrderActivity.this, OrderListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
