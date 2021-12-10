package com.example.memory_loss_app.userpage.home;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class HomePagePopulate {
    private String keyword;
    private String value;
    private int icon;

    public HomePagePopulate(String keyword, String value, int icon) {
        this.keyword = keyword;
        this.value = value;
        this.icon = icon;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getValue() {
        return value;
    }

    public int getIcon() {
        return icon;
    }
}
