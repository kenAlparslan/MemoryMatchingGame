package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String GAME_MODE = "com.example.application.example.GAME_MODE";
    TextView easy = null;
    TextView medium = null;
    TextView difficult = null;
    int gameStatus = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_choice);

        easy = findViewById(R.id.grid4x4);
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Game.class);
                gameStatus = 0;
                i.putExtra(GAME_MODE, gameStatus);
                MainActivity.this.startActivity(i);

            }
        });

        medium = findViewById(R.id.grid5x4);
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Game.class);
                gameStatus = 1;
                i.putExtra(GAME_MODE, gameStatus);
                MainActivity.this.startActivity(i);

            }
        });

        difficult = findViewById(R.id.grid6x4);
        difficult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Game.class);
                gameStatus = 2;
                i.putExtra(GAME_MODE, gameStatus);
                MainActivity.this.startActivity(i);

            }
        });


    }
}
