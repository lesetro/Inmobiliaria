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

        // Cuando el reset es exitoso, cambiamos el texto del boton para dar feedback visual
        vm.getResetExitoso().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean exito) {
                if (exito != null && exito) {
                    binding.btnRecuperar.setText("Contraseña restablecida");
                    binding.btnRecuperar.setEnabled(false);
                }
            }
        });

        vm.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                // Reservado para ProgressBar
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

        // "¿Olvidaste tu contraseña?": llama al endpoint de reseteo .
        binding.btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.resetearContrasenia();
            }
        });

    }
}