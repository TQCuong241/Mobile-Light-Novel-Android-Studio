package com.example.apptruyenchu.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.widget.SearchView;

import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptruyenchu.R;
import com.example.apptruyenchu.chuong.MainMota;
import com.example.apptruyenchu.toastSetting.CustomToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchTruyen extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private TruyenAdapter truyenAdapter;
    private List<Truyen> truyenList;
    private DatabaseReference databaseRef;
    private CardView cardView;
    private ImageButton btnBack;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_truyen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        khai_bao();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTruyen(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchTruyen(newText);
                return false;
            }
        });
    }

    private void khai_bao(){
        databaseRef = FirebaseDatabase.getInstance().getReference("DataTruyen");
        searchView = findViewById(R.id.search_view);
        cardView = findViewById(R.id.card_view);
        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recycler_view);
        cardView.setOnClickListener(v -> searchView.setIconified(false));
        btnBack.setOnClickListener(v -> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        truyenList = new ArrayList<>();

        truyenAdapter = new TruyenAdapter(truyenList, truyen -> {
            Intent intent_kethua = new Intent(SearchTruyen.this, MainMota.class);
            intent_kethua.putExtra("truyen_id", truyen.getId());
            intent_kethua.putExtra("user_id", userId);
            startActivity(intent_kethua);
        });

        recyclerView.setAdapter(truyenAdapter);
        userId = getIntent().getStringExtra("user_id");
    }


    private void searchTruyen(String searchText) {
        String searchLower = removeDiacritics(searchText.toLowerCase()); // Loại bỏ dấu và chuyển sang chữ thường
        if (searchText.isEmpty()) {
            truyenList.clear();
            truyenAdapter.notifyDataSetChanged();
            return;
        }
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                truyenList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String tenTruyen = snapshot.child("TenTruyen").getValue(String.class);

                    if (tenTruyen != null) {
                        // Loại bỏ dấu và chuyển tên truyện sang chữ thường
                        String tenTruyenKhongDau = removeDiacritics(tenTruyen.toLowerCase());

                        // Kiểm tra xem tên truyện có chứa chuỗi tìm kiếm
                        if (tenTruyenKhongDau.contains(searchLower)) {
                            String imgUrlTruyen = snapshot.child("Img_Url_Truyen").getValue(String.class);
                            Double tbSoSao = snapshot.child("TBSoSao").getValue(Double.class);
                            Integer viewAll = snapshot.child("ViewAll").getValue(Integer.class);
                            String tacGia = snapshot.child("TacGiaID").getValue(String.class);
                            String truyenId = snapshot.getKey(); // Lấy ID của truyện từ Firebase

                            // Kiểm tra và tạo đối tượng Truyen
                            Truyen truyen = new Truyen();
                            truyen.setId(truyenId); // Gán ID truyện thay vì userId
                            truyen.setTenTruyen(tenTruyen);
                            truyen.setImgUrlTruyen(imgUrlTruyen);
                            truyen.setTbSoSao(tbSoSao);
                            truyen.setViewAll(viewAll);
                            truyen.setTacGia(tacGia);

                            truyenList.add(truyen);
                        }
                    }
                }

                truyenAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SearchError", "Lỗi tìm kiếm: " + databaseError.getMessage());
            }
        });
    }

    public String removeDiacritics(String input) {
        if (input == null) return null;
        return input
                // Các ký tự thường
                .replaceAll("[àáảãạâầấẩậ]", "a")
                .replaceAll("[èéẻẽẹêềếểệ]", "e")
                .replaceAll("[ìíỉĩị]", "i")
                .replaceAll("[òóỏõọôồốổộơờớởợ]", "o")
                .replaceAll("[ùúủũụûừứửự]", "u")
                .replaceAll("[ỳýỷỹỵ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[ÀÁẢÃẠÂẦẤẨẬ]", "A")
                .replaceAll("[ÈÉẺẼẸÊỀẾỂỆ]", "E")
                .replaceAll("[ÌÍỈĨỊ]", "I")
                .replaceAll("[ÒÓỎÕỌÔỒỐỔỘƠỜỚỞỢ]", "O")
                .replaceAll("[ÙÚỦŨỤÛỪỨỬỰ]", "U")
                .replaceAll("[Ý]", "Y")
                .replaceAll("[Đ]", "D");
    }

}
