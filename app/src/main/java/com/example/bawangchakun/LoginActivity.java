package com.example.bawangchakun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bawangchakun.db.DatabaseHelper;
import com.example.bawangchakun.model.User;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private CheckBox cbRemember;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = DatabaseHelper.getInstance(this);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.cb_remember);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvForgot = findViewById(R.id.tv_forgot);
        TextView tvRegister = findViewById(R.id.tv_register);

        SharedPreferences sp = getSharedPreferences("login_prefs", MODE_PRIVATE);
        boolean remembered = sp.getBoolean("remember", false);
        if (remembered) {
            etUsername.setText(sp.getString("username", ""));
            etPassword.setText(sp.getString("password", ""));
            cbRemember.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> login());
        tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = dbHelper.loginUser(username, password);
        if (user != null) {
            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            sp.edit().putLong("user_id", user.getId())
                    .putString("username", user.getUsername())
                    .putString("phone", user.getPhone())
                    .apply();

            SharedPreferences loginPrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
            if (cbRemember.isChecked()) {
                loginPrefs.edit().putBoolean("remember", true)
                        .putString("username", username)
                        .putString("password", password)
                        .apply();
            } else {
                loginPrefs.edit().clear().apply();
            }

            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
    }
}
