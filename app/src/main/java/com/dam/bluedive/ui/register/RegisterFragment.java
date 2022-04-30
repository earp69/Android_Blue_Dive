package com.dam.bluedive.ui.register;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.dam.bluedive.LoginActivity;
import com.dam.bluedive.MainActivity;
import com.dam.bluedive.MyApplication;
import com.dam.bluedive.R;
import com.dam.bluedive.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {

    //private FragmentHomeBinding binding;
    FirebaseAuth fAuth;
    FirebaseUser fUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        View view = inflater.inflate(R.layout.fragment_register,container,false);

        //TODO:Mostrar dialogo
        if (fUser != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setMessage(R.string.msj_regiter);
            builder.setPositiveButton(R.string.fd_btn_desconectar,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fAuth.signOut();
                            Intent i = new Intent(getContext(), LoginActivity.class);
                            startActivity(i);
                        }
                    });

            builder.setNegativeButton(R.string.fd_btn_cancelar,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getContext(), MainActivity.class);
                            startActivity(i);
                            dialog.cancel();
                        }
                    });
            //Para crear y visualizar el contenido del builder
            builder.create().show();
        }
        else{
            Intent i = new Intent(getActivity(), RegisterActivity.class);
            getActivity().startActivity(i);
        }
        return view;
    }

}