package edu.usc.uscfilm01.ui.detail;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.HashMap;
import java.util.List;

import edu.usc.uscfilm01.R;
import edu.usc.uscfilm01.ui.home.ImgItem;

public class CastListAdapter extends RecyclerView.Adapter<CastListAdapter.MyViewHolder> {
    private final List<HashMap<String,String>> data;
    private final Context ct;

    public CastListAdapter(List<HashMap<String,String>>  data, Context context) {
        this.data = data;
        this.ct = context;
    }

    @NonNull
    @Override
    public CastListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(ct, R.layout.detail_cast_items,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastListAdapter.MyViewHolder holder, int position) {
        Glide.with(holder.img)
                .load(data.get(position).get("profile_path"))
                .fitCenter()
                .transform(new CircleCrop())
                .into(holder.img);

        holder.castName.setText(data.get(position).get("name"));
    }

    @Override
    public int getItemCount() {
        return Math.min(data.size(),6);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private final ImageView img;
        private final TextView castName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.detail_cast_item_img);
            castName = itemView.findViewById(R.id.detail_cast_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(iListener !=null){
                        iListener.onClickItem(getBindingAdapterPosition());
                    }
                    Log.d("detail","card clicked");
                }
            });
        }
    }

    // for add listener out of this class
    public  interface itemListener{
        void onClickItem(int position);
    }

    private  itemListener iListener;
    public void setItemListener(itemListener listener){
        iListener = listener;
    }

}
