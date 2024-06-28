package com.example.salessync;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Home extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TabAdapter tabAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        viewPager2.setAdapter(tabAdapter);

        TabLayoutMediator TLM=new TabLayoutMediator(
                tabLayout,
                viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int i) {
                        switch (i){
                            case 1:
                                tab.setText("Inventory");
                                break;
                            case 0:
                                tab.setText("Sales");
                                break;
                        }
                    }
                }
        );
        TLM.attach();

    }

    private void init(){
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);
        tabAdapter = new TabAdapter(this);
    }

}