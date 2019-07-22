package com.wisata.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.wisata.R;
import com.wisata.models.*;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingHolder> {

    private Context mContext;
    private List<Wisata> ratingList;

    public class RatingHolder extends RecyclerView.ViewHolder {
        public TextView nama;
        public ImageView gambar;
        public RatingBar rating;

        public RatingHolder(View view) {
            super(view);
            nama = view.findViewById(R.id.nama);
            gambar = view.findViewById(R.id.gambar);
            rating = view.findViewById(R.id.rating);
        }
    }


    public RatingAdapter(Context mContext, List<Wisata> ratingList) {
        this.mContext = mContext;
        this.ratingList = ratingList;
    }

    @Override
    public RatingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rating, parent, false);

        return new RatingHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RatingHolder holder, int position) {
        Wisata rating = ratingList.get(position);
        holder.nama.setText(rating.getNama());
        holder.rating.setRating(rating.getRating());
        if(rating.getGambar() != null && !rating.getGambar().equals("")){
            Glide.with(mContext /* context */)
                    .using(new FirebaseImageLoader())
                    .load(rating.getGambar())
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
        return ratingList.size();
    }
}
