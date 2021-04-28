package edu.usc.uscfilm01;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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

import edu.usc.uscfilm01.ui.detail.CastListAdapter;
import edu.usc.uscfilm01.ui.detail.RecomendPickAdapter;
import edu.usc.uscfilm01.ui.detail.ReviewListAdapter;
import edu.usc.uscfilm01.ui.home.DataPersitence;
import edu.usc.uscfilm01.ui.home.GalleryAdapter;
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

        StringRequest cast = commonFetch(baseUrl + "3/" + mediaType + "/" + mediaId + "/credits", new DataProcessing() {
            @Override
            public void processData(String resp) {
                loadCast(resp);
            }
        });

        StringRequest review = commonFetch(baseUrl + "3/" + mediaType + "/" + mediaId + "/reviews", new DataProcessing() {
                    @Override
                    public void processData(String resp) {
                        loadReviews(resp);
                    }
        });

        StringRequest rec = commonFetch(baseUrl + "collect/" + mediaType + "/" + mediaId + "/similar", new DataProcessing() {
            @Override
            public void processData(String resp) {
//                collect/tv/1399/similar
                loadRecommendedPicks(resp);
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
        queue.add(cast);
        queue.add(review);
        queue.add(rec);
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
            item.setTitle(result.optString("title"));


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

                    Toast.makeText(getApplicationContext(), item.getTitle()+" was removed from watchlist ", Toast.LENGTH_SHORT).show();
                    localData.remove(mediaId);
                    addBtn.setBackground(getDrawable(R.drawable.ic_baseline_add_circle_outline_24));
                }else{
                    Toast.makeText(getApplicationContext(), item.getTitle()+" was added to watchlist ", Toast.LENGTH_SHORT).show();
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

    public void loadCast(String resp){
        try {
            JSONArray casts = new JSONArray(resp);
            ArrayList<HashMap<String,String>> data = new ArrayList<>();
            for (int i = 0; i <casts.length() ; i++) {
                HashMap<String,String> tmp = new HashMap<>();
                tmp.put("name",casts.getJSONObject(i).optString("name"));
                tmp.put("profile_path",casts.getJSONObject(i).optString("profile_path"));
                data.add(tmp);
            }

            if(data.size()==0){
                findViewById(R.id.detail_cast_title).setVisibility(View.GONE);
            }

            RecyclerView castRecView = findViewById(R.id.detail_cast_content);
            CastListAdapter castAdapter = new CastListAdapter(data,this);
            castRecView.setAdapter(castAdapter);
            GridLayoutManager wlGridLayout = new GridLayoutManager(this,3);
            wlGridLayout.setOrientation(RecyclerView.VERTICAL);
            castRecView.setLayoutManager(wlGridLayout);

        } catch (JSONException e) {
            Log.d(TAG, "loadCast: "+e.toString());
        }

    }

    public void loadReviews(String resp){
        try {
            JSONArray casts = new JSONArray(resp);
            ArrayList<HashMap<String,String>> data = new ArrayList<>();
            for (int i = 0; i <casts.length() ; i++) {
                HashMap<String,String> tmp = new HashMap<>();
                tmp.put("author",casts.getJSONObject(i).optString("author"));
                tmp.put("created_at",casts.getJSONObject(i).optString("created_at"));
                tmp.put("rating",casts.getJSONObject(i).optString("rating"));
                tmp.put("content",casts.getJSONObject(i).optString("content"));
                data.add(tmp);
            }

            if (data.size()==0){
                TextView reviewTitle = findViewById(R.id.detail_review_title);
                reviewTitle.setVisibility(View.GONE);
            }

            RecyclerView reviewRecView = findViewById(R.id.detail_review_content);
            ReviewListAdapter reviewAdapter = new ReviewListAdapter(data,this);
            reviewAdapter.setItemListener(new ReviewListAdapter.itemListener() {
                @Override
                public void onClickItem(int position) {
                    Intent  intent = new Intent(getApplicationContext(), ReviewActivity.class);
                    intent.putExtra("author",data.get(position).get("author") );
                    intent.putExtra("created_at",data.get(position).get("created_at") );
                    intent.putExtra("rating",data.get(position).get("rating") );
                    intent.putExtra("content",data.get(position).get("content") );
                    startActivity(intent);
                }
            });
            reviewRecView.setAdapter(reviewAdapter);
            reviewRecView.setNestedScrollingEnabled(false);
            LinearLayoutManager linearLayout = new LinearLayoutManager(this);
            linearLayout.setOrientation(RecyclerView.VERTICAL);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
            reviewRecView.setLayoutManager(linearLayout);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void loadRecommendedPicks(String resp){
//        collect/tv/1399/similar
        JSONArray picks = null;
        ArrayList<ImgItem> data= new ArrayList<>();;
        try {
            picks = new JSONArray(resp);
            for (int i = 0; i <picks.length() ; i++) {
                ImgItem tmp = new ImgItem();
                tmp.setId(picks.getJSONObject(i).optInt("id"));
                tmp.setPoster_path(picks.getJSONObject(i).optString("poster_path"));
                tmp.setMedia_type(picks.getJSONObject(i).optString("media_type"));
                data.add(tmp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecyclerView recPickRecView = findViewById(R.id.detail_rec_content);
        recPickRecView.setNestedScrollingEnabled(false);
        RecomendPickAdapter recPickAdapter = new RecomendPickAdapter(data, this);

        recPickRecView.setAdapter(recPickAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recPickRecView.setLayoutManager(linearLayoutManager);

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