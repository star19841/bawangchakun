package com.example.bawangchakun;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bawangchakun.adapter.CategoryAdapter;
import com.example.bawangchakun.adapter.ProductAdapter;
import com.example.bawangchakun.db.DatabaseHelper;
import com.example.bawangchakun.model.Category;
import com.example.bawangchakun.model.Product;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvCategory, rvProduct;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private DatabaseHelper dbHelper;
    private List<Category> categories = new ArrayList<>();
    private List<Object> mixedItems = new ArrayList<>();
    private long currentUserId;

    // Maps categoryId -> first index in mixedItems (the header position)
    private Map<Long, Integer> categoryHeaderIndex = new LinkedHashMap<>();
    // Maps category position (in categories list) -> categoryId
    private List<Long> categoryIds = new ArrayList<>();

    private boolean isScrollingFromClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DatabaseHelper.getInstance(this);
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        currentUserId = sp.getLong("user_id", -1);

        rvCategory = findViewById(R.id.rv_category);
        rvProduct = findViewById(R.id.rv_product);
        EditText etSearch = findViewById(R.id.et_search);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProducts(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadCategories();
        buildMixedList();
        setupAdapters();
        setupBottomNav();
    }

    private void loadCategories() {
        categories.clear();
        categoryIds.clear();

        // "全部" at the top
        Category allCat = new Category("全部", -1);
        allCat.setId(-1);
        categories.add(allCat);
        categoryIds.add(-1L);

        List<Category> dbCategories = dbHelper.getAllCategories();
        for (Category cat : dbCategories) {
            categories.add(cat);
            categoryIds.add(cat.getId());
        }
    }

    private void buildMixedList() {
        mixedItems.clear();
        categoryHeaderIndex.clear();

        // Add "全部" section with all products
        mixedItems.add("全部");
        categoryHeaderIndex.put(-1L, 0);

        List<Category> dbCategories = dbHelper.getAllCategories();
        for (Category cat : dbCategories) {
            int headerPos = mixedItems.size();
            mixedItems.add(cat.getName());
            categoryHeaderIndex.put(cat.getId(), headerPos);

            List<Product> products = dbHelper.getProductsByCategory(cat.getId());
            mixedItems.addAll(products);
        }
    }

    private void setupAdapters() {
        categoryAdapter = new CategoryAdapter(categories, (position, categoryId) -> {
            Integer headerIdx = categoryHeaderIndex.get(categoryId);
            if (headerIdx != null && headerIdx < mixedItems.size()) {
                isScrollingFromClick = true;
                ((LinearLayoutManager) rvProduct.getLayoutManager())
                        .scrollToPositionWithOffset(headerIdx, 0);
                rvProduct.post(() -> isScrollingFromClick = false);
            }
        });
        rvCategory.setLayoutManager(new LinearLayoutManager(this));
        rvCategory.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter(mixedItems, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onAddToCart(Product product) {
                showAddToCartDialog(product);
            }
        });
        LinearLayoutManager productLayoutManager = new LinearLayoutManager(this);
        rvProduct.setLayoutManager(productLayoutManager);
        rvProduct.setAdapter(productAdapter);

        // Scroll listener: sync left sidebar when scrolling
        rvProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isScrollingFromClick) return;

                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisible = lm.findFirstVisibleItemPosition();
                if (firstVisible == RecyclerView.NO_POSITION) return;

                // Find which category this position belongs to
                long detectedCategoryId = findCategoryForPosition(firstVisible);
                int categoryPos = categoryIds.indexOf(detectedCategoryId);
                if (categoryPos >= 0 && categoryPos != categoryAdapter.getSelectedPosition()) {
                    categoryAdapter.setSelectedPosition(categoryPos);
                    // Scroll sidebar to keep selected item visible
                    rvCategory.smoothScrollToPosition(categoryPos);
                }
            }
        });
    }

    private long findCategoryForPosition(int position) {
        // Walk backwards from position to find the nearest header
        for (int i = position; i >= 0; i--) {
            Object item = mixedItems.get(i);
            if (item instanceof String) {
                // It's a header - find its category ID
                for (Map.Entry<Long, Integer> entry : categoryHeaderIndex.entrySet()) {
                    if (entry.getValue() == i) {
                        return entry.getKey();
                    }
                }
            }
        }
        return -1;
    }

    private void searchProducts(String keyword) {
        if (keyword.isEmpty()) {
            // Restore normal grouped view
            buildMixedList();
            productAdapter.notifyDataSetChanged();
            rvCategory.setVisibility(View.VISIBLE);
            return;
        }

        // Search mode: flat list of matching products, no category headers
        rvCategory.setVisibility(View.GONE);
        mixedItems.clear();
        categoryHeaderIndex.clear();

        List<Product> allProducts = dbHelper.getProductsByCategory(-1);
        for (Product p : allProducts) {
            if (p.getName().contains(keyword) || p.getDescription().contains(keyword)) {
                mixedItems.add(p);
            }
        }
        productAdapter.notifyDataSetChanged();
    }

    private void showAddToCartDialog(Product product) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_to_cart, null);

        TextView tvName = dialogView.findViewById(R.id.tv_dialog_product_name);
        tvName.setText(product.getName());

        // Cup size
        TextView[] cupViews = {
                dialogView.findViewById(R.id.dialog_cup_1),
                dialogView.findViewById(R.id.dialog_cup_2),
                dialogView.findViewById(R.id.dialog_cup_3)
        };
        String[] cupOptions = {"大杯", "中杯", "小杯"};
        double[] cupPriceOffset = {0, -3, -5};
        final String[] selectedCup = {"大杯"};
        final double[] selectedPrice = {product.getPrice()};

        // Temperature
        TextView[] iceViews = {
                dialogView.findViewById(R.id.dialog_ice_1),
                dialogView.findViewById(R.id.dialog_ice_2),
                dialogView.findViewById(R.id.dialog_ice_3),
                dialogView.findViewById(R.id.dialog_ice_4)
        };
        String[] iceOptions = {"正常冰", "少冰", "常温", "热"};
        final String[] selectedIce = {"正常冰"};

        // Quantity
        TextView tvQty = dialogView.findViewById(R.id.dialog_tv_quantity);
        final int[] qty = {1};

        // 价格显示
        TextView tvDialogPrice = dialogView.findViewById(R.id.dialog_tv_price);
        tvDialogPrice.setText("¥" + String.format("%.0f", selectedPrice[0]));

        for (int i = 0; i < cupViews.length; i++) {
            final int idx = i;
            cupViews[i].setOnClickListener(v -> {
                selectedCup[0] = cupOptions[idx];
                selectedPrice[0] = product.getPrice() + cupPriceOffset[idx];
                tvDialogPrice.setText("¥" + String.format("%.0f", selectedPrice[0]));
                for (int j = 0; j < cupViews.length; j++) {
                    cupViews[j].setBackgroundResource(j == idx ? R.drawable.tag_selected_bg : R.drawable.tag_unselected_bg);
                    cupViews[j].setTextColor(j == idx ? 0xFFFFFFFF : 0xFF666666);
                }
            });
        }

        for (int i = 0; i < iceViews.length; i++) {
            final int idx = i;
            iceViews[i].setOnClickListener(v -> {
                selectedIce[0] = iceOptions[idx];
                for (int j = 0; j < iceViews.length; j++) {
                    iceViews[j].setBackgroundResource(j == idx ? R.drawable.tag_selected_bg : R.drawable.tag_unselected_bg);
                    iceViews[j].setTextColor(j == idx ? 0xFFFFFFFF : 0xFF666666);
                }
            });
        }

        dialogView.findViewById(R.id.dialog_btn_minus).setOnClickListener(v -> {
            if (qty[0] > 1) {
                qty[0]--;
                tvQty.setText(String.valueOf(qty[0]));
            }
        });
        dialogView.findViewById(R.id.dialog_btn_plus).setOnClickListener(v -> {
            if (qty[0] < 99) {
                qty[0]++;
                tvQty.setText(String.valueOf(qty[0]));
            }
        });

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("加入购物车", (dialog, which) -> {
                    dbHelper.addToCart(currentUserId, product.getId(),
                            "全糖", selectedIce[0], selectedCup[0], qty[0], selectedPrice[0]);
                    Toast.makeText(this, "已加入购物车", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void setupBottomNav() {
        LinearLayout tabAi = findViewById(R.id.tab_ai);
        LinearLayout tabCart = findViewById(R.id.tab_cart);
        LinearLayout tabOrder = findViewById(R.id.tab_order);
        LinearLayout tabProfile = findViewById(R.id.tab_profile);

        tabAi.setOnClickListener(v ->
                startActivity(new Intent(this, AIChatActivity.class)));
        tabCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
        tabOrder.setOnClickListener(v ->
                startActivity(new Intent(this, OrderListActivity.class)));
        tabProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}
