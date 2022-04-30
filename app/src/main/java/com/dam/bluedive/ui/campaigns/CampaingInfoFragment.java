package com.dam.bluedive.ui.campaigns;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dam.bluedive.R;
import com.dam.bluedive.model.Campania;
import com.dam.bluedive.ui.register.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CampaingInfoFragment extends Fragment implements View.OnClickListener{
    FirebaseAuth fAuth;
    FirebaseUser fUser;

    ImageView ivImgInfo;
    TextView tvNInfo;
    TextView tvOInfo;
    TextView tvFLInfo;
    RegisterFragment fragRegist;
    Button btnApuntarte;

    public CampaingInfoFragment() {} //Requerido

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_campaing_info, container, false);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        Bundle bundle = this.getArguments();

        Campania cam = bundle.getParcelable(CampaignViewFragment.CLAVE_CAM);

        ivImgInfo = v.findViewById(R.id.iv_frag_camp_info_imagen);
        tvNInfo = v.findViewById(R.id.tv_info_nombre_camp);
        tvOInfo = v.findViewById(R.id.tv_info_obj_camp);
        tvFLInfo = v.findViewById(R.id.tv_info_fecha_camp);
        btnApuntarte = v.findViewById(R.id.btn_info_apunt);

        btnApuntarte.setOnClickListener(this);

        Glide.with(ivImgInfo.getContext()).load(cam.getUrlFoto()).into(ivImgInfo);
        tvNInfo.setText(cam.getNombre());
        tvOInfo.setText(cam.getObjetivo());
        tvFLInfo.setText(cam.getLugar() + " " + cam.getFecha());

        return v;
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
            String apunt = String.format(getResources().getString(R.string.apuntarse), tvNInfo.getText().toString());
            Toast.makeText(getContext(), apunt, Toast.LENGTH_SHORT).show();

        }
    }
}