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

public class WisataAdapter extends RecyclerView.Adapter<WisataAdapter.WisataHolder> {

    private final Context mContext;
    private final List<Wisata> wisataList;
    private final int lastPosition = -1;

    public class WisataHolder extends RecyclerView.ViewHolder {
        public TextView nama;
        public ImageView gambar;

        public WisataHolder(View view) {
            super(view);
            nama = view.findViewById(R.id.nama);
            gambar = view.findViewById(R.id.gambar);
        }
    }


    public WisataAdapter(Context mContext, List<Wisata> wisataList) {
        this.mContext = mContext;
        this.wisataList = wisataList;
    }

    @Override
    public WisataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wisata, parent, false);

        return new WisataHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final WisataHolder holder, int position) {
        Wisata wisata = wisataList.get(position);
        holder.nama.setText(wisata.getNama());
        if(wisata.getGambar() != null && !wisata.getGambar().equals("")){
            Glide.with(mContext /* context */)
                    .using(new FirebaseImageLoader())
                    .load(wisata.getGambar())
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
        return wisataList.size();
    }
}
