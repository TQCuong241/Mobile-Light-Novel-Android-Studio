package com.example.apptruyenchu.danhSachChuong;

public class DBChuong {
    public String Chuong_id;
    public String Data_Chuong;
    public String TenChuong;

    public DBChuong() {
        // Cần thiết cho Firebase
    }

    public DBChuong(String chuong_id, String data_Chuong, String tenChuong) {
        this.Chuong_id = chuong_id;
        this.Data_Chuong = data_Chuong;
        this.TenChuong = tenChuong;
    }
}
