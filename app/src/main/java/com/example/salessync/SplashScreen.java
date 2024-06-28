package com.example.salessync;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {
    Animation animLogo,animSlogan;
    TextView tvSlogan;
    ImageView ivLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreen.this,com.example.salessync.MainActivity.class));
                        finish();
                    }
                },2000);
    }
    private void init(){
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        animLogo= AnimationUtils.loadAnimation(this,R.anim.logo_animation);
        animSlogan=AnimationUtils.loadAnimation(this,R.anim.slogan_animation);
        ivLogo=findViewById(R.id.ivLogo);
        tvSlogan=findViewById(R.id.tvSlogan);

        ivLogo.setAnimation(animLogo);
        tvSlogan.setAnimation(animSlogan);

    }
}