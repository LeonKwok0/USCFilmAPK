package edu.usc.uscfilm01.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

import edu.usc.uscfilm01.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    private final List<ImgItem> data;
    private final Context ct;
    private DataPersitence localData;

    public GalleryAdapter(List<ImgItem> data, Context context) {
        this.data = data;
        this.ct = context;
        this.localData = new DataPersitence(ct,"alldata");
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
            TextView btn_pop = itemView.findViewById(R.id.btn_pop);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(iListener !=null){
                        iListener.onClickItem(getBindingAdapterPosition());
                    }
                }
            });

            btn_pop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popMenuEvent(btn_pop, getBindingAdapterPosition());
                }
            });
            // missunderstanding of doc should not use longpress but i dont want to del
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    popMenuEvent(img, getBindingAdapterPosition());
                    if (iListener !=null){
                        iListener.onPressItem(img, getBindingAdapterPosition());
                    }
                    return true;
                }
            });

        }
    }

    public void popMenuEvent(View anchor,int position){
        PopupMenu popupMenu = new PopupMenu(this.ct,anchor);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        ImgItem item = data.get(position);

        if(localData.contains(""+item.getId())){
              popupMenu.getMenu().getItem(3).setTitle(ct.getString(R.string.pop_remove_watchlist));
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                String tmdbUrl = "https://www.themoviedb.org/"+item.getMedia_type()+"/"+item.getId();
                if(menuItem.getItemId() == R.id.tmdb){
                    browserIntent.setData(Uri.parse(tmdbUrl));
                    ct.startActivity(browserIntent);
                }
                if(menuItem.getItemId() == R.id.share_facebook){
                    String url = "https://www.facebook.com/sharer/sharer.php?u="+tmdbUrl+"%2F&amp;src=sdkpreparse";
                    browserIntent.setData(Uri.parse(url));
                    ct.startActivity(browserIntent);
                }

                if(menuItem.getItemId() == R.id.share_tw){
                    String url = "https://twitter.com/intent/tweet?text="+tmdbUrl;
                    browserIntent.setData(Uri.parse(url));
                    ct.startActivity(browserIntent);
                }

                if(menuItem.getItemId() == R.id.action_watchlist){
                    if(localData.contains(""+item.getId())){
                        // remove
                        Toast.makeText(ct, item.getTitle()+" was removed from watchlist ", Toast.LENGTH_SHORT).show();
                        localData.remove(""+item.getId());
                        menuItem.setTitle(ct.getString(R.string.pop_add_to_watchlist));
                    }else {
                        Toast.makeText(ct, item.getTitle()+" was added to watchlist ", Toast.LENGTH_SHORT).show();
                        localData.putString(""+item.getId(),item);
                        menuItem.setTitle(ct.getString(R.string.pop_remove_watchlist));
                    }
                    localData.commit();
                }
                return true;
            }
        });
        // Showing the popup menu
        popupMenu.show();
    }



    // for add listener out of this class
    public  interface itemListener{
        void onClickItem(int position);
        void onPressItem(View anchor, int position);
    }

    private  itemListener iListener;
    public void setItemListener(itemListener listener){
        iListener = listener;
    }


}
