package edu.usc.uscfilm01.ui.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import edu.usc.uscfilm01.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    private List<ImgItem> data;
    private Context ct;

    public GalleryAdapter(List<ImgItem> data, Context context) {
        this.data = data;
        this.ct = context;
    }

    @NonNull
    @Override
    public GalleryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(ct, R.layout.grallery_items,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.MyViewHolder holder, int position) {
        Glide.with(holder.img)
                .load(data.get(position).getPoster_path())
                .fitCenter()
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return data.size()>=10?10:data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_item);
        }
    }
}
