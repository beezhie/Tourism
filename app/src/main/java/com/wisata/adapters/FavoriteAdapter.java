package com.wisata.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.wisata.R;
import com.wisata.models.*;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder> {

    private Context mContext;
    private List<Wisata> favoriteList;
    private int lastPosition = -1;

    public class FavoriteHolder extends RecyclerView.ViewHolder {
        public TextView nama;
        public ImageView gambar;

        public FavoriteHolder(View view) {
            super(view);
            nama = view.findViewById(R.id.nama);
            gambar = view.findViewById(R.id.gambar);
        }
    }

    public FavoriteAdapter(){}
    public FavoriteAdapter(Context mContext, List<Wisata> favoriteList) {
        this.mContext = mContext;
        this.favoriteList = favoriteList;
    }

    @Override
    public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);

        return new FavoriteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FavoriteHolder holder, int position) {
        Wisata favorite = favoriteList.get(position);
        holder.nama.setText(favorite.getNama());
        if(favorite.getGambar() != null && !favorite.getGambar().equals("")){
            Glide.with(mContext /* context */)
                    .using(new FirebaseImageLoader())
                    .load(favorite.getGambar())
                    .into(holder.gambar);
        }
        int last = -1;
        if(position > last){
            Animation anim = AnimationUtils.loadAnimation(mContext,R.anim.bounce);
            holder.itemView.setAnimation(anim);
            last = position;
        }
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public void hapus(int posisi){
        favoriteList.remove(posisi);
        this.notifyDataSetChanged();
    }
}
