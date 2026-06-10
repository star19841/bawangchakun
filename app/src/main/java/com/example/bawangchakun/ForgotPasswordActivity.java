package com.example.bawangchakun;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bawangchakun.R;
import com.example.bawangchakun.db.DatabaseHelper;

public class ForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditText etUsername = findViewById(R.id.et_forgot_username);
        EditText etPhone = findViewById(R.id.et_forgot_phone);
        EditText etNewPassword = findViewById(R.id.et_forgot_new_password);
        Button btnReset = findViewById(R.id.btn_reset);

        btnReset.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();

            if (username.isEmpty() || phone.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper db = DatabaseHelper.getInstance(this);
            boolean success = db.resetPassword(username, phone, newPassword);
            if (success) {
                Toast.makeText(this, "密码重置成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "用户名或手机号不正确", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
