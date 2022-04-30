package com.dam.bluedive.ui.campaigns;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dam.bluedive.MyApplication;
import com.dam.bluedive.R;

import com.dam.bluedive.model.Campania;
import com.dam.bluedive.utils.CampaingAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CampaignViewFragment extends Fragment implements View.OnClickListener{
    public final static String CLAVE_CAM = "Campania";
    FirebaseAuth fAuth;
    FirebaseUser fUser;

    StorageReference mFotoStorageRef;
    FirebaseDatabase fDB;
    DatabaseReference dbRef;
    ValueEventListener vel;

    Button btnBuscar;
    Button btnAdd;
    RecyclerView rvCam;
    LinearLayoutManager llm;
    CampaingAdapter adapter;
    CampaignsAddFragment fragAdd;
    ArrayList<Campania> datosC;
    String lugarSelec;

    EditText lug;
    public CampaignViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_campaign_view, container, false);
        // Inflate the layout for this fragment
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        btnBuscar = v.findViewById(R.id.btnBuscarCamp);
        btnAdd = v.findViewById(R.id.btnAnadirCamp);
        rvCam = v.findViewById(R.id.rvCampanias);
        llm = new LinearLayoutManager(getContext());

        btnBuscar.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        fDB = FirebaseDatabase.getInstance();
        dbRef = fDB.getReference("campañas");
        mFotoStorageRef = FirebaseStorage.getInstance().getReference().child("fotosCampañas");

        datosC = new ArrayList<Campania>();

        rvCam.setLayoutManager(llm);

        addValueEventListener();

        return v;
    }

    private void cargarRV() {
        adapter = new CampaingAdapter(datosC);
        adapter.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Campania cam = datosC.get(rvCam.getChildAdapterPosition(view));
                Bundle bundle = new Bundle();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                bundle.putParcelable(CLAVE_CAM,cam);
                CampaingInfoFragment info = new CampaingInfoFragment();
                info.setArguments(bundle);
                ft.replace(getId(),info);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        rvCam.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnBuscar)){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_layout, null);
            lug = v.findViewById(R.id.et_dialog_lay_camp);
            builder.setView(v);
            builder.setTitle(R.string.title_dialog);
            builder.setPositiveButton(R.string.btn_aceptar,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String lugar = lug.getText().toString().trim();
                            ((MyApplication)getActivity().getApplicationContext()).setNombre(lugar);
                            dialog.dismiss();
                            buscarPorLugar();
                        }
                    });

            builder.setNegativeButton(R.string.btn_dialog_cancelar,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();

        } else if (view.equals(btnAdd)){
            if (fUser != null) {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                fragAdd = new CampaignsAddFragment();
                ft.replace(getId(), fragAdd);
                ft.addToBackStack(null);
                ft.commit();
            }else{
                Toast.makeText(getContext(),getString(R.string.msj_no_logado),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void buscarPorLugar() {
        lugarSelec = ((MyApplication)getActivity().getApplicationContext()).getNombre();
        if (lugarSelec.isEmpty()){
            Toast.makeText(getContext(),getString(R.string.msj_error_data),
                    Toast.LENGTH_SHORT).show();
        } else{
            Query q = dbRef.orderByChild("lugar").equalTo(lugarSelec);

            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() == null){
                        Toast.makeText(getContext(),getString(R.string.msj_error_lugar),
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Campania campania;
                        datosC.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            campania = dataSnapshot.getValue(Campania.class);
                            datosC.add(campania);
                            Log.d("campañas", dataSnapshot.getKey() + " => " + dataSnapshot.getValue());
                        }
                        cargarRV();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("LECTURA FIREBASE", "lectura cancelada",
                            error.toException());
                }
            });
        }
    }

    private void addValueEventListener() {
        if (vel == null) {
            vel = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Campania cam;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        cam = child.getValue(Campania.class);
                        datosC.add(cam);
                    }
                    cargarRV();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("LECTURA FIREBASE", "lectura cancelada",
                            error.toException());
                }
            };
        }
        dbRef.addValueEventListener(vel);
    }

}

