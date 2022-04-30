package com.dam.bluedive.ui.events;

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
import com.dam.bluedive.R;
import com.dam.bluedive.model.Campania;
import com.dam.bluedive.model.Evento;
import com.dam.bluedive.ui.campaigns.CampaignViewFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class AddEvento extends Fragment implements View.OnClickListener{

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

    EventsFragment fragEvent;
    public AddEvento(){}
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();

                    /*Cargamos la imagen seleccionada,
                    guardando la ruta de la imagen en un atributo de tipo Uri,
                    selectedUri;
                    */
                    if (resultCode == -1 ) {
                        selectedUri = result.getData().getData();
                        Glide.with(ivImgCamp.getContext()).load(selectedUri)
                                .into(ivImgCamp);
                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_evento, container, false);

        dbRef = FirebaseDatabase.getInstance().getReference("eventos"); //Estructura de la base de datos
        mFotoStorageRef = FirebaseStorage.getInstance().getReference().child("fotosEventos");

        ivLogo = view.findViewById(R.id.iv_frag_ev_add_logo);
        etNombre = view.findViewById(R.id.et_frag_ev_add_nom);
        etObjetivo = view.findViewById(R.id.et_frag_ev_add_obj);
        etFecha = view.findViewById(R.id.et_frag_ev_add_fecha);
        etLugar = view.findViewById(R.id.et_frag_ev_add_lugar);
        tvAddImagen = view.findViewById(R.id.tv_frag_ev_add_imagen);
        ivImgCamp = view.findViewById(R.id.iv_frag_ev_add_imagen);
        btnAceptar = view.findViewById(R.id.btn_frag_ev_add_aceptar);
        btnCancelar = view.findViewById(R.id.btn_frag_ev_add_cancelar);

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
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            fragEvent = new EventsFragment();
            ft.replace(getId(), fragEvent);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void guardarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String objetivo = etObjetivo.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String lugar = etLugar.getText().toString().trim();
        if (nombre.isEmpty() || objetivo.isEmpty() || fecha.isEmpty() || lugar.isEmpty()) {
            Toast.makeText(getContext(),getString(R.string.msj_carga_ko),
                    Toast.LENGTH_SHORT).show();
        }else if (selectedUri == null){
            Toast.makeText(getContext(),getString(R.string.msj_carga_imagen_ko),
                Toast.LENGTH_SHORT).show();
        }else {
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
                //despues de importarla, al hacer clic, recupera de la tarea,
                // el url deo donde se ha almacenado la foto (storage)
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Evento evento = new Evento(nombre, objetivo, fecha, lugar, downloadUri.toString());
                        dbRef.child(nombre).setValue(evento);

                        /* añadimos un listener a la referencia donde hemos insertado el perfil*/
                        //esto iria en el fragment del adapter
                        //addDatabaseListener(nombre + "_" + fecha);
                    }
                }
            });
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            fragEvent = new EventsFragment();
            ft.replace(getId(), fragEvent);
            ft.addToBackStack(null);
            ft.commit();
        }
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