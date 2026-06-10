package com.example.bawangchakun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AvatarHelper {
    private static final String AVATAR_DIR = "avatars";
    private static final int AVATAR_SIZE = 200;

    // 预设头像资源 ID 映射
    private static final int[] PRESET_AVATARS = {
            R.drawable.preset_avatar_1,
            R.drawable.preset_avatar_2,
            R.drawable.preset_avatar_3,
            R.drawable.preset_avatar_4,
            R.drawable.preset_avatar_5,
            R.drawable.preset_avatar_6,
            R.drawable.preset_avatar_7,
            R.drawable.preset_avatar_8
    };

    /**
     * 获取预设头像资源 ID
     * @param index 预设头像索引 (0-7)
     * @return 资源 ID
     */
    public static int getPresetAvatarResId(int index) {
        if (index >= 0 && index < PRESET_AVATARS.length) {
            return PRESET_AVATARS[index];
        }
        return PRESET_AVATARS[0]; // 默认返回第一个
    }

    /**
     * 获取预设头像标识符
     * @param index 预设头像索引 (0-7)
     * @return 标识符字符串
     */
    public static String getPresetAvatarIdentifier(int index) {
        return "preset_" + (index + 1);
    }

    /**
     * 从标识符获取预设头像索引
     * @param identifier 标识符字符串
     * @return 索引，如果不是预设头像返回 -1
     */
    public static int getPresetIndexFromIdentifier(String identifier) {
        if (identifier != null && identifier.startsWith("preset_")) {
            try {
                int index = Integer.parseInt(identifier.substring(7)) - 1;
                if (index >= 0 && index < PRESET_AVATARS.length) {
                    return index;
                }
            } catch (NumberFormatException e) {
                // 忽略
            }
        }
        return -1;
    }

    /**
     * 加载头像到 ImageView
     * @param context 上下文
     * @param avatar 头像标识符或路径
     * @param imageView 目标 ImageView
     */
    public static void loadAvatar(Context context, String avatar, ImageView imageView) {
        if (avatar == null || avatar.isEmpty()) {
            imageView.setImageResource(R.drawable.avatar_bg);
            return;
        }

        int presetIndex = getPresetIndexFromIdentifier(avatar);
        if (presetIndex >= 0) {
            // 预设头像
            imageView.setImageResource(PRESET_AVATARS[presetIndex]);
        } else {
            // 文件路径头像
            File file = new File(avatar);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(avatar);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.avatar_bg);
                }
            } else {
                imageView.setImageResource(R.drawable.avatar_bg);
            }
        }
    }

    /**
     * 从 Uri 加载图片并保存为头像
     * @param context 上下文
     * @param uri 图片 Uri
     * @return 保存后的文件路径，失败返回 null
     */
    public static String saveAvatarFromUri(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap == null) return null;

            // 裁剪为正方形
            bitmap = cropToSquare(bitmap);

            // 缩放到指定大小
            bitmap = Bitmap.createScaledBitmap(bitmap, AVATAR_SIZE, AVATAR_SIZE, true);

            // 保存到私有目录
            File avatarDir = new File(context.getFilesDir(), AVATAR_DIR);
            if (!avatarDir.exists()) {
                avatarDir.mkdirs();
            }

            String fileName = "avatar_" + System.currentTimeMillis() + ".png";
            File file = new File(avatarDir, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 裁剪 Bitmap 为正方形
     */
    public static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = Math.min(width, height);

        int x = (width - size) / 2;
        int y = (height - size) / 2;

        return Bitmap.createBitmap(bitmap, x, y, size, size);
    }

    /**
     * 创建圆形 Bitmap
     */
    public static Bitmap createCircleBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, size, size);

        paint.setAntiAlias(true);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
