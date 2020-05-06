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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Game extends AppCompatActivity {

    public static final String GAME_MODE = "com.example.application.example.GAME_MODE";
    private static final String TAG = Game.class.getName();
    int desaultVal = 0;
    TableLayout table = null;
    TableRow tr = null;
    ImageView iv = null;
    int gameStatus;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        gameStatus = intent.getIntExtra(GAME_MODE, desaultVal);

        if(gameStatus == 0) {
            Log.d("In GAME Class", "Intent returned default value");
        } else if(gameStatus == 1) {
            setContentView(R.layout.grid_4x4);
        } else if(gameStatus == 2) {
            setContentView(R.layout.grid_5x4);
        } else if(gameStatus == 3) {
            setContentView(R.layout.grid_6x4);
        }

        sendAndRequestResponse();


    }

    private void setUpPuzzle(ArrayList<String> al) {

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


                    }
                }
            }

        }



    }

    private void sendAndRequestResponse() {
        String url = "https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";
        final ArrayList<String> al = new ArrayList<>();

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

                    for(int i=0; i<12; ++i) {
                        obj = arr.getJSONObject(i);
                        obj = obj.getJSONObject("image");
                        al.add(i, obj.getString("src"));
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
