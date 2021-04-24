package edu.usc.uscfilm01.ui.watchList;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;

import edu.usc.uscfilm01.R;
import edu.usc.uscfilm01.ui.home.DataPersitence;


public class WatchListFragment extends Fragment {


    private View watchView;
    private SharedPreferences localData;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if(watchView==null){
            watchView = inflater.inflate(R.layout.fragment_watch_list, container, false);
        }

        // fetch data from local
        DataPersitence localData = new DataPersitence(watchView.getContext(),"alldata");
        // if no data show empty data page
        int size = localData.getItemList().size();
        if(size == 0){
            Log.d("data","no data");
            watchView.findViewById(R.id.empty_wl).setVisibility(View.VISIBLE);
            watchView.findViewById(R.id.wl_content_view).setVisibility(View.GONE);
        }else {
            showPic(watchView,localData);
        }

        return watchView;
    }

    public void showPic(View root, DataPersitence localData){

        RecyclerView wlRecView = root.findViewById(R.id.wl_rec_list);
        WatchListAdapter wlAdapter = new WatchListAdapter(localData.getItemList(), root.getContext());
        wlAdapter.setItemListener(new WatchListAdapter.itemListener() {
            @Override
            public void onClickItem(int position) {

            }
            @Override
            public void onClickDel(int position) {
                localData.getItemList().remove(position);
                localData.commit();
                wlAdapter.notifyDataSetChanged();
            }
        });
        wlRecView.setAdapter(wlAdapter);
        LinearLayoutManager wlLinearLayout = new LinearLayoutManager(root.getContext());
        GridLayoutManager wlGridLayout = new GridLayoutManager(root.getContext(),3);
        wlGridLayout.setOrientation(RecyclerView.VERTICAL);
        wlRecView.setLayoutManager(wlGridLayout);

        // set drag and drop
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.START|ItemTouchHelper.END,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();
                Collections.swap(localData.getItemList(),fromPosition,toPosition);
                localData.commit();
                localData.redoOrder();
                wlAdapter.notifyItemMoved(fromPosition,toPosition);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(wlRecView);

    }


}