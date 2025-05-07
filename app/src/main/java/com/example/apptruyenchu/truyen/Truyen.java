package com.example.apptruyenchu.truyen;

public class Truyen {
    private String truyenID;
    private String tenTruyen;
    private String imgUrl;
    private String slView;
    private String slStar;
    private String tacGia;
    private String update_truyen_now;

    // Constructor
    public Truyen(String truyenID, String tenTruyen, String imgUrl, String slView, String slStar, String tacGia, String update_truyen_now) {
        this.truyenID = truyenID;
        this.tenTruyen = tenTruyen;
        this.imgUrl = imgUrl;
        this.slView = slView;
        this.slStar = slStar;
        this.tacGia = tacGia;
        this.update_truyen_now = update_truyen_now;
    }

    // Getters
    public String getTruyenID() {
        return truyenID;
    }

    public String getViewAll() {
        return slView;
    }

    public String getTacGia() {
        return tacGia;
    }

    public String getStar() {
        return slStar;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getUpdate_truyen_now() {
        return update_truyen_now;
    }
}
