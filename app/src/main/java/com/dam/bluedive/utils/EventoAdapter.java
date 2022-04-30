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
import com.dam.bluedive.model.Evento;
import java.util.ArrayList;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EveVH> implements View.OnClickListener{


    private ArrayList<Evento> datos;
    private View.OnClickListener listener;

    public EventoAdapter(ArrayList<Evento> datos) {
        this.datos = datos;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener != null) {
            listener.onClick(v);
        }
    }

    @NonNull
    @Override
    public EveVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.evento_layout, parent, false);
        v.setOnClickListener(this);
        EveVH vh = new EveVH(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull EveVH holder, int position) {
        holder.bindEvento(datos.get(position));
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public static class EveVH extends RecyclerView.ViewHolder{

        ImageView ivFoto;
        TextView tvTitulo;
        public EveVH(@NonNull View itemView) {
            super(itemView);
            ivFoto = (ImageView) itemView.findViewById(R.id.ivFotoEvent);
            tvTitulo = itemView.findViewById(R.id.tvNombreEvent);
        }
        public void bindEvento(Evento eve){
            tvTitulo.setText(eve.getNombre());
            Glide.with(ivFoto.getContext()).load(eve.getUrlFoto()).into(ivFoto);
        }
    }
}
