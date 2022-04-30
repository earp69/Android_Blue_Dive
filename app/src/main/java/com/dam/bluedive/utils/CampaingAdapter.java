package com.dam.bluedive.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dam.bluedive.R;
import com.dam.bluedive.model.Campania;

import java.util.ArrayList;

public class CampaingAdapter extends RecyclerView.Adapter<CampaingAdapter.CampaingVH>
        implements View.OnClickListener{

    private View.OnClickListener listener;
    private ArrayList<Campania> datos;


    public CampaingAdapter(ArrayList<Campania> datos) {
        this.datos = datos;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CampaingVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.campain_layout, parent, false);
        v.setOnClickListener(this);
        CampaingVH vh = new CampaingVH(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CampaingVH holder, int position) {
        holder.bindCampaing(datos.get(position));
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public class CampaingVH extends RecyclerView.ViewHolder {
        TextView tvT;
        ImageView ivImg;

        public CampaingVH(@NonNull View itemView) {
            super(itemView);
            tvT = itemView.findViewById(R.id.tvNombreCamp);
            ivImg = itemView.findViewById(R.id.ivFotoCamp);
        }

        public void bindCampaing(Campania camping) {
            tvT.setText(camping.getNombre());
            Glide.with(ivImg.getContext()).load(camping.getUrlFoto()).into(ivImg);
        }
    }
}
