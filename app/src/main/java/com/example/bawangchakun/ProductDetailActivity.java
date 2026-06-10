package com.example.bawangchakun;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bawangchakun.db.DatabaseHelper;
import com.example.bawangchakun.model.Product;

public class ProductDetailActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private long currentUserId;
    private String selectedCupSize = "大杯";
    private String selectedSweetness = "全糖";
    private String selectedIce = "正常冰";
    private int quantity = 1;
    private Product product;
    private double selectedPrice;
    private TextView tvPrice;

    private TextView[] cupViews = new TextView[3];
    private TextView[] sweetViews = new TextView[5];
    private TextView[] iceViews = new TextView[5];
    private TextView tvQuantity;

    private String[] cupOptions = {"大杯", "中杯", "小杯"};
    // 杯型价格调整：大杯=基础价，中杯-3，小杯-5
    private double[] cupPriceOffset = {0, -3, -5};
    private String[] sweetOptions = {"全糖", "七分糖", "半糖", "三分糖", "无糖"};
    private String[] iceOptions = {"正常冰", "少冰", "去冰", "温", "热"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = DatabaseHelper.getInstance(this);
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        currentUserId = sp.getLong("user_id", -1);

        long productId = getIntent().getLongExtra("product_id", -1);
        product = dbHelper.getProductById(productId);

        TextView tvBack = findViewById(R.id.tv_back);
        TextView tvName = findViewById(R.id.tv_detail_name);
        TextView tvDesc = findViewById(R.id.tv_detail_desc);
        tvPrice = findViewById(R.id.tv_detail_price);
        Button btnAdd = findViewById(R.id.btn_add_to_cart);
        tvQuantity = findViewById(R.id.tv_quantity);

        tvBack.setOnClickListener(v -> finish());

        if (product != null) {
            tvName.setText(product.getName());
            tvDesc.setText(product.getDescription());
            selectedPrice = product.getPrice(); // 大杯=基础价
            tvPrice.setText("¥" + String.format("%.0f", selectedPrice));

            ImageView ivDetail = findViewById(R.id.iv_detail_image);
            String imageName = product.getImage();
            if (imageName != null && !imageName.isEmpty()) {
                int resId = getResources().getIdentifier(
                        imageName, "drawable", getPackageName());
                if (resId != 0) {
                    ivDetail.setImageResource(resId);
                }
            }
        }

        // 杯型
        cupViews[0] = findViewById(R.id.cup_1);
        cupViews[1] = findViewById(R.id.cup_2);
        cupViews[2] = findViewById(R.id.cup_3);
        for (int i = 0; i < cupViews.length; i++) {
            final int index = i;
            cupViews[i].setOnClickListener(v -> selectCupSize(index));
        }

        // 甜度
        sweetViews[0] = findViewById(R.id.sweet_1);
        sweetViews[1] = findViewById(R.id.sweet_2);
        sweetViews[2] = findViewById(R.id.sweet_3);
        sweetViews[3] = findViewById(R.id.sweet_4);
        sweetViews[4] = findViewById(R.id.sweet_5);
        for (int i = 0; i < sweetViews.length; i++) {
            final int index = i;
            sweetViews[i].setOnClickListener(v -> selectSweetness(index));
        }

        // 温度
        iceViews[0] = findViewById(R.id.ice_1);
        iceViews[1] = findViewById(R.id.ice_2);
        iceViews[2] = findViewById(R.id.ice_3);
        iceViews[3] = findViewById(R.id.ice_4);
        iceViews[4] = findViewById(R.id.ice_5);
        for (int i = 0; i < iceViews.length; i++) {
            final int index = i;
            iceViews[i].setOnClickListener(v -> selectIce(index));
        }

        // 数量
        findViewById(R.id.btn_qty_minus).setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });
        findViewById(R.id.btn_qty_plus).setOnClickListener(v -> {
            if (quantity < 99) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnAdd.setOnClickListener(v -> {
            if (product != null) {
                dbHelper.addToCart(currentUserId, product.getId(),
                        selectedSweetness, selectedIce, selectedCupSize, quantity, selectedPrice);
                Toast.makeText(this, "已加入购物车", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void selectCupSize(int index) {
        selectedCupSize = cupOptions[index];
        updateTagSelection(cupViews, index);
        // 根据杯型更新价格
        if (product != null) {
            selectedPrice = product.getPrice() + cupPriceOffset[index];
            tvPrice.setText("¥" + String.format("%.0f", selectedPrice));
        }
    }

    private void selectSweetness(int index) {
        selectedSweetness = sweetOptions[index];
        updateTagSelection(sweetViews, index);
    }

    private void selectIce(int index) {
        selectedIce = iceOptions[index];
        updateTagSelection(iceViews, index);
    }

    private void updateTagSelection(TextView[] views, int selectedIndex) {
        for (int i = 0; i < views.length; i++) {
            if (i == selectedIndex) {
                views[i].setBackgroundResource(R.drawable.tag_selected_bg);
                views[i].setTextColor(0xFFFFFFFF);
            } else {
                views[i].setBackgroundResource(R.drawable.tag_unselected_bg);
                views[i].setTextColor(0xFF666666);
            }
        }
    }
}
