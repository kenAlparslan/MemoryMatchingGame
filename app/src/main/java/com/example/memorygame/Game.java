package com.example.memorygame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game extends AppCompatActivity {

    public static final String GAME_MODE = "com.example.application.example.GAME_MODE";
    private static final String TAG = Game.class.getName();
    int desaultVal = -1;
    ArrayList<ImageView> imgs = new ArrayList<>();
    TableLayout table = null;
    int gameStatus;
    int limit;
    int win = -1;
    View.OnClickListener clickListener;
    ArrayList<Integer> shuffleIndex; // will be populated with players game mode: easy, medium, hard

    // used for the shuffle logic
    ArrayList<Integer> easy = new ArrayList<>(
            Arrays.asList(0,0,1,1,2,2,3,3,4,4,5,5,6,6,7,7)
    );
    ArrayList<Integer> medium = new ArrayList<>(
            Arrays.asList(0,0,1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8,9,9)
    );

    ArrayList<Integer> difficult = new ArrayList<>(
            Arrays.asList(0,0,1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8,9,9,10,10,11,11)
    );


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        gameStatus = intent.getIntExtra(GAME_MODE, desaultVal);

        // initialize variables
        if(gameStatus == -1) {
            Log.d("In GAME Class", "Intent returned default value");
        } else if(gameStatus == 0) {
            setContentView(R.layout.grid_4x4);
            shuffleIndex = easy;
            win = shuffleIndex.size();
        } else if(gameStatus == 1) {
            setContentView(R.layout.grid_5x4);
            shuffleIndex = medium;
            win = shuffleIndex.size();
        } else if(gameStatus == 2) {
            setContentView(R.layout.grid_6x4);
            shuffleIndex = difficult;
            win = shuffleIndex.size();
        }

        sendAndRequestResponse(); // make api call

        clickListener = new View.OnClickListener() {
            public void onClick(View v) { // game click

                Runnable r = new MyRunnable(v);
                new Thread(r).start();
            }
        };

    }

    private void setUpPuzzle(ArrayList<String> al) { // set up the grid, download images

        Thread setupThread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(7000);
                    table = findViewById(R.id.gameTable);
                    int rowCount = table.getChildCount();
                    int columnCount;
                    View rowView;
                    for(int i =0 ;i < rowCount;i++) {
                        rowView = table.getChildAt(i);

                        if(rowView instanceof TableRow) {
                            TableRow tableRow = (TableRow)rowView;
                            columnCount = tableRow.getChildCount();

                            for(int j = 0;j<columnCount;j++)
                            {
                                View columnView = tableRow.getChildAt(j);
                                if(columnView instanceof ImageView)
                                {
                                    ImageView iv = (ImageView)columnView;
                                    iv.setColorFilter(Color.argb(255, 255, 255, 255));
                                    iv.setTag(0);

                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        table = findViewById(R.id.gameTable);
        Random rand = new Random();
        int rowCount = table.getChildCount();
        int columnCount;
        int index = 0;
        View rowView;
        for(int i =0 ;i < rowCount;i++) {
            rowView = table.getChildAt(i);

            if(rowView instanceof TableRow) {
                TableRow tableRow = (TableRow)rowView;
                columnCount = tableRow.getChildCount();


                for(int j = 0;j<columnCount;j++)
                {
                    View columnView = tableRow.getChildAt(j);
                    if(columnView instanceof ImageView)
                    {
                        index = rand.nextInt(shuffleIndex.size());
                        ImageView iv = (ImageView)columnView;
                        Picasso.get().load(al.get(shuffleIndex.get(index))).resize(70,80).into(iv);
                        iv.setContentDescription(al.get(shuffleIndex.get(index)));
                        shuffleIndex.remove(index);
                        iv.setOnClickListener(clickListener);

                    }
                }
            }

        }

        Toast toast = Toast.makeText(getApplicationContext(), "You have 5 seconds to memorize the cards" , Toast.LENGTH_LONG);//display the response on screen
        toast.setGravity(Gravity.TOP,0,20);
        toast.show();
        setupThread.start();


    }

    public class MyRunnable implements Runnable { // game logic

        private ImageView imageView;

        public MyRunnable(Object parameter) {
            if(parameter instanceof ImageView) {
                imageView = (ImageView) parameter;
            }
        }

        public void run() {
            try {

                ImageView iv = imageView;
                if(iv.getTag().equals(1)) {
                    if(imgs.size() == 1) {
                        if(imgs.get(0) != iv) {
                            iv.setColorFilter(Color.argb(255, 255, 255, 255));
                            iv.setTag(0);
                        }
                    }
                    else {
                        iv.setColorFilter(Color.argb(255, 255, 255, 255));
                        iv.setTag(0);
                    }

                } else {
                    if(imgs.size() == 1) { // user should not click on the same image
                        if(imgs.get(0) != iv) {
                            iv.setColorFilter(null);
                            iv.setTag(1);
                            imgs.add(iv);
                        }
                    }
                    else {
                        iv.setColorFilter(null);
                        iv.setTag(1);
                        imgs.add(iv);
                    }

                }

                if(imgs.size() == 2) {
                    if(imgs.get(0).getContentDescription() == imgs.get(1).getContentDescription()) {
                        imgs.get(0).setEnabled(false);
                        imgs.get(1).setEnabled(false);
                        imgs.get(0).setTag(2);
                        imgs.get(1).setTag(2);

                        //Log.d("------------------", "found a match!!!");
                        imgs.clear();
                    }
                    else {
                        imageToggle(0);
                        Thread.sleep(750);
                        imageToggle(1);
                        imgs.get(0).setColorFilter(Color.argb(255, 255, 255, 255));
                        imgs.get(1).setColorFilter(Color.argb(255, 255, 255, 255));
                        imgs.get(0).setTag(0);
                        imgs.get(1).setTag(0);
                        imgs.clear();
                    }
                }

                if(checkWin() == 1) {
                    Thread.sleep(500);
                    Intent i = new Intent(Game.this, LastPage.class);
                    Game.this.startActivity(i);
                    //Log.d("!!!!!!!!!!!!!!!!!!!", "You WON!!!!!");
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void imageToggle(int mode) { // toggle onClickListener between choices

            table = findViewById(R.id.gameTable);
            int rowCount = table.getChildCount();
            int columnCount;
            View rowView;
            for(int i =0 ;i < rowCount;i++) {
                rowView = table.getChildAt(i);

                if(rowView instanceof TableRow) {
                    TableRow tableRow = (TableRow)rowView;
                    columnCount = tableRow.getChildCount();


                    for(int j = 0;j<columnCount;j++)
                    {
                        View columnView = tableRow.getChildAt(j);
                        if(columnView instanceof ImageView)
                        {
                            ImageView iv = (ImageView)columnView;
                            if(mode == 0) { // disable

                                if(iv.getTag().equals(0) || iv.getTag().equals(1)) {
                                    iv.setOnClickListener(null);
                                }
                            } else if(mode == 1) { // enable
                                if(iv.getTag().equals(0) || iv.getTag().equals(1)) {
                                    iv.setOnClickListener(clickListener);
                                }
                            }
                        }
                    }
                }

            }


        }

        public int checkWin() { // check if all matches are found

            table = findViewById(R.id.gameTable);
            int rowCount = table.getChildCount();
            int columnCount;
            int wincount=0;
            View rowView;
            for(int i =0 ;i < rowCount;i++) {
                rowView = table.getChildAt(i);

                if(rowView instanceof TableRow) {
                    TableRow tableRow = (TableRow)rowView;
                    columnCount = tableRow.getChildCount();

                    for(int j = 0;j<columnCount;j++)
                    {
                        View columnView = tableRow.getChildAt(j);
                        if(columnView instanceof ImageView)
                        {
                            ImageView tmp = (ImageView)columnView;
                            if(tmp.getTag().equals(2)) {
                                ++wincount;
                            }
                        }
                    }
                }
            }
            if(wincount == win) {
                return 1;
            }


            return 0;
        }
    }


    private void sendAndRequestResponse() { // make api call
        String url = "https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";
        final ArrayList<String> al = new ArrayList<>();

        if(gameStatus == 0) {
            limit = 8;
        } else if(gameStatus == 1) {
            limit = 10;
        } else if(gameStatus == 2) {
            limit = 13;
        }
        //RequestQueue initialized
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);

        //String Request initialized

        //display the response on screen
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray arr = obj.getJSONArray("products");
                    int index = 0;

                    for(int i=0; i<limit; ++i) {
                        if (i == 11) {
                            continue; // 10 and 11 are the same pic
                        }
                        obj = arr.getJSONObject(i);
                        obj = obj.getJSONObject("image");
                        al.add(index, obj.getString("src"));
                        ++index;
                    }
                    setUpPuzzle(al);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG+"", "Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }

}
