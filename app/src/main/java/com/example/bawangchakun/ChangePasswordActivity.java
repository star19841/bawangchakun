package com.example.bawangchakun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bawangchakun.db.DatabaseHelper;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private View strength1, strength2, strength3;
    private TextView tvStrength;
    private DatabaseHelper dbHelper;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        dbHelper = DatabaseHelper.getInstance(this);

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        userId = sp.getLong("user_id", -1);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        strength1 = findViewById(R.id.strength_1);
        strength2 = findViewById(R.id.strength_2);
        strength3 = findViewById(R.id.strength_3);
        tvStrength = findViewById(R.id.tv_strength);

        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> finish());

        Button btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> submitPassword());
    }

    private void setupListeners() {
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updatePasswordStrength(s.toString());
            }
        });
    }

    private void updatePasswordStrength(String password) {
        int strength = calculatePasswordStrength(password);

        strength1.setBackgroundColor(Color.parseColor("#E8EDE4"));
        strength2.setBackgroundColor(Color.parseColor("#E8EDE4"));
        strength3.setBackgroundColor(Color.parseColor("#E8EDE4"));

        switch (strength) {
            case 1:
                strength1.setBackgroundColor(Color.parseColor("#FF6B6B"));
                tvStrength.setText("密码强度：弱");
                break;
            case 2:
                strength1.setBackgroundColor(Color.parseColor("#FFA500"));
                strength2.setBackgroundColor(Color.parseColor("#FFA500"));
                tvStrength.setText("密码强度：中");
                break;
            case 3:
                strength1.setBackgroundColor(Color.parseColor("#4CAF50"));
                strength2.setBackgroundColor(Color.parseColor("#4CAF50"));
                strength3.setBackgroundColor(Color.parseColor("#4CAF50"));
                tvStrength.setText("密码强度：强");
                break;
            default:
                tvStrength.setText("密码强度：弱");
                break;
        }
    }

    private int calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        boolean hasDigit = false;
        boolean hasLetter = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isLetter(c)) {
                hasLetter = true;
            } else {
                hasSpecial = true;
            }
        }

        if (hasDigit && hasLetter && hasSpecial) {
            return 3;
        } else if ((hasDigit && hasLetter) || (hasDigit && hasSpecial) || (hasLetter && hasSpecial)) {
            return 2;
        } else {
            return 1;
        }
    }

    private void submitPassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // 验证当前密码
        if (currentPassword.isEmpty()) {
            Toast.makeText(this, "请输入当前密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证新密码
        if (newPassword.isEmpty()) {
            Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6 || newPassword.length() > 20) {
            Toast.makeText(this, "密码长度需要6-20个字符", Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证确认密码
        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "请确认新密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证当前密码是否正确
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String username = sp.getString("username", "");
        com.example.bawangchakun.model.User user = dbHelper.loginUser(username, currentPassword);

        if (user == null) {
            Toast.makeText(this, "当前密码不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        // 更新密码
        boolean success = dbHelper.updatePassword(userId, newPassword);

        if (success) {
            Toast.makeText(this, "密码修改成功，请重新登录", Toast.LENGTH_LONG).show();

            // 清除登录状态
            sp.edit().clear().apply();

            // 跳转到登录页
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "操作失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
}
