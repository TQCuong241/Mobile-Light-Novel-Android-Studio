package com.example.apptruyenchu.love;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.apptruyenchu.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DanhSachLove extends Fragment {
    ListView lvLove;
    DatabaseReference databaseReference;
    ArrayList<Love> loveList;
    String user_id;
    AdapterLove adapter;
    private int itemsAdded = 0; // Khai báo biến itemsAdded
    ImageView progressBar;
    TextView tvLoading;
    AnimationDrawable loadingAnimation;
    public static DanhSachLove newInstance(String userId) {
        DanhSachLove fragment = new DanhSachLove();
        Bundle args = new Bundle();
        args.putString("user_id", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_danh_sach_love, container, false);

        // Áp dụng EdgeToEdge (nếu cần)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khai báo các thành phần
        khai_bao(view);
        loadLoveData();

        return view;
    }
    private void khai_bao(View view) {
        lvLove = view.findViewById(R.id.lvLove);
        progressBar = view.findViewById(R.id.progressBar);
        tvLoading = view.findViewById(R.id.tvLoading);
        // Lấy user_id từ arguments
        if (getArguments() != null) {
            user_id = getArguments().getString("user_id");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("LoveTruyen").child(user_id);
        loveList = new ArrayList<>();
        adapter = new AdapterLove(getContext(), loveList);
        lvLove.setAdapter(adapter);
    }

    private void loadLoveData() {
        progressBar.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        progressBar.setBackgroundResource(R.drawable.frame_animation);
        loadingAnimation = (AnimationDrawable) progressBar.getBackground();
        loadingAnimation.start();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loveList.clear();
                int totalItems = (int) dataSnapshot.getChildrenCount();
                itemsAdded = 0; // Reset items added count

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String truyenId = snapshot.getKey(); // Lấy truyenId từ key của snapshot
                    Long lastReadTime = snapshot.child("last_read_time").getValue(Long.class); // Lấy last_read_time từ vị trí của truyenId

                    if (truyenId != null) {
                        DatabaseReference truyenRef = FirebaseDatabase.getInstance().getReference("DataTruyen").child(truyenId);
                        truyenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot truyenSnapshot) {
                                String tenTruyen = truyenSnapshot.child("TenTruyen").getValue(String.class);
                                String imgUrl = truyenSnapshot.child("Img_Url_Truyen").getValue(String.class);
                                Long slViewLong = truyenSnapshot.child("ViewAll").getValue(Long.class);
                                Long slStarLong = truyenSnapshot.child("TBSoSao").getValue(Long.class);
                                String tacGia = truyenSnapshot.child("TacGiaID").getValue(String.class);
                                String slView = String.valueOf(slViewLong);
                                String slStar = String.valueOf(slStarLong);

                                Love love = new Love(tenTruyen, imgUrl, slView, slStar, tacGia, truyenId, user_id, lastReadTime);
                                loveList.add(love);
                                itemsAdded++;

                                if (itemsAdded == totalItems) {
                                    loveList.sort((ls1, ls2) -> {
                                        Long time1 = ls1.getLastReadTime();
                                        Long time2 = ls2.getLastReadTime();

                                        if (time1 == null && time2 == null) return 0;
                                        if (time1 == null) return 1;
                                        if (time2 == null) return -1;

                                        return time2.compareTo(time1);
                                    });
                                    adapter.notifyDataSetChanged(); // Notify adapter only once
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle error if necessary
                            }

                        });
                        loadingAnimation.stop();
                        progressBar.setVisibility(View.GONE);
                        tvLoading.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if necessary
            }
        });
    }
}
