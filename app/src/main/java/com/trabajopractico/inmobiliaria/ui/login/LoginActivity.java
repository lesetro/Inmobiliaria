package com.trabajopractico.inmobiliaria.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.trabajopractico.inmobiliaria.MainActivity;
import com.trabajopractico.inmobiliaria.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(LoginViewModel.class);

        // Observar mensajes (errores o info) -> Toast
        vm.getMensaje().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String msg) {
                if (msg != null && !msg.isEmpty()) {
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observar el token: cuando llega un token valido, navegar al MainActivity
        vm.getToken().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String token) {
                if (token != null && !token.isEmpty()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // Observar el loading (para mostrar/ocultar progress si en algun momento
        // se agrega un ProgressBar al layout)
        vm.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                // aca se hace setVisibility(loading ? VISIBLE : GONE).
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = binding.etUsuario.getText().toString();
                String clave = binding.etContrasena.getText().toString();
                vm.login(usuario, clave);
            }
        });

    }
}