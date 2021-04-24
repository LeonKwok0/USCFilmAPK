package edu.usc.uscfilm01.ui.watchList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

import edu.usc.uscfilm01.R;
import edu.usc.uscfilm01.ui.home.ImgItem;

public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.MyViewHolder> {
    private final List<ImgItem> data;
    private final Context ct;

    public WatchListAdapter(List<ImgItem> data, Context context) {
        this.data = data;
        this.ct = context;
    }

    @NonNull
    @Override
    public WatchListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(ct, R.layout.wl_items,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchListAdapter.MyViewHolder holder, int position) {
        Glide.with(holder.img)
                .load(data.get(position).getPoster_path())
                .fitCenter()
                .transform(new RoundedCorners(40))
                .into(holder.img);
        String tx = data.get(position).getMedia_type().equals("tv")?"TV":"Movie";
        holder.mediaText.setText(tx);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private final ImageView img;
        private final TextView mediaText;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.wl_img_item);
            mediaText = itemView.findViewById(R.id.text_meida_type);
            TextView btn_del = itemView.findViewById(R.id.btn_del);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(iListener !=null){
                        iListener.onClickItem(getBindingAdapterPosition());
                    }
                    Log.d("data","card clicked");
                }
            });

            btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(iListener !=null){
                        iListener.onClickDel(getBindingAdapterPosition());
                    }
                    Log.d("data","btn clicked");

                }
            });

        }
    }

    // for add listener out of this class
    public  interface itemListener{
        void onClickItem(int position);
        void onClickDel(int position);
    }

    private  itemListener iListener;
    public void setItemListener(itemListener listener){
        iListener = listener;
    }


}
