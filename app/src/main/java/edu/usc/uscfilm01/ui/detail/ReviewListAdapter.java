package edu.usc.uscfilm01.ui.detail;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.usc.uscfilm01.R;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.MyViewHolder> {
    private final List<HashMap<String,String>> data;
    private final Context ct;

    public ReviewListAdapter(List<HashMap<String,String>>  data, Context context) {
        this.data = data;
        this.ct = context;
    }

    @NonNull
    @Override
    public ReviewListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(ct, R.layout.detail_review_items,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewListAdapter.MyViewHolder holder, int position) {
        HashMap<String,String> cur = data.get(position);
        String author = "by "+cur.get("author")+ " on ";
        SimpleDateFormat rawDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = rawDateFormat.parse(cur.get("created_at").substring(0,10));
            SimpleDateFormat newDateFormat = new SimpleDateFormat("E, MMM dd yyyy");
            holder.author.setText(author + newDateFormat.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        String rate = cur.get("rating");
        int rate5 = 0;
        if (!rate.equals("null")){
            rate5 = Integer.parseInt(rate)/2;
        }
        holder.rate.setText(rate5+"/5");
        holder.content.setText(cur.get("content"));
    }

    @Override
    public int getItemCount() {
        return Math.min(data.size(),6);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView rate;
        private final TextView author;
        private final TextView content;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rate = itemView.findViewById(R.id.review_rate);
            author = itemView.findViewById(R.id.review_author);
            content = itemView.findViewById(R.id.review_content);

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
