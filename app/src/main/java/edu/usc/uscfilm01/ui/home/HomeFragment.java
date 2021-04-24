package edu.usc.uscfilm01.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import edu.usc.uscfilm01.R;

public class HomeFragment extends Fragment {

    private View homeView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (homeView==null){
            homeView = inflater.inflate(R.layout.fragment_home, container, false);
        }

        fetchData("movie", homeView);

        Button btnMov = homeView.findViewById(R.id.button_mov);
        Button btnTv = homeView.findViewById(R.id.button_tv);
        btnMov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMov.setTextColor(getResources().getColor(R.color.white));
                btnTv.setTextColor(getResources().getColor(R.color.colorMost));
                fetchData("movie", homeView);
            }
        });

        btnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMov.setTextColor(getResources().getColor(R.color.colorMost));
                btnTv.setTextColor(getResources().getColor(R.color.white));
                fetchData("tv", homeView);
            }
        });
        return homeView;

    }

    // modeUrl : movie tv
    public void fetchData(String modeUrl, View root){
        showSpinner(View.VISIBLE,root);
        String baseUrl = "http://10.0.2.2:3001/";
        String topRatedUrl = baseUrl + "collect/"+modeUrl+"/top_rated";
        String popularUrl = baseUrl + "collect/"+modeUrl+"/popular";
        String mvBannerUrl = baseUrl + "current_play";
        String tvBannerUrl = baseUrl + "collect/trending/tv/day";
        String bannerUrl = modeUrl.equals("tv") ?tvBannerUrl:mvBannerUrl;

        RequestQueue queue = Volley.newRequestQueue(root.getContext());
        ArrayList<ImgItem>  popularData = new ArrayList<>();
        ArrayList<ImgItem> topData = new ArrayList<>();
        ArrayList<ImgItem> bannerData = new ArrayList<>();
        StringRequest popular = commonFetch(popularUrl,popularData);
        StringRequest top = commonFetch(topRatedUrl,topData);
        StringRequest banner = commonFetch(bannerUrl,bannerData);
        queue.add(banner);
        queue.add(top);
        queue.add(popular);
        // TODO this is deprecated method, change in future
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            public void onRequestFinished(Request<Object> request){
                addSlider( bannerData,root);
                addGallery(topData,R.id.gallery,root);
                addGallery(popularData,R.id.gallery_popular,root);
                showSpinner(View.GONE,root);
            }
        });
    }


    public  void showSpinner(int flag,View root){
        root.findViewById(R.id.loading_effect).setVisibility(flag);
        int other = View.GONE;
        if(flag != View.VISIBLE){
            other = View.VISIBLE;
        }
        root.findViewById(R.id.header).setVisibility(other);
        root.findViewById(R.id.home_content).setVisibility(other);
    }


    // add Top-Rated OR pouplar gallery
    public void addGallery(ArrayList<ImgItem> data,int galleryId, View root){
        RecyclerView gallery = root.findViewById(galleryId);
        gallery.setNestedScrollingEnabled(false);
        GalleryAdapter galleryAdapter = new GalleryAdapter(data, root.getContext());


        gallery.setAdapter(galleryAdapter);
        LinearLayoutManager galleryLinearLayout = new LinearLayoutManager(root.getContext());
        galleryLinearLayout.setOrientation(RecyclerView.HORIZONTAL);
        gallery.setLayoutManager(galleryLinearLayout);
    }


    public void addSlider(ArrayList<ImgItem> sliderDataArrayList, View root){
        SliderView sliderView = root.findViewById(R.id.slider);
        // passing this array list inside our adapter class.
        SliderAdapter adapter = new SliderAdapter(sliderDataArrayList);
        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        // below method is used to
        // setadapter to sliderview.
        sliderView.setSliderAdapter(adapter);
        // below method is use to set
        // scroll time in seconds.
        sliderView.setScrollTimeInSec(6);
        // to set it scrollable automatically
        // we use below method.
        sliderView.setAutoCycle(true);
        // to start autocycle below method is used.
        sliderView.startAutoCycle();
    }

    public StringRequest commonFetch(String url,List<ImgItem> resultData){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray result = new JSONArray(response);
                            for (int i = 0; i <result.length() ; i++) {
                                ImgItem item  = new ImgItem();
                                item.setBackdrop_path(result.getJSONObject(i).optString("backdrop_path"));
                                item.setTitle(result.getJSONObject(i).optString("title"));
                                item.setId(result.getJSONObject(i).getInt("id"));
                                item.setPoster_path(result.getJSONObject(i).optString("poster_path"));
                                item.setMedia_type(result.getJSONObject(i).optString("media_type"));
                                resultData.add(item);
                            }

                        } catch (JSONException e) {
                            Log.d("fetch",e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("fetch",error.toString());
                    }
                });
        return stringRequest;
    }

}