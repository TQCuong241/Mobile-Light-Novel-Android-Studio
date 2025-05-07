package com.example.apptruyenchu.lichSu;

public class LS {
    private String tenTruyen;
    private String chuongId;
    private String imgUrl;
    private String slView;
    private String slStar;
    private String tacGia;
    private String truyenID;
    private String userID;
    private Long lastReadTime;

    public LS(String tenTruyen, String chuongId, String imgUrl, String slView, String slStar, String tacGia, String truyenID, String userID, Long lastReadTime) {
        this.tenTruyen = tenTruyen;
        this.chuongId = chuongId;
        this.imgUrl = imgUrl;
        this.slView = slView;
        this.slStar = slStar;
        this.tacGia = tacGia;
        this.truyenID = truyenID;
        this.userID = userID;
        this.lastReadTime = lastReadTime;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }

    public String getChuongId() {
        return chuongId;
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

    public String getTruyenID() {
        return truyenID;
    }

    public String getUserID() {
        return userID;
    }

    public Long getLastReadTime() {
        return lastReadTime;
    }
}
