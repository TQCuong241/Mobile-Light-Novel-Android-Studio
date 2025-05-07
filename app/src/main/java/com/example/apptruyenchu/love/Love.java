package com.example.apptruyenchu.love;

public class Love {
    private String tenTruyen;
    private String imgUrl;
    private String slView;
    private String slStar;
    private String tacGia;
    private String truyenId;
    private String user_id;
    private Long lastReadTime;


    public Love() {
        // Constructor trống để Firebase sử dụng
    }

    public Love(String tenTruyen, String imgUrl, String slView, String slStar, String tacGia, String truyenId, String user_id, Long lastReadTime) {
        this.tenTruyen = tenTruyen;
        this.imgUrl = imgUrl;
        this.slView = slView;
        this.slStar = slStar;
        this.tacGia = tacGia;
        this.truyenId = truyenId;
        this.user_id = user_id;
        this.lastReadTime = lastReadTime;
    }

    public String getTruyenId() {
        return truyenId;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }
    public String getImgUrl() {
        return imgUrl;
    }
    public String getSlView() {
        return slView;
    }
    public String getSlStar() {
        return slStar;
    }
    public String getTacGia() {
        return tacGia;
    }
    public String getUser_id() {
        return user_id;
    }
    public Long getLastReadTime() {
        return lastReadTime;
    }
}
