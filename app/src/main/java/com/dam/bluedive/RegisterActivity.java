package com.dam.bluedive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.bluedive.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etEmail;
    EditText etContrasena;
    EditText etContrasenaCom;
    EditText etNom;
    EditText etFechaNac;
    EditText etTel;
    Button btnCanc;
    Button btnAcept;

    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseDatabase fdb;
    DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fdb = FirebaseDatabase.getInstance();
        dbRef = fdb.getReference("usuarios");

        etEmail = findViewById(R.id.reg_Email);
        etContrasena = findViewById(R.id.reg_contrasena);
        etContrasenaCom = findViewById(R.id.reg_contrasena_comp);
        etNom = findViewById(R.id.reg_nombre);
        etFechaNac = findViewById(R.id.reg_fecha_nac);
        etTel = findViewById(R.id.reg_telefono);
        btnAcept = findViewById(R.id.btn_aceptar_reg);
        btnCanc = findViewById(R.id.btn_cancelar_reg);

        btnAcept.setOnClickListener(this);
        btnCanc.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnCanc)) {
            //Intent i = new Intent(RegisterActivity.this, MainActivity.class);
            //startActivity(i);
            finish();
        }
        else if (v.equals(btnAcept))
            recojerComponentes();

    }

    private void recojerComponentes() {
        String email = etEmail.getText().toString();
        String contrasena = etContrasena.getText().toString();
        String contrasenaComp = etContrasenaCom.getText().toString();
        String nombre = etNom.getText().toString();

        String fechaNac = etFechaNac.getText().toString();
        //TODO: controlar lo que quiera
        //if (String.format(fechaNac, ))

        String telefono = etTel.getText().toString();
        //TODO: controlar lo que quiera



        if (!email.isEmpty() & !contrasena.isEmpty() & !nombre.isEmpty() & !fechaNac.isEmpty()
                & !telefono.isEmpty()) {
            if (!email.contains("@") || !email.contains(".")) {
                email = "";
                etEmail.setText("");
                Toast.makeText(RegisterActivity.this,
                        getString(R.string.msj_email_no_correcto),
                        Toast.LENGTH_SHORT).show();

            }else if (!contrasena.equals(contrasenaComp) | contrasena.length() < 6) {
                contrasena = "";
                etContrasena.setText("");
                etContrasenaCom.setText("");
                Toast.makeText(RegisterActivity.this,
                        getString(R.string.msj_contrsena_comp),
                        Toast.LENGTH_SHORT).show();

            }else if (!nombre.trim().contains(" ")){
                nombre = "";
                etNom.setText("");

            }else if (fechaNac.length() < 8 | fechaNac.length() > 10 | !fechaNac.contains("/")){
                fechaNac = "";
                etFechaNac.setText("");
                Toast.makeText(RegisterActivity.this,
                        getString(R.string.msj_fechanac_comp),
                        Toast.LENGTH_SHORT).show();

            }else if (telefono.length() != 13 & telefono.length() != 9){
                telefono = "";
                etTel.setText("");
                Toast.makeText(RegisterActivity.this,
                        getString(R.string.msj_tel_comp),
                        Toast.LENGTH_SHORT).show();

            }else {
                Usuario user = new Usuario(nombre, fechaNac, telefono, email);
                registrarEC(email, contrasena, user);
            }

        }else {
            Toast.makeText(RegisterActivity.this,
                    getString(R.string.msj_campo_vacio),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void registrarEC(String email, String contrasena, Usuario user) {
        fAuth.createUserWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fUser = fAuth.getCurrentUser();
                            dbRef.child(user.getNombreCompleto()).setValue(user);
                            Toast.makeText(RegisterActivity.this,
                                    getString(R.string.msj_registrado),
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            //adFlags para que al dar atras no retroceda por todos los activity
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();

                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    getString(R.string.msj_no_registrado),
                                    Toast.LENGTH_SHORT).show();

                            etEmail.setText("");
                            etContrasena.setText("");
                            etContrasenaCom.setText("");
                            etNom.setText("");
                            etFechaNac.setText("");
                            etTel.setText("");

                        }
                    }
                });
    }
}