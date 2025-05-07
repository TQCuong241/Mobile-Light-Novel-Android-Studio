package com.example.apptruyenchu.search;

public class Truyen {
    private String userId;
    private String tenTruyen;
    private String imgUrlTruyen;
    private double tbSoSao;
    private int viewAll;
    private String tacGia;

    public Truyen() {
    }

    public String getId() {
        return userId;
    }

    public void setId(String userId) {
        this.userId = userId;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }

    public void setTenTruyen(String tenTruyen) {
        this.tenTruyen = tenTruyen;
    }

    public String getImgUrlTruyen() {
        return imgUrlTruyen;
    }

    public void setImgUrlTruyen(String imgUrlTruyen) {
        this.imgUrlTruyen = imgUrlTruyen;
    }

    public double getTbSoSao() {
        return tbSoSao;
    }

    public void setTbSoSao(double tbSoSao) {
        this.tbSoSao = tbSoSao;
    }

    public int getViewAll() {
        return viewAll;
    }

    public void setViewAll(int viewAll) {
        this.viewAll = viewAll;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public String getTacGia() {
        return tacGia;
    }
}
