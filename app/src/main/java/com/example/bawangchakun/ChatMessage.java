package com.example.bawangchakun;

import com.example.bawangchakun.model.Product;
import java.util.List;

public class ChatMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_AI = 1;

    private int type;
    private String content;
    private List<Product> recommendedProducts;

    public ChatMessage(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public int getType() { return type; }
    public String getContent() { return content; }
    public List<Product> getRecommendedProducts() { return recommendedProducts; }
    public void setRecommendedProducts(List<Product> products) { this.recommendedProducts = products; }
}
