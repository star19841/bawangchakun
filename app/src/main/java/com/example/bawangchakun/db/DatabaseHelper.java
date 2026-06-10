package com.example.bawangchakun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bawangchakun.model.*;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "bawangchakun.db";
    private static final int DB_VERSION = 6;
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "phone TEXT," +
                "avatar TEXT)");

        db.execSQL("CREATE TABLE category (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "sort_order INTEGER)");

        db.execSQL("CREATE TABLE product (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "description TEXT," +
                "price REAL," +
                "image TEXT," +
                "category_id INTEGER)");

        db.execSQL("CREATE TABLE cart_item (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "product_id INTEGER," +
                "sweetness TEXT," +
                "ice TEXT," +
                "cup_size TEXT," +
                "quantity INTEGER," +
                "price REAL)");

        db.execSQL("CREATE TABLE `order` (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "total_price REAL," +
                "create_time TEXT," +
                "status TEXT)");

        db.execSQL("CREATE TABLE order_item (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id INTEGER," +
                "product_id INTEGER," +
                "sweetness TEXT," +
                "ice TEXT," +
                "cup_size TEXT," +
                "quantity INTEGER," +
                "price REAL)");

        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE cart_item ADD COLUMN price REAL");
        }
    }

    private void insertInitialData(SQLiteDatabase db) {
        String[] categories = {"原叶鲜奶茶", "拿铁", "活力轻果茶", "原叶纯茶", "远山鲜沏篇"};
        for (int i = 0; i < categories.length; i++) {
            ContentValues cv = new ContentValues();
            cv.put("name", categories[i]);
            cv.put("sort_order", i);
            db.insert("category", null, cv);
        }

        insertProduct(db, "伯牙绝弦", "茉莉雪芽鲜奶茶，东方经典", 16.0, 1, "boya_juexian");
        insertProduct(db, "桂馥兰香", "桂花乌龙鲜奶茶", 18.0, 1, "guifu_lanxiang");
        insertProduct(db, "花田乌龙", "蜜桃乌龙鲜奶茶", 18.0, 1, "huatian_wulong");
        insertProduct(db, "一抹山月", "山月鲜奶茶", 16.0, 1, "yimo_shanyue");
        insertProduct(db, "月抹静山", "静山鲜奶茶", 20.0, 1, "yuemo_jingshan");
        insertProduct(db, "云漫普洱", "普洱鲜奶茶", 19.0, 1, "yunman_puer");
        insertProduct(db, "青青糯山", "糯香鲜奶茶", 17.0, 1, "qingqing_nuoshan");
        insertProduct(db, "茉莉奶绿", "茉莉花茶鲜奶茶", 15.0, 1, "moli_nailv");

        insertProduct(db, "拿铁", "经典拿铁", 16.0, 2, "latte");
        insertProduct(db, "香草拿铁", "香草风味拿铁", 18.0, 2, "vanilla_latte");
        insertProduct(db, "黑糖拿铁", "黑糖风味拿铁", 18.0, 2, "brown_sugar_latte");
        insertProduct(db, "丝绒拿铁", "丝绒口感拿铁", 20.0, 2, "velvet_latte");
        insertProduct(db, "陨石拿铁", "陨石风味拿铁", 22.0, 2, "meteor_latte");
        insertProduct(db, "抹茶拿铁", "日式抹茶拿铁", 20.0, 2, "matcha_latte");
        insertProduct(db, "燕麦拿铁", "燕麦风味拿铁", 19.0, 2, "oat_latte");

        insertProduct(db, "琥珀光", "琥珀风味果茶", 15.0, 3, "amber_light");
        insertProduct(db, "橙香四季", "鲜橙四季春果茶", 16.0, 3, "orange_season");
        insertProduct(db, "千峰翠", "翠绿茶香果茶", 14.0, 3, "qianfeng_cui");
        insertProduct(db, "七里香", "七里香果茶", 15.0, 3, "qilixiang");
        insertProduct(db, "芋圆葡萄", "芋圆配葡萄", 16.0, 3, "taro_grape");
        insertProduct(db, "蜜桃四季香", "蜜桃四季春果茶", 17.0, 3, "peach_season");
        insertProduct(db, "满杯百香果", "满杯百香果", 16.0, 3, "full_passion");

        insertProduct(db, "春日龙井", "春日龙井绿茶", 16.0, 4, "spring_longjing");
        insertProduct(db, "茉莉绿茶", "经典茉莉花茶", 12.0, 4, "jasmine_green");
        insertProduct(db, "高山四季春茶", "高山四季春茶", 14.0, 4, "highland_spring");

        insertProduct(db, "归云南", "云南普洱茶", 18.0, 5, "yunnan");
        insertProduct(db, "醒春山", "春山鲜沏茶", 20.0, 5, "spring_mountain");
        insertProduct(db, "酌红袍", "大红袍鲜沏", 22.0, 5, "dahongpao");
        insertProduct(db, "花田坞", "花香鲜沏茶", 24.0, 5, "flower_field");
        insertProduct(db, "云中绿", "云雾绿茶", 20.0, 5, "cloud_green");
        insertProduct(db, "折桂令", "桂花鲜沏茶", 22.0, 5, "osmanthus");
        insertProduct(db, "木兰辞", "木兰花香茶", 22.0, 5, "magnolia");
    }

    private void insertProduct(SQLiteDatabase db, String name, String desc, double price, long categoryId, String image) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("description", desc);
        cv.put("price", price);
        cv.put("image", image);
        cv.put("category_id", categoryId);
        db.insert("product", null, cv);
    }

    public long registerUser(String username, String password, String phone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        cv.put("phone", phone);
        return db.insert("user", null, cv);
    }

    public User loginUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM user WHERE username=? AND password=?",
                new String[]{username, password});
        User user = null;
        if (c.moveToFirst()) {
            user = new User();
            user.setId(c.getLong(c.getColumnIndexOrThrow("id")));
            user.setUsername(c.getString(c.getColumnIndexOrThrow("username")));
            user.setPassword(c.getString(c.getColumnIndexOrThrow("password")));
            user.setPhone(c.getString(c.getColumnIndexOrThrow("phone")));
            user.setAvatar(c.getString(c.getColumnIndexOrThrow("avatar")));
        }
        c.close();
        return user;
    }

    public boolean resetPassword(String username, String phone, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);
        int rows = db.update("user", cv, "username=? AND phone=?",
                new String[]{username, phone});
        return rows > 0;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", user.getUsername());
        cv.put("phone", user.getPhone());
        if (user.getAvatar() != null) {
            cv.put("avatar", user.getAvatar());
        }
        int rows = db.update("user", cv, "id=?", new String[]{String.valueOf(user.getId())});
        return rows > 0;
    }

    public User getUserById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM user WHERE id=?", new String[]{String.valueOf(id)});
        User user = null;
        if (c.moveToFirst()) {
            user = new User();
            user.setId(c.getLong(c.getColumnIndexOrThrow("id")));
            user.setUsername(c.getString(c.getColumnIndexOrThrow("username")));
            user.setPassword(c.getString(c.getColumnIndexOrThrow("password")));
            user.setPhone(c.getString(c.getColumnIndexOrThrow("phone")));
            user.setAvatar(c.getString(c.getColumnIndexOrThrow("avatar")));
        }
        c.close();
        return user;
    }

    public boolean updatePassword(long userId, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);
        int rows = db.update("user", cv, "id=?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    public boolean updateAvatar(long userId, String avatar) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("avatar", avatar);
            String[] whereArgs = new String[]{String.valueOf(userId)};
            Log.d("DatabaseHelper", "updateAvatar: userId=" + userId + ", avatar=" + avatar);
            int rows = db.update("user", cv, "id=?", whereArgs);
            Log.d("DatabaseHelper", "updateAvatar: 更新了 " + rows + " 行");
            return rows > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "updateAvatar 异常", e);
            return false;
        }
    }

    public boolean updateUsername(long userId, String newUsername) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("username", newUsername);
            String[] whereArgs = new String[]{String.valueOf(userId)};
            Log.d("DatabaseHelper", "updateUsername: userId=" + userId + ", newUsername=" + newUsername);
            int rows = db.update("user", cv, "id=?", whereArgs);
            Log.d("DatabaseHelper", "updateUsername: 更新了 " + rows + " 行");
            return rows > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "updateUsername 异常", e);
            return false;
        }
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM category ORDER BY sort_order", null);
        while (c.moveToNext()) {
            Category cat = new Category();
            cat.setId(c.getLong(c.getColumnIndexOrThrow("id")));
            cat.setName(c.getString(c.getColumnIndexOrThrow("name")));
            cat.setSortOrder(c.getInt(c.getColumnIndexOrThrow("sort_order")));
            list.add(cat);
        }
        c.close();
        return list;
    }

    public List<Product> getProductsByCategory(long categoryId) {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        if (categoryId == -1) {
            c = db.rawQuery("SELECT * FROM product", null);
        } else {
            c = db.rawQuery("SELECT * FROM product WHERE category_id=?",
                    new String[]{String.valueOf(categoryId)});
        }
        while (c.moveToNext()) {
            Product p = new Product();
            p.setId(c.getLong(c.getColumnIndexOrThrow("id")));
            p.setName(c.getString(c.getColumnIndexOrThrow("name")));
            p.setDescription(c.getString(c.getColumnIndexOrThrow("description")));
            p.setPrice(c.getDouble(c.getColumnIndexOrThrow("price")));
            p.setCategoryId(c.getLong(c.getColumnIndexOrThrow("category_id")));
            p.setImage(c.getString(c.getColumnIndexOrThrow("image")));
            list.add(p);
        }
        c.close();
        return list;
    }

    public Product getProductById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM product WHERE id=?", new String[]{String.valueOf(id)});
        Product p = null;
        if (c.moveToFirst()) {
            p = new Product();
            p.setId(c.getLong(c.getColumnIndexOrThrow("id")));
            p.setName(c.getString(c.getColumnIndexOrThrow("name")));
            p.setDescription(c.getString(c.getColumnIndexOrThrow("description")));
            p.setPrice(c.getDouble(c.getColumnIndexOrThrow("price")));
            p.setCategoryId(c.getLong(c.getColumnIndexOrThrow("category_id")));
            p.setImage(c.getString(c.getColumnIndexOrThrow("image")));
        }
        c.close();
        return p;
    }

    public long addToCart(long userId, long productId, String sweetness, String ice, String cupSize, int quantity, double price) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT id, quantity FROM cart_item WHERE user_id=? AND product_id=? AND sweetness=? AND ice=? AND cup_size=?",
                new String[]{String.valueOf(userId), String.valueOf(productId), sweetness, ice, cupSize});
        if (c.moveToFirst()) {
            long existId = c.getLong(c.getColumnIndexOrThrow("id"));
            int existQty = c.getInt(c.getColumnIndexOrThrow("quantity"));
            c.close();
            ContentValues cv = new ContentValues();
            cv.put("quantity", existQty + quantity);
            db.update("cart_item", cv, "id=?", new String[]{String.valueOf(existId)});
            return existId;
        }
        c.close();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("product_id", productId);
        cv.put("sweetness", sweetness);
        cv.put("ice", ice);
        cv.put("cup_size", cupSize);
        cv.put("quantity", quantity);
        cv.put("price", price);
        return db.insert("cart_item", null, cv);
    }

    public List<CartItem> getCartItems(long userId) {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT c.*, p.name as product_name, COALESCE(c.price, p.price) as product_price " +
                "FROM cart_item c JOIN product p ON c.product_id = p.id " +
                "WHERE c.user_id=?", new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            CartItem item = new CartItem();
            item.setId(c.getLong(c.getColumnIndexOrThrow("id")));
            item.setUserId(c.getLong(c.getColumnIndexOrThrow("user_id")));
            item.setProductId(c.getLong(c.getColumnIndexOrThrow("product_id")));
            item.setProductName(c.getString(c.getColumnIndexOrThrow("product_name")));
            item.setProductPrice(c.getDouble(c.getColumnIndexOrThrow("product_price")));
            item.setSweetness(c.getString(c.getColumnIndexOrThrow("sweetness")));
            item.setIce(c.getString(c.getColumnIndexOrThrow("ice")));
            item.setCupSize(c.getString(c.getColumnIndexOrThrow("cup_size")));
            item.setQuantity(c.getInt(c.getColumnIndexOrThrow("quantity")));
            list.add(item);
        }
        c.close();
        return list;
    }

    public void updateCartItemQuantity(long cartItemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        if (quantity <= 0) {
            db.delete("cart_item", "id=?", new String[]{String.valueOf(cartItemId)});
        } else {
            ContentValues cv = new ContentValues();
            cv.put("quantity", quantity);
            db.update("cart_item", cv, "id=?", new String[]{String.valueOf(cartItemId)});
        }
    }

    public void deleteCartItem(long cartItemId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("cart_item", "id=?", new String[]{String.valueOf(cartItemId)});
    }

    public void clearCart(long userId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("cart_item", "user_id=?", new String[]{String.valueOf(userId)});
    }

    public long createOrder(long userId, List<CartItem> items) {
        SQLiteDatabase db = getWritableDatabase();
        double total = 0;
        for (CartItem item : items) {
            total += item.getProductPrice() * item.getQuantity();
        }

        ContentValues orderCv = new ContentValues();
        orderCv.put("user_id", userId);
        orderCv.put("total_price", total);
        orderCv.put("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        orderCv.put("status", "待取餐");
        long orderId = db.insert("`order`", null, orderCv);

        for (CartItem item : items) {
            ContentValues itemCv = new ContentValues();
            itemCv.put("order_id", orderId);
            itemCv.put("product_id", item.getProductId());
            itemCv.put("sweetness", item.getSweetness());
            itemCv.put("ice", item.getIce());
            itemCv.put("cup_size", item.getCupSize());
            itemCv.put("quantity", item.getQuantity());
            itemCv.put("price", item.getProductPrice());
            db.insert("order_item", null, itemCv);
        }

        clearCart(userId);
        return orderId;
    }

    public List<Order> getOrders(long userId) {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM `order` WHERE user_id=? ORDER BY id DESC",
                new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            Order order = new Order();
            order.setId(c.getLong(c.getColumnIndexOrThrow("id")));
            order.setUserId(c.getLong(c.getColumnIndexOrThrow("user_id")));
            order.setTotalPrice(c.getDouble(c.getColumnIndexOrThrow("total_price")));
            order.setCreateTime(c.getString(c.getColumnIndexOrThrow("create_time")));
            order.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
            list.add(order);
        }
        c.close();
        return list;
    }

    public List<OrderItem> getOrderItems(long orderId) {
        List<OrderItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT oi.*, p.name as product_name " +
                "FROM order_item oi JOIN product p ON oi.product_id = p.id " +
                "WHERE oi.order_id=?", new String[]{String.valueOf(orderId)});
        while (c.moveToNext()) {
            OrderItem item = new OrderItem();
            item.setId(c.getLong(c.getColumnIndexOrThrow("id")));
            item.setOrderId(c.getLong(c.getColumnIndexOrThrow("order_id")));
            item.setProductId(c.getLong(c.getColumnIndexOrThrow("product_id")));
            item.setProductName(c.getString(c.getColumnIndexOrThrow("product_name")));
            item.setSweetness(c.getString(c.getColumnIndexOrThrow("sweetness")));
            item.setIce(c.getString(c.getColumnIndexOrThrow("ice")));
            item.setCupSize(c.getString(c.getColumnIndexOrThrow("cup_size")));
            item.setQuantity(c.getInt(c.getColumnIndexOrThrow("quantity")));
            item.setPrice(c.getDouble(c.getColumnIndexOrThrow("price")));
            list.add(item);
        }
        c.close();
        return list;
    }
}
