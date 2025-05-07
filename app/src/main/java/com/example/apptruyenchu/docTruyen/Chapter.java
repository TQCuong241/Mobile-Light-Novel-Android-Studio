package com.example.apptruyenchu.docTruyen;

public class Chapter {
    private String chuongID;
    private String tenChuong;
    private String truyenID;

    // Constructor
    public Chapter(String chuongID, String tenChuong, String truyenID) {
        this.chuongID = chuongID;
        this.tenChuong = tenChuong;
        this.truyenID = truyenID;
    }

    // Getter for chuongID
    public String getChuongID() {
        return chuongID;
    }

    // Getter for tenChuong
    public String getTenChuong() {
        return tenChuong;
    }

    // Getter for truyenID
    public String getTruyenID() {
        return truyenID;
    }
}
