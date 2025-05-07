package com.example.apptruyenchu.upTruyen;

public class Chuong {
    public String Chuong_id;
    public String Data_Chuong;
    public String TenChuong;

    public Chuong() {
        // Cần thiết cho Firebase
    }

    public Chuong(String chuong_id, String data_Chuong, String tenChuong) {
        this.Chuong_id = chuong_id;
        this.Data_Chuong = data_Chuong;
        this.TenChuong = tenChuong;
    }
}
