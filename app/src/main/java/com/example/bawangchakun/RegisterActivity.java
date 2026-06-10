package com.example.bawangchakun;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bawangchakun.R;
import com.example.bawangchakun.db.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPhone, etPassword, etPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.et_reg_username);
        etPhone = findViewById(R.id.et_reg_phone);
        etPassword = findViewById(R.id.et_reg_password);
        etPassword2 = findViewById(R.id.et_reg_password2);
        Button btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String password2 = etPassword2.getText().toString().trim();

        if (username.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(password2)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        long result = db.registerUser(username, password, phone);
        if (result > 0) {
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
        }
    }
}
