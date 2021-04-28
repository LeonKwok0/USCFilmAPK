package edu.usc.uscfilm01.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

import edu.usc.uscfilm01.R;
import edu.usc.uscfilm01.detailActivity;
import edu.usc.uscfilm01.ui.home.DataPersitence;
import edu.usc.uscfilm01.ui.home.ImgItem;

public class RecomendPickAdapter extends RecyclerView.Adapter<RecomendPickAdapter.MyViewHolder> {
    private final List<ImgItem> data;
    private final Context ct;

    public RecomendPickAdapter(List<ImgItem> data, Context context) {
        this.data = data;
        this.ct = context;

    }

    @NonNull
    @Override
    public RecomendPickAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(ct, R.layout.recomend_items,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecomendPickAdapter.MyViewHolder holder, int position) {
        Glide.with(holder.img)
                .load(data.get(position).getPoster_path())
                .fitCenter()
                .transform(new RoundedCorners(40))
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return Math.min(data.size(), 10);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private final ImageView img;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent  intent = new Intent(ct, detailActivity.class);
                    ImgItem cur = data.get(getBindingAdapterPosition());
                    intent.putExtra("intent",""+cur.watchListString());
                    ct.startActivity(intent);
                }
            });


        }
    }




}
