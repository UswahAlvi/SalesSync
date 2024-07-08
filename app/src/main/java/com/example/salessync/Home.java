package com.example.salessync;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Home extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TabAdapter tabAdapter;
    ImageView ivMenu;
    NavigationView navigationView;
    private View bottomSheetView;
    private BottomSheetDialog bottomSheetDialog;
    private FirebaseAuth mAuth;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        viewPager2.setAdapter(tabAdapter);

        TabLayoutMediator TLM = new TabLayoutMediator(
                tabLayout,
                viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("Inventory");
                                break;
                            case 1:
                                tab.setText("Sales");
                                break;
                        }
                    }
                }
        );
        TLM.attach();
        builder.setTitle("Are you sure you want to log out?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            mAuth = FirebaseAuth.getInstance();
                            mAuth.signOut();
                            startActivity(new Intent(this,com.example.salessync.MainActivity.class));
                        }).setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        addDialogBox();


    }


    private void init() {
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);
        ivMenu=findViewById(R.id.ivMenu);
        builder = new AlertDialog.Builder(this);
        tabAdapter = new TabAdapter(this);

    }
    private void addDialogBox() {
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeBottomDialogBox();
                navigationView = bottomSheetView.findViewById(R.id.navigationView);
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.logout){
                            bottomSheetDialog.dismiss();
                            builder.create().show();

                        }
                        return true;
                    }
                });

            }
        });
    }
    private void initializeBottomDialogBox() {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_navigation, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
    }
}
