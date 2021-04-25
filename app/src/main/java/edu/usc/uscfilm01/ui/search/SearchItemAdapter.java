package edu.usc.uscfilm01.ui.search;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

import edu.usc.uscfilm01.R;
import edu.usc.uscfilm01.ui.home.ImgItem;

public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.MyViewHolder> {
    private final List<ImgItem> data;
    private final Context ct;

    public SearchItemAdapter(List<ImgItem> data, Context context) {
        this.data = data;
        this.ct = context;
    }

    @NonNull
    @Override
    public SearchItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(ct, R.layout.search_items,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchItemAdapter.MyViewHolder holder, int position) {
        ImgItem curItem = data.get(position);
        Glide.with(holder.img)
                .load(curItem.getBackdrop_path())
                .transform(new RoundedCorners(40))
                .into(holder.img);
        holder.rate.setText(curItem.getVote_average());
        String year = curItem.getDate().substring(0,Math.min(curItem.getDate().length(),4));
        String typeYear = curItem.getMedia_type().toUpperCase()+" ("+year+")";
        holder.typeYear.setText(typeYear);
        SpannableString textSpan = new SpannableString (curItem.getTitle().toUpperCase());
        textSpan.setSpan(new AbsoluteSizeSpan(60),0,1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        holder.title.setText(textSpan);


    }

    @Override
    public int getItemCount() {
        return Math.min(data.size(),20);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private final ImageView img;
        private final TextView rate;
        private final TextView typeYear;
        private final TextView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.search_img_item);
            rate = itemView.findViewById(R.id.text_rate);
            typeYear = itemView.findViewById(R.id.text_type_year);
            title = itemView.findViewById(R.id.text_item_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(iListener !=null){
                        iListener.onClickItem(getBindingAdapterPosition());
                    }
                    Log.d("data","card clicked");
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
