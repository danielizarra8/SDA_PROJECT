package com.example.project_sda_2022;

public class SetChatItem {

    private String userName;
    private String reviewContent;

    public SetChatItem(String userName, String reviewContent) {
        this.userName = userName;
        this.reviewContent = reviewContent;
    }

    public String getName() {
        return userName;
    }

    public void setName(String userName) {
        this.userName = userName;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

}
