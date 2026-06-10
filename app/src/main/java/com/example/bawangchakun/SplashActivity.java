package com.example.bawangchakun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
            boolean isFirstLaunch = sp.getBoolean("is_first_launch", true);

            if (isFirstLaunch) {
                startActivity(new Intent(SplashActivity.this, GuideActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 1500);
    }
}
