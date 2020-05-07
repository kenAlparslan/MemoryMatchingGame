// To do:
// make sure pictures load fully before giving player 5 sec
// improve ui
// modularity && design pattern
// test ui for the landscape on your phone, emulators does not support landscape mode

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
        if( getIntent().getBooleanExtra("Exit", false)){
            finish();
        }
        setContentView(R.layout.grid_choice);

        // load easy
        easy = findViewById(R.id.grid4x4);
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Game.class);
                gameStatus = 0;
                i.putExtra(GAME_MODE, gameStatus);
                MainActivity.this.startActivity(i);
                finish();

            }
        });

        // load medium
        medium = findViewById(R.id.grid5x4);
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Game.class);
                gameStatus = 1;
                i.putExtra(GAME_MODE, gameStatus);
                MainActivity.this.startActivity(i);
                finish();
            }
        });

        // load difficult
        difficult = findViewById(R.id.grid6x4);
        difficult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Game.class);
                gameStatus = 2;
                i.putExtra(GAME_MODE, gameStatus);
                MainActivity.this.startActivity(i);
                finish();
            }
        });


    }
}
