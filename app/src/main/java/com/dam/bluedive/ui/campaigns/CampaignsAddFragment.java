package com.dam.bluedive.ui.campaigns;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dam.bluedive.LoginActivity;
import com.dam.bluedive.R;
import com.dam.bluedive.model.Campania;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CampaignsAddFragment extends Fragment implements View.OnClickListener{

    StorageReference mFotoStorageRef;
    DatabaseReference dbRef;
    ValueEventListener vEL;
    Uri selectedUri;

    ImageView ivLogo;
    EditText etNombre;
    EditText etObjetivo;
    EditText etFecha;
    EditText etLugar;
    TextView tvAddImagen;
    ImageView ivImgCamp;
    Button btnCancelar;
    Button btnAceptar;

    CampaignViewFragment fragCamp;

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();

                    if (resultCode == -1 ) {
                        selectedUri = result.getData().getData();
                        Glide.with(ivImgCamp.getContext()).load(selectedUri)
                                .into(ivImgCamp);
                    }
                }
            });

    public CampaignsAddFragment() {} //Requerido

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_campaigns_add, container, false);

        dbRef = FirebaseDatabase.getInstance().getReference("campañas"); //Estructura de la base de datos
        mFotoStorageRef = FirebaseStorage.getInstance().getReference().child("fotosCampañas");

        ivLogo = view.findViewById(R.id.iv_frag_campg_add_logo);
        etNombre = view.findViewById(R.id.et_frag_camp_add_nom);
        etObjetivo = view.findViewById(R.id.et_frag_camp_add_obj);
        etFecha = view.findViewById(R.id.et_frag_camp_add_fecha);
        etLugar = view.findViewById(R.id.et_frag_camp_add_lugar);
        tvAddImagen = view.findViewById(R.id.tv_frag_camp_add_imagen);
        ivImgCamp = view.findViewById(R.id.iv_frag_camp_add_imagen);
        btnAceptar = view.findViewById(R.id.btn_frag_camp_add_aceptar);
        btnCancelar = view.findViewById(R.id.btn_frag_camp_add_cancelar);

        tvAddImagen.setOnClickListener(this);
        btnAceptar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.equals(tvAddImagen)){
            tvAddImagen.setTextColor(getResources().getColor(R.color.RED));
            /*abrirá un selector de archivos para ayudarnos a elegir entre cualquier imagen JPEG almacenada localmente en el dispositivo */
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startForResult.launch(Intent.createChooser(intent, getString(R.string.title)));

        }else if (view.equals(btnAceptar)){
            guardarDatos();

        }else{
            cerrarFragment();
        }
    }

    private void guardarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String objetivo = etObjetivo.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String lugar = etLugar.getText().toString().trim();

        if (nombre.isEmpty() || objetivo.isEmpty() || fecha.isEmpty() || lugar.isEmpty()){
            Toast.makeText(getContext(),getString(R.string.msj_carga_ko),
                    Toast.LENGTH_SHORT).show();
        }else if (selectedUri == null){
            Toast.makeText(getContext(),getString(R.string.msj_carga_imagen_ko),
                    Toast.LENGTH_SHORT).show();
        } else {
            final StorageReference fotoRef = mFotoStorageRef.child(selectedUri.getEncodedPath());//Desendemos hasta la ubicacion del fichero
            UploadTask ut = fotoRef.putFile(selectedUri);//Tarea de subida de imagen
            Task<Uri> urlTask = ut.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fotoRef.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Campania campania = new Campania(nombre, objetivo, fecha, lugar, downloadUri.toString());
                        dbRef.child(nombre + "_" + lugar).setValue(campania);
                        Toast.makeText(getContext(), getString(R.string.msj_carga_ok),
                                Toast.LENGTH_SHORT).show();
                        cerrarFragment();
                    }
                }
            });
        }
    }

    private void cerrarFragment() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        fragCamp = new CampaignViewFragment();
        ft.replace(getId(), fragCamp);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeDatabaseListener();
    }

    private void removeDatabaseListener() {
        if (vEL != null) {
            dbRef.removeEventListener(vEL);
            vEL = null;
        }
    }
}