package com.wisata.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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

public class RekomendasiAdapter extends RecyclerView.Adapter<RekomendasiAdapter.RekomendasiHolder> {

    private final Context mContext;
    private final List<Wisata> ratingList;

    public class RekomendasiHolder extends RecyclerView.ViewHolder {
        public TextView nama;
        public ImageView gambar;

        public RekomendasiHolder(View view) {
            super(view);
            nama = view.findViewById(R.id.nama);
            gambar = view.findViewById(R.id.gambar);
        }
    }


    public RekomendasiAdapter(Context mContext, List<Wisata> ratingList) {
        this.mContext = mContext;
        this.ratingList = ratingList;
    }

    @Override
    public RekomendasiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rekomendasi, parent, false);

        return new RekomendasiHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RekomendasiHolder holder, int position) {
        Wisata rating = ratingList.get(position);
        holder.nama.setText(rating.getNama());
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
