package com.wisata.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wisata.R;
import com.wisata.models.*;

import java.util.List;

public class TerdekatAdapter extends RecyclerView.Adapter<TerdekatAdapter.TerdekatHolder> {

    private final List<Wisata> terdekatList;

    public class TerdekatHolder extends RecyclerView.ViewHolder {
        public TextView nama,jarak;

        public TerdekatHolder(View view) {
            super(view);
            nama = view.findViewById(R.id.nama_wisata);
            jarak = view.findViewById(R.id.jarak);
        }
    }


    public TerdekatAdapter(List<Wisata> terdekatList) {
        this.terdekatList = terdekatList;
    }

    @Override
    public TerdekatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_terdekat, parent, false);

        return new TerdekatHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TerdekatHolder holder, int position) {
        Wisata terdekat = terdekatList.get(position);
        holder.nama.setText(terdekat.getNama());
        holder.jarak.setText(terdekat.getJarak() +" Km");
    }

    @Override
    public int getItemCount() {
        return terdekatList.size();
    }
}
