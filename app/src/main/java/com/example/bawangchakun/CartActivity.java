package com.example.bawangchakun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

public class CartActivity extends AppCompatActivity {
    private RecyclerView rvCart;
    private TextView tvEmpty, tvTotal;
    private CartAdapter cartAdapter;
    private DatabaseHelper dbHelper;
    private long currentUserId;
    private List<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = DatabaseHelper.getInstance(this);
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        currentUserId = sp.getLong("user_id", -1);

        TextView tvBack = findViewById(R.id.tv_back);
        rvCart = findViewById(R.id.rv_cart);
        tvEmpty = findViewById(R.id.tv_empty);
        tvTotal = findViewById(R.id.tv_total);
        Button btnCheckout = findViewById(R.id.btn_checkout);

        tvBack.setOnClickListener(v -> finish());

        cartAdapter = new CartAdapter(cartItems, new CartAdapter.OnCartActionListener() {
            @Override
            public void onQuantityChange(CartItem item, int newQuantity) {
                dbHelper.updateCartItemQuantity(item.getId(), newQuantity);
                loadCart();
            }

            @Override
            public void onDelete(CartItem item) {
                dbHelper.deleteCartItem(item.getId());
                loadCart();
            }
        });

        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(cartAdapter);

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "购物车是空的", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, ConfirmOrderActivity.class));
        });

        loadCart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
    }

    private void loadCart() {
        cartItems.clear();
        cartItems.addAll(dbHelper.getCartItems(currentUserId));
        cartAdapter.notifyDataSetChanged();

        if (cartItems.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvCart.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvCart.setVisibility(View.VISIBLE);
        }

        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProductPrice() * item.getQuantity();
        }
        tvTotal.setText("合计: ¥" + total);
    }
}
