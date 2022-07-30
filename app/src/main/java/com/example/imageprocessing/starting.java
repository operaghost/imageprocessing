package com.example.imageprocessing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class starting extends AppCompatActivity {

    private ImageButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        StatusBarUtill.transparencyBar(this);

        btn = findViewById(R.id.openimg);
        btn.setOnClickListener(v -> {

            Intent i = new Intent(starting.this, MainActivity.class);
            startActivity(i);
            System.out.println("000000000");
        });
    }
}