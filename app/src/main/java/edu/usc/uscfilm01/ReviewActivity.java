package edu.usc.uscfilm01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.USCFilm01);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        loadContent();
    }

    public void loadContent(){
        TextView rate = findViewById(R.id.review_rate);
        TextView author = findViewById(R.id.review_author);
        TextView content = findViewById(R.id.review_content);

        Intent cur = getIntent();
        String authorVal = "by "+cur.getStringExtra("author")+ " on ";
        SimpleDateFormat rawDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = rawDateFormat.parse(cur.getStringExtra("created_at").substring(0,10));
            SimpleDateFormat newDateFormat = new SimpleDateFormat("E, MMM dd yyyy");
            author.setText(authorVal + newDateFormat.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        String rateVal = cur.getStringExtra("rating");
        int rate5 = 0;
        if (!rateVal.equals("null")){
            rate5 = Integer.parseInt(rateVal)/2;
        }
        rate.setText(rate5+"/5");
        content.setText(cur.getStringExtra("content"));
    }
}