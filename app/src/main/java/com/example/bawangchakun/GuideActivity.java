package com.example.bawangchakun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

public class GuideActivity extends AppCompatActivity {

    private LinearLayout llDots;
    private Button btnStart;
    private View[] dots;

    private final int[] icons = {
            R.drawable.ic_guide_1,
            R.drawable.ic_guide_2,
            R.drawable.ic_guide_3
    };

    private final String[] titles = {
            "精选好茶",
            "自由定制",
            "轻松下单"
    };

    private final String[] descriptions = {
            "原叶鲜奶茶，品质之选\n每一杯都是匠心之作",
            "杯型、甜度、温度随心选\n打造专属你的那一杯",
            "一键下单，快速取餐\n让好茶触手可及"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        ViewPager2 vpGuide = findViewById(R.id.vp_guide);
        llDots = findViewById(R.id.ll_dots);
        btnStart = findViewById(R.id.btn_start);

        GuidePagerAdapter adapter = new GuidePagerAdapter(icons, titles, descriptions);
        vpGuide.setAdapter(adapter);

        initDots(icons.length);
        updateDots(0);

        vpGuide.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
                if (position == icons.length - 1) {
                    btnStart.setVisibility(View.VISIBLE);
                } else {
                    btnStart.setVisibility(View.GONE);
                }
            }
        });

        btnStart.setOnClickListener(v -> {
            // 标记已不是首次启动
            SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
            sp.edit().putBoolean("is_first_launch", false).apply();

            startActivity(new Intent(GuideActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void initDots(int count) {
        dots = new View[count];
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            dot.setBackgroundResource(R.drawable.dot_unselected);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dp2px(10), dp2px(10));
            params.setMargins(dp2px(6), 0, dp2px(6), 0);
            dot.setLayoutParams(params);
            llDots.addView(dot);
            dots[i] = dot;
        }
    }

    private void updateDots(int selected) {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundResource(
                    i == selected ? R.drawable.dot_selected : R.drawable.dot_unselected);
        }
    }

    private int dp2px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
