package com.example.bawangchakun;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bawangchakun.db.DatabaseHelper;
import com.example.bawangchakun.model.User;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int REQUEST_PICK_IMAGE = 1001;
    private static final int REQUEST_PERMISSION_READ_STORAGE = 1002;

    private ImageView ivAvatar;
    private TextView tvUsername, tvPhone;
    private DatabaseHelper dbHelper;
    private SharedPreferences sp;
    private long userId;
    private String currentAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = DatabaseHelper.getInstance(this);
        sp = getSharedPreferences("user", MODE_PRIVATE);
        userId = sp.getLong("user_id", -1);

        initViews();
        loadUserInfo();
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.iv_avatar);
        tvUsername = findViewById(R.id.tv_username);
        tvPhone = findViewById(R.id.tv_phone);

        // 设置头像圆形裁剪
        ivAvatar.setClipToOutline(true);
        ivAvatar.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });

        // 返回按钮
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> finish());

        // 头像点击
        ivAvatar.setOnClickListener(v -> showAvatarDialog());
        findViewById(R.id.iv_avatar_edit).setOnClickListener(v -> showAvatarDialog());

        // 昵称点击
        findViewById(R.id.layout_nickname).setOnClickListener(v -> showNicknameDialog());

        // 菜单项
        findViewById(R.id.menu_orders).setOnClickListener(v ->
                startActivity(new Intent(this, OrderListActivity.class)));

        findViewById(R.id.menu_nickname).setOnClickListener(v -> showNicknameDialog());

        findViewById(R.id.menu_avatar).setOnClickListener(v -> showAvatarDialog());

        findViewById(R.id.menu_password).setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class)));

        findViewById(R.id.menu_logout).setOnClickListener(v -> showLogoutDialog());
    }

    private void loadUserInfo() {
        User user = dbHelper.getUserById(userId);
        if (user != null) {
            tvUsername.setText(user.getUsername());
            currentAvatar = user.getAvatar();

            // 脱敏显示手机号
            String phone = user.getPhone();
            if (phone != null && phone.length() >= 11) {
                tvPhone.setText("📱 " + phone.substring(0, 3) + "****" + phone.substring(7));
                tvPhone.setVisibility(View.VISIBLE);
            } else {
                tvPhone.setVisibility(View.GONE);
            }

            // 加载头像
            AvatarHelper.loadAvatar(this, currentAvatar, ivAvatar);
        }
    }

    private void showNicknameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改昵称");

        // 创建输入框
        EditText input = new EditText(this);
        input.setHint("请输入新昵称");
        input.setMaxLines(1);
        input.setText(tvUsername.getText().toString());
        input.setSelection(input.getText().length());

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(48, 24, 48, 0);
        container.addView(input);

        // 字数统计
        TextView tvCount = new TextView(this);
        tvCount.setTextSize(12);
        tvCount.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        tvCount.setGravity(Gravity.END);
        tvCount.setPadding(0, 8, 0, 0);
        updateCharCount(tvCount, input.getText().length());
        container.addView(tvCount);

        input.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                updateCharCount(tvCount, s.length());
            }
        });

        builder.setView(container);

        builder.setPositiveButton("保存", (dialog, which) -> {
            String newNickname = input.getText().toString().trim();
            if (newNickname.isEmpty()) {
                Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newNickname.length() > 20) {
                Toast.makeText(this, "昵称不能超过20个字符", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "尝试修改昵称, userId=" + userId + ", newNickname=" + newNickname);
            if (userId <= 0) {
                Toast.makeText(this, "用户未登录，请重新登录", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                boolean success = dbHelper.updateUsername(userId, newNickname);
                Log.d(TAG, "updateUsername 结果: " + success);
                if (success) {
                    sp.edit().putString("username", newNickname).apply();
                    tvUsername.setText(newNickname);
                    Toast.makeText(this, "昵称修改成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "昵称修改失败，可能已被占用", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "修改昵称异常", e);
                Toast.makeText(this, "操作异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void updateCharCount(TextView tvCount, int length) {
        tvCount.setText(length + "/20");
    }

    private void showAvatarDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_avatar);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        // 预设头像网格
        GridLayout gridPresets = dialog.findViewById(R.id.grid_presets);
        setupPresetAvatars(gridPresets, dialog);

        // 相册选择
        dialog.findViewById(R.id.layout_gallery).setOnClickListener(v -> {
            dialog.dismiss();
            checkPermissionAndPickImage();
        });

        // 取消按钮
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void setupPresetAvatars(GridLayout gridLayout, Dialog dialog) {
        gridLayout.removeAllViews();

        // 计算每个头像的尺寸（4列，减去间距）
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (24 * getResources().getDisplayMetrics().density); // dialog padding
        int margin = (int) (8 * getResources().getDisplayMetrics().density);
        int avatarSize = (screenWidth - padding * 2 - margin * 2 * 4) / 4;

        for (int i = 0; i < 8; i++) {
            int index = i;
            ImageView imageView = new ImageView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = avatarSize;
            params.height = avatarSize;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(margin, margin, margin, margin);
            imageView.setLayoutParams(params);
            imageView.setImageResource(AvatarHelper.getPresetAvatarResId(i));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // 圆形裁剪
            imageView.setClipToOutline(true);
            imageView.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });

            imageView.setOnClickListener(v -> {
                String identifier = AvatarHelper.getPresetAvatarIdentifier(index);
                saveAvatar(identifier);
                dialog.dismiss();
            });

            gridLayout.addView(imageView);
        }
    }

    private void checkPermissionAndPickImage() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 使用 READ_MEDIA_IMAGES
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            // Android 12 及以下使用 READ_EXTERNAL_STORAGE
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    REQUEST_PERMISSION_READ_STORAGE);
        } else {
            pickImageFromGallery();
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "需要相册权限才能选择头像", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                String savedPath = AvatarHelper.saveAvatarFromUri(this, imageUri);
                if (savedPath != null) {
                    saveAvatar(savedPath);
                } else {
                    Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveAvatar(String avatar) {
        Log.d(TAG, "尝试修改头像, userId=" + userId + ", avatar=" + avatar);
        if (userId <= 0) {
            Toast.makeText(this, "用户未登录，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            boolean success = dbHelper.updateAvatar(userId, avatar);
            Log.d(TAG, "updateAvatar 结果: " + success);
            if (success) {
                currentAvatar = avatar;
                AvatarHelper.loadAvatar(this, avatar, ivAvatar);
                Toast.makeText(this, "头像修改成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "头像修改失败，请重新登录后重试", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "修改头像异常", e);
            Toast.makeText(this, "操作异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    sp.edit().clear().apply();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
