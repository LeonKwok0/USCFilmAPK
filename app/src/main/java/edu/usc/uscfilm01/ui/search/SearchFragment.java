package edu.usc.uscfilm01.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import edu.usc.uscfilm01.R;
import edu.usc.uscfilm01.detailActivity;
import edu.usc.uscfilm01.ui.home.ImgItem;


public class SearchFragment extends Fragment {
    private View searchView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(searchView ==null){
            searchView = inflater.inflate(R.layout.fragment_search, container, false);
        }

        SearchView searchComp =  searchView.findViewById(R.id.comp_search_view);
        RequestQueue queue = Volley.newRequestQueue(searchView.getContext());
        searchComp.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queue.cancelAll("OnChangeSearch");
                queue.add(fetchData(query,"final"));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("search", newText);
                queue.add(fetchData(newText,"OnChangeSearch"));
                return false;
            }
        });

        return searchView;
    }

    public StringRequest fetchData(String kw, String tag){
        String baseUrl = getResources().getString(R.string.base_url);
        String fullUlr = baseUrl+"search?query="+kw;
        ArrayList<ImgItem> resData = new ArrayList<>();
        StringRequest searchReq = new StringRequest(Request.Method.GET, fullUlr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray result = new JSONArray(response);
                            for (int i = 0; i <result.length() ; i++) {
                                ImgItem item  = new ImgItem();
                                item.setBackdrop_path(result.getJSONObject(i).optString("backdrop_path"));
                                item.setTitle(result.getJSONObject(i).optString("name"));
                                item.setId(result.getJSONObject(i).getInt("id"));
                                item.setPoster_path(result.getJSONObject(i).optString("poster_path"));
                                item.setMedia_type(result.getJSONObject(i).optString("media_type"));
                                item.setDate(result.getJSONObject(i).optString("date"));
                                item.setVote_average(result.getJSONObject(i).optString("vote_average"));
                                resData.add(item);
                            }
                            if(resData.size()>0){
                                searchView.findViewById(R.id.search_no_res_view).findViewById(View.GONE);
                                searchView.findViewById(R.id.search_rec_list).setVisibility(View.VISIBLE);
                                showResult(resData);
                            }else {
                                searchView.findViewById(R.id.search_no_res_view).setVisibility(View.VISIBLE);
                                searchView.findViewById(R.id.search_rec_list).setVisibility(View.GONE);
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
        searchReq.setTag(tag);
        return searchReq;
    }

    public void showResult(ArrayList<ImgItem> data){
        RecyclerView searchRecView =searchView.findViewById(R.id.search_rec_list);
        SearchItemAdapter searchAdapter = new SearchItemAdapter(data, searchView.getContext());
        searchAdapter.setItemListener(new SearchItemAdapter.itemListener() {
            @Override
            public void onClickItem(int position) {
                Intent intent = new Intent(searchView.getContext(), detailActivity.class);
                intent.putExtra("intent",""+data.get(position).watchListString());
                startActivity(intent);
            }

        });
        searchRecView.setAdapter(searchAdapter);
        LinearLayoutManager searchLinearLayout = new LinearLayoutManager(searchView.getContext());
        searchLinearLayout.setOrientation(RecyclerView.VERTICAL);
        searchRecView.setLayoutManager(searchLinearLayout);

    }




}