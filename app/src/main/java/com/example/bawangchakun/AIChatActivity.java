package com.example.bawangchakun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bawangchakun.adapter.ChatAdapter;
import com.example.bawangchakun.db.DatabaseHelper;
import com.example.bawangchakun.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etInput;
    private TextView tvLoading;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private DeepSeekService aiService;
    private DatabaseHelper dbHelper;
    private List<Product> allProducts;

    private static final String SYSTEM_PROMPT = "你是霸王茶姬奶茶店的AI点单助手。你的职责是根据顾客的需求，推荐合适的饮品。\n\n"
            + "以下是我们的完整菜单：\n\n"
            + "【原叶鲜奶茶】\n"
            + "1. 伯牙绝弦 - 茉莉雪芽鲜奶茶，东方经典 - ¥16\n"
            + "2. 桂馥兰香 - 桂花乌龙鲜奶茶 - ¥18\n"
            + "3. 花田乌龙 - 蜜桃乌龙鲜奶茶 - ¥18\n"
            + "4. 一抹山月 - 山月鲜奶茶 - ¥16\n"
            + "5. 月抹静山 - 静山鲜奶茶 - ¥20\n"
            + "6. 云漫普洱 - 普洱鲜奶茶 - ¥19\n"
            + "7. 青青糯山 - 糯香鲜奶茶 - ¥17\n"
            + "8. 茉莉奶绿 - 茉莉花茶鲜奶茶 - ¥15\n\n"
            + "【拿铁】\n"
            + "9. 拿铁 - 经典拿铁 - ¥16\n"
            + "10. 香草拿铁 - 香草风味拿铁 - ¥18\n"
            + "11. 黑糖拿铁 - 黑糖风味拿铁 - ¥18\n"
            + "12. 丝绒拿铁 - 丝绒口感拿铁 - ¥20\n"
            + "13. 陨石拿铁 - 陨石风味拿铁 - ¥22\n"
            + "14. 抹茶拿铁 - 日式抹茶拿铁 - ¥20\n"
            + "15. 燕麦拿铁 - 燕麦风味拿铁 - ¥19\n\n"
            + "【活力轻果茶】\n"
            + "16. 琥珀光 - 琥珀风味果茶 - ¥15\n"
            + "17. 橙香四季 - 鲜橙四季春果茶 - ¥16\n"
            + "18. 千峰翠 - 翠绿茶香果茶 - ¥14\n"
            + "19. 七里香 - 七里香果茶 - ¥15\n"
            + "20. 芋圆葡萄 - 芋圆配葡萄 - ¥16\n"
            + "21. 蜜桃四季香 - 蜜桃四季春果茶 - ¥17\n"
            + "22. 满杯百香果 - 满杯百香果 - ¥16\n\n"
            + "【原叶纯茶】\n"
            + "23. 春日龙井 - 春日龙井绿茶 - ¥16\n"
            + "24. 茉莉绿茶 - 经典茉莉花茶 - ¥12\n"
            + "25. 高山四季春茶 - 高山四季春茶 - ¥14\n\n"
            + "【远山鲜沏篇】\n"
            + "26. 归云南 - 云南普洱茶 - ¥18\n"
            + "27. 醒春山 - 春山鲜沏茶 - ¥20\n"
            + "28. 酌红袍 - 大红袍鲜沏 - ¥22\n"
            + "29. 花田坞 - 花香鲜沏茶 - ¥24\n"
            + "30. 云中绿 - 云雾绿茶 - ¥20\n"
            + "31. 折桂令 - 桂花鲜沏茶 - ¥22\n"
            + "32. 木兰辞 - 木兰花香茶 - ¥22\n\n"
            + "杯型：大杯(标准价)、中杯(减3元)、小杯(减5元)\n"
            + "甜度：全糖、七分糖、半糖、三分糖、无糖\n"
            + "温度：正常冰、少冰、去冰、温、热\n\n"
            + "规则：\n"
            + "1. 只能推荐上面菜单中列出的饮品，绝对不能编造不存在的产品名称\n"
            + "2. 根据顾客的口味偏好、心情、场景推荐1-3款饮品\n"
            + "3. 简短介绍推荐理由\n"
            + "4. 在回复的最后，用特殊格式列出推荐的饮品名称，格式为：[推荐：饮品名称1,饮品名称2]\n"
            + "5. 如果顾客的问题与奶茶无关，礼貌地引导回点单话题\n"
            + "6. 回复要简洁友好，像一个热情的店员";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        dbHelper = DatabaseHelper.getInstance(this);
        allProducts = dbHelper.getProductsByCategory(-1);
        aiService = new DeepSeekService(BuildConfig.DEEPSEEK_API_KEY);

        rvChat = findViewById(R.id.rv_chat);
        etInput = findViewById(R.id.et_input);
        tvLoading = findViewById(R.id.tv_loading);
        TextView tvBack = findViewById(R.id.tv_back);
        TextView btnSend = findViewById(R.id.btn_send);

        chatAdapter = new ChatAdapter(messages, this);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(chatAdapter);

        // 欢迎消息
        addAiMessage("你好！我是霸王茶姬的 AI 点单助手 🍵\n告诉我你想喝什么样的茶，比如：\"想喝冰的甜的\"或者\"有没有果茶推荐\"，我来帮你选~", null);

        tvBack.setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> sendMessage());
        etInput.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String input = etInput.getText().toString().trim();
        if (input.isEmpty()) return;

        // 添加用户消息
        messages.add(new ChatMessage(ChatMessage.TYPE_USER, input));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
        etInput.setText("");

        // 显示加载
        tvLoading.setVisibility(View.VISIBLE);

        // 调用 AI
        new Thread(() -> {
            try {
                String response = aiService.chat(SYSTEM_PROMPT, input);
                List<Product> recommended = extractRecommendedProducts(response);

                runOnUiThread(() -> {
                    tvLoading.setVisibility(View.GONE);
                    addAiMessage(response, recommended);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvLoading.setVisibility(View.GONE);
                    addAiMessage("抱歉，网络出了点问题，请稍后再试~ 😅\n错误信息：" + e.getMessage(), null);
                });
            }
        }).start();
    }

    private void addAiMessage(String content, List<Product> recommended) {
        ChatMessage msg = new ChatMessage(ChatMessage.TYPE_AI, content);
        msg.setRecommendedProducts(recommended);
        messages.add(msg);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
    }

    private List<Product> extractRecommendedProducts(String aiResponse) {
        List<Product> recommended = new ArrayList<>();

        // 匹配 [推荐：xxx,xxx] 格式
        Pattern pattern = Pattern.compile("\\[推荐[：:]\\s*(.+?)\\]");
        Matcher matcher = pattern.matcher(aiResponse);
        if (matcher.find()) {
            String[] names = matcher.group(1).split(",");
            for (String name : names) {
                name = name.trim();
                for (Product p : allProducts) {
                    if (p.getName().equals(name)) {
                        recommended.add(p);
                        break;
                    }
                }
            }
        }

        // 如果没有匹配到格式，尝试模糊匹配所有产品名
        if (recommended.isEmpty()) {
            for (Product p : allProducts) {
                if (aiResponse.contains(p.getName())) {
                    recommended.add(p);
                }
            }
        }

        return recommended;
    }
}
