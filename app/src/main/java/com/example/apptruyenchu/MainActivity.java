package com.example.apptruyenchu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.apptruyenchu.account.Login;
import com.example.apptruyenchu.account.Profile;
import com.example.apptruyenchu.databinding.ActivityMainBinding;
import com.example.apptruyenchu.love.DanhSachLove;
import com.example.apptruyenchu.lichSu.LichSu;
import com.example.apptruyenchu.toastSetting.CustomToast;
import com.example.apptruyenchu.truyen.DanhSachTruyen;public class MainActivity extends AppCompatActivity {

    private String userId;
    private ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);


        userId = sharedPreferences.getString("user_id", null);
        if (userId == null) {
            CustomToast.showToast(this, "đéo lấy được id");
            redirectToLogin();
            return;
        }

        if (!isLoggedIn()) {
            redirectToLogin();
            return;
        }

        replaceFragment(new DanhSachTruyen());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_love) {
                replaceFragment(new DanhSachLove());
            } else if (item.getItemId() == R.id.navigation_history) {
                replaceFragment(new LichSu());
            } else if (item.getItemId() == R.id.navigation_truyen) {
                replaceFragment(new DanhSachTruyen());
            } else if (item.getItemId() == R.id.profile_user) {
                replaceFragment(new Profile());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("user_id", userId);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}