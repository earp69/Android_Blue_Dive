package com.dam.bluedive.ui.events;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dam.bluedive.MyApplication;
import com.dam.bluedive.R;
import com.dam.bluedive.model.Evento;
import com.dam.bluedive.ui.campaigns.CampaignsAddFragment;
import com.dam.bluedive.ui.register.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EventosInfoFragment extends Fragment implements View.OnClickListener{
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    TextView nombre;
    TextView objeto;
    TextView fechaLugar;
    ImageView imgInfo;
    Button btnApuntarte;

    RegisterFragment fragRegist;

    public EventosInfoFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_eventos_info, container, false);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        nombre = root.findViewById(R.id.tv_info_nombre_ev);
        objeto = root.findViewById(R.id.tv_info_obj_ev);
        fechaLugar = root.findViewById(R.id.tv_info_fecha_ev_lugar);
        imgInfo = root.findViewById(R.id.iv_frag_ev_info_imagen);
        btnApuntarte = root.findViewById(R.id.btn_info_apunt);
        btnApuntarte.setOnClickListener(this);
        Bundle bundle = this.getArguments();

        Evento eve = bundle.getParcelable(EventsFragment.CLAVE_EVE);
        nombre.setText(eve.getNombre() + ":");
        objeto.setText(eve.getObjetivo());
        fechaLugar.setText(eve.getLugar() + "  " + eve.getFecha());

        Glide.with(imgInfo.getContext()).load(eve.getUrlFoto()).into(imgInfo);

        return root;
    }

    @Override
    public void onClick(View view) {
        if(fUser == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.title_dialog2);
            builder.setPositiveButton(R.string.btn_si,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                            fragRegist = new RegisterFragment();
                            ft.replace(getId(), fragRegist);
                            ft.addToBackStack(null);
                            ft.commit();

                        }
                    });

            builder.setNegativeButton(R.string.btn_no,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        }else{
            String apunt = String.format(getResources().getString(R.string.apuntarse), nombre.getText().toString());
            Toast.makeText(getContext(), apunt, Toast.LENGTH_SHORT).show();

        }
    }
}