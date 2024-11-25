package com.example.loginsample;

public class RoomInfo {
    private String title;
    private String description;
    private int imageUrl;

    public RoomInfo(String title, String description, int imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageUrl() {
        return imageUrl;
    }

}
