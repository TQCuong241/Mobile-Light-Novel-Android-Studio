package com.example.apptruyenchu.comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommentItem {
    private String ND_Comment;
    private String Update_Comment; // Thời gian cập nhật
    private String UserName;
    private String userId;
    private String imgUser; // Thêm trường imgUser
    private String currentUserId;

    public CommentItem() {
    }

    public CommentItem(String ND_Comment, String Update_Comment, String UserName, String userId, String imgUser, String currentUserId) {
        this.ND_Comment = ND_Comment;
        this.Update_Comment = Update_Comment;
        this.UserName = UserName;
        this.userId = userId;
        this.imgUser = imgUser;
        this.currentUserId = currentUserId;
    }

    public String getND_Comment() {
        return ND_Comment;
    }

    public String getUpdate_Comment() {
        return Update_Comment;
    }

    public String getUserName() {
        return UserName;
    }

    public String getUserId() {
        return userId;
    }

    public String getImgUser() {
        return imgUser;
    }

    public boolean isCurrentUserComment() {
        return this.userId.equals(currentUserId);
    }

    public String getRelativeTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

        if (Update_Comment == null || Update_Comment.isEmpty()) {
            return "Không xác định";
        }

        try {
            Date commentDate = sdf.parse(Update_Comment);
            long difference = new Date().getTime() - commentDate.getTime();
            long minutes = difference / (1000 * 60);
            long hours = difference / (1000 * 60 * 60);
            long days = difference / (1000 * 60 * 60 * 24);

            if (minutes < 1) {
                return "Vừa xong";
            } else if (minutes < 60) {
                return minutes + " phút trước";
            } else if (hours < 24) {
                return hours + " giờ trước";
            } else {
                return days + " ngày trước";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return Update_Comment;
        }
    }
}
