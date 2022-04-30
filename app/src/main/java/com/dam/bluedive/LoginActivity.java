package com.dam.bluedive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FrameLayout fLContenedor;

    EditText etEmail;
    EditText etPass;
    Button btnAcceder;
    TextView tvActLogRegister;
    TextView tvActLogInvitado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Widget_AppCompat_ActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fLContenedor = findViewById(R.id.fLActLoginContenedor);

        etEmail = findViewById(R.id.etActLogUser);
        etPass = findViewById(R.id.etActLogPass);
        btnAcceder = findViewById(R.id.btnActLogAcceder);
        tvActLogRegister = findViewById(R.id.tvActLogRegister);
        tvActLogInvitado = findViewById(R.id.tvActLogInvitado);

        btnAcceder.setOnClickListener(this);
        tvActLogRegister.setOnClickListener(this);
        tvActLogInvitado.setOnClickListener(this);

        fUser = fAuth.getCurrentUser();
        if (fUser != null) {
            enviarIntentAMain();

        }
        getSupportActionBar().hide();//para que el splash termine
    }

    private void enviarIntentAMain() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra("EMAIL", fUser.getEmail());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnAcceder))
            acceder();
        else if (view.equals(tvActLogRegister))
            registrar();
        else if (view.equals(tvActLogInvitado))
            irAMain();

    }

    private void irAMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void registrar() {
        Intent i = new Intent(this, RegisterActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void acceder() {
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()){
            //Similar al Toast
            Toast.makeText(LoginActivity.this,getString(R.string.msj_no_hay_datos),
                    Toast.LENGTH_SHORT).show();
        } else{
            fAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                fUser = fAuth.getCurrentUser();
                                enviarIntentAMain();
                            } else {
                                Toast.makeText(LoginActivity.this,getString(R.string.msj_no_accede),
                                        Toast.LENGTH_SHORT).show();
                            } } });
        }
    }
}