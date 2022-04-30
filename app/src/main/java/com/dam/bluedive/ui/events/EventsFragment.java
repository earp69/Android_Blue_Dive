package com.dam.bluedive.ui.events;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.bluedive.R;
import com.dam.bluedive.model.Evento;
import com.dam.bluedive.utils.EventoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class EventsFragment extends Fragment implements View.OnClickListener{
    public static final String CLAVE_EVE = "EVE_SEL";

    FirebaseAuth fAuth;
    FirebaseUser fUser;

    StorageReference mFotoStorageRef;
    FirebaseDatabase fDB;
    DatabaseReference dbRef;
    ValueEventListener vel;

    RecyclerView rvEve;
    LinearLayoutManager llm;
    EventoAdapter adapter;
    FloatingActionButton fabAdd;
    Button btnBuscar;
    EditText etNombre;
    ArrayList<Evento> datosE;

    public EventsFragment(){}
    public View onCreateView( LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_evento_view, container, false);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        btnBuscar = root.findViewById(R.id.btnBuscarEvento);
        fabAdd = root.findViewById(R.id.add_fbtn);
        etNombre = root.findViewById(R.id.etNomEvento);
        rvEve = root.findViewById(R.id.rvEventos);
        llm = new LinearLayoutManager(getContext());
        fDB = FirebaseDatabase.getInstance();
        dbRef = fDB.getReference("eventos");
        mFotoStorageRef = FirebaseStorage.getInstance().getReference().child("fotosEventos");
        fabAdd.setOnClickListener(this);
        btnBuscar.setOnClickListener(this);
        datosE = new ArrayList<Evento>();
        rvEve.setLayoutManager(llm);
        addValueEventListener();
        return root;

    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private void cargarRV() {
        adapter = new EventoAdapter(datosE);
        adapter.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Evento ev = datosE.get(rvEve.getChildAdapterPosition(v));
                Bundle bundle = new Bundle();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                bundle.putParcelable(CLAVE_EVE,ev);
                EventosInfoFragment info = new EventosInfoFragment();
                info.setArguments(bundle);
                ft.replace(getId(),info);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        rvEve.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if(v == fabAdd){
           if(fUser != null) {
               Bundle bundle = new Bundle();
               FragmentTransaction ft = getParentFragmentManager().beginTransaction();
               AddEvento addE = new AddEvento();
               addE.setArguments(bundle);
               ft.replace(getId(), addE);
               ft.addToBackStack(null);
               ft.commit();
            }else{
                Toast.makeText(getContext(),getString(R.string.msj_no_logado2),
                        Toast.LENGTH_SHORT).show();
            }
        }else if(v == btnBuscar){
            if(etNombre.getText().toString().isEmpty()){
                Toast.makeText(getContext(), R.string.buscar_env, Toast.LENGTH_SHORT).show();
            }else {
                buscarPorLugar();
            }

        }
    }

    private void buscarPorLugar() {
        String lugar = etNombre.getText().toString().trim();
        Query q = dbRef.orderByChild("lugar").equalTo(lugar);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    Toast.makeText(getContext(),getString(R.string.msj_error_lugar),
                            Toast.LENGTH_SHORT).show();
                } else {
                Evento evento;
                datosE.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    evento = dataSnapshot.getValue(Evento.class);
                    datosE.add(evento);
                    Log.d("eventos", dataSnapshot.getKey() + " => " + dataSnapshot.getValue());
                }
                cargarRV();
              }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void addValueEventListener() {
        if (vel == null) {
            vel = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Evento ev;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        ev = child.getValue(Evento.class);
                        datosE.add(ev);
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