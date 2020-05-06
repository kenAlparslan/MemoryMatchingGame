package com.example.memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    TableLayout table = null;
    TableRow tr = null;
    ImageView iv = null;
    int gameStatus;
    int limit;
    ArrayList<Integer> shuffleIndex;
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

        if(gameStatus == -1) {
            Log.d("In GAME Class", "Intent returned default value");
        } else if(gameStatus == 0) {
            setContentView(R.layout.grid_4x4);
            shuffleIndex = easy;
        } else if(gameStatus == 1) {
            setContentView(R.layout.grid_5x4);
            shuffleIndex = medium;
        } else if(gameStatus == 2) {
            setContentView(R.layout.grid_6x4);
            shuffleIndex = difficult;
        }

        sendAndRequestResponse();



    }

    private void setUpPuzzle(ArrayList<String> al) {

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
                        shuffleIndex.remove(index);
                    }
                }
            }

        }



    }

    private void sendAndRequestResponse() {
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
                    //Toast.makeText(getApplicationContext(), "Response :" + al.get(0) , Toast.LENGTH_LONG).show();//display the response on screen
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
