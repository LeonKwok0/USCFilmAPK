package edu.usc.uscfilm01;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.uscfilm01.ui.home.DataPersitence;
import edu.usc.uscfilm01.ui.home.ImgItem;


public class detailActivity extends AppCompatActivity {

    private ImgItem item;
    private String mediaType;
    private String mediaId;
    private DataPersitence localData;
    public String TAG = "detail";
    public String backdrop_path="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.USCFilm01);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        item = ImgItem.getInstance(intent.getStringExtra("intent"));
        mediaType = item.getMedia_type();
        mediaId = item.getId()+"";
        localData = new DataPersitence(this,"alldata");
        loadShareBtn();
        RequestQueue queue = Volley.newRequestQueue(this);
        generateReq(queue);
    }

    public void generateReq(RequestQueue queue){
        String baseUrl = getResources().getString(R.string.base_url);


        StringRequest video = commonFetch(baseUrl + "3/" + mediaType + "/" + mediaId + "/videos", new DataProcessing() {
            @Override
            public void processData(String resp) {
                loadVideo(resp);
            }
        });

        StringRequest basic = commonFetch(baseUrl + "detail/" + mediaType + "/" + mediaId, new DataProcessing() {
            @Override
            public void processData(String resp) {
                loadBasic(resp);
                loadAddBtn();
                queue.add(video);
            }
        });

        queue.add(basic);
    }

    public void loadVideo(String data){
        try {
            JSONArray result = new JSONArray(data);
            if(result.length()==0){
                ImageView backup = findViewById(R.id.backup_img);
                Glide.with(getBaseContext())
                        .load(backdrop_path)
                        .fitCenter()
                        .into(backup);
                Log.d(TAG, "loadVideo: "+backdrop_path);
                backup.setVisibility(View.VISIBLE);
                findViewById(R.id.youtube_player_view).setVisibility(View.GONE);
            }else{
                String videoId = result.getJSONObject(0).optString("key");
                YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
                getLifecycle().addObserver(youTubePlayerView);
                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0);
                    }
                });
                findViewById(R.id.youtube_player_view).setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            Log.d(TAG, "loadVideo: "+e.toString());
        }
    }

    public void loadBasic(String resp){
        JSONObject result = null;
        try {
            result = new JSONObject(resp);
            TextView mediaTitle = findViewById(R.id.detail_media_title);
            mediaTitle.setText(result.getString("title"));
            TextView overview = findViewById(R.id.detail_overview_content);
            overview.setText(result.optString("overview"));

            StringBuilder geners = new StringBuilder();
            JSONArray generList = result.getJSONArray("genres");
            for (int i = 0; i <generList.length() ; i++) {
                if(i<generList.length()-1){
                    geners.append(generList.getJSONObject(i).optString("name")).append(", ");
                }else{
                    geners.append(generList.getJSONObject(i).optString("name"));
                }
            }
            TextView genresView = findViewById(R.id.detail_genres_content);
            genresView.setText(geners.toString());

            TextView year = findViewById(R.id.detail_year_content);
            year.setText(result.optString("release_date").substring(0,4));

            backdrop_path = result.optString("backdrop_path");
            item.setPoster_path(result.optString("poster_path"));


        } catch (JSONException e) {
            Log.d(TAG, "loadBasic: "+e.toString());
        }

    }


    public void loadAddBtn(){
        TextView addBtn = findViewById(R.id.detail_btn_add);
        if(localData.contains(mediaId)){
            addBtn.setBackground(getDrawable(R.drawable.ic_baseline_remove_circle_outline_24));
        }else{
            addBtn.setBackground(getDrawable(R.drawable.ic_baseline_add_circle_outline_24));
        }
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(localData.contains(mediaId)) {
                    localData.remove(mediaId);
                    addBtn.setBackground(getDrawable(R.drawable.ic_baseline_add_circle_outline_24));
                }else{
                    localData.putString(mediaId, item);
                    addBtn.setBackground(getDrawable(R.drawable.ic_baseline_remove_circle_outline_24));
                }
                localData.commit();
            }
        });
    }

    public void loadShareBtn(){
        String tmdbUrl = "https://www.themoviedb.org/"+mediaType+"/"+mediaId;
        TextView fbBtn = findViewById(R.id.detail_btn_fb);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.facebook.com/sharer/sharer.php?u="+tmdbUrl+"%2F&amp;src=sdkpreparse";
                browserIntent.setData(Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        TextView twBtn = findViewById(R.id.detail_btn_tw);
        twBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://twitter.com/intent/tweet?text="+tmdbUrl;
                browserIntent.setData(Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }

    public void loadCast(){

    }

    public void loadReviews(){

    }

    public void loadRecommendedPicks(){

    }


    public StringRequest commonFetch(String url, HashMap<String,String> data){
        return  new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject result = null;
                        try {
                            result = new JSONObject(response);
                            data.put("title",result.getString("title"));
                            data.put("genres",result.getString("genres"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("fetch",error.toString());
            }
        });
    }


    public StringRequest commonFetch(String url,  DataProcessing processor){
        return  new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processor.processData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("fetch",error.toString());
            }
        });
    }

    public interface  DataProcessing{
        void processData(String resp);
    }

}