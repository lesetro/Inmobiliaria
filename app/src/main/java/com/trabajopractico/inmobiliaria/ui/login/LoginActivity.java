package com.trabajopractico.inmobiliaria.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.trabajopractico.inmobiliaria.MainActivity;
import com.trabajopractico.inmobiliaria.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "LoginActivity";
    private static final int CODIGO_PERMISO_LLAMADA = 100;
    private static final String NUMERO_INMOBILIARIA = "2664553747";
    private static final float UMBRAL_SHAKE = 12.0f;

    private ActivityLoginBinding binding;
    private LoginViewModel vm;

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private long ultimoShake = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(LoginViewModel.class);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        vm.getMensaje().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String msg) {
                if (msg != null && !msg.isEmpty()) {
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                // Reservado
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

        binding.btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.resetearContrasenia();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarYSolicitarPermiso();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float aceleracion = (float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

        if (aceleracion > UMBRAL_SHAKE) {
            long ahora = System.currentTimeMillis();
            if (ahora - ultimoShake > 2000) {
                ultimoShake = ahora;
                Log.d(TAG, "Shake detectado — iniciando llamada");
                realizarLlamada();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se necesita implementación
    }

    private void realizarLlamada() {
        if (tienePermisoLlamada()) {
            Intent intentLlamada = new Intent(Intent.ACTION_CALL);
            intentLlamada.setData(Uri.parse("tel:" + NUMERO_INMOBILIARIA));
            intentLlamada.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intentLlamada);
            } catch (SecurityException e) {
                Log.e(TAG, "Error: No se tiene permiso para realizar llamadas");
                Toast.makeText(this, "Se necesita permiso para llamar", Toast.LENGTH_SHORT).show();
            }
        } else {
            verificarYSolicitarPermiso();
        }
    }

    private boolean tienePermisoLlamada() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void verificarYSolicitarPermiso() {
        if (!tienePermisoLlamada()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                mostrarDialogoExplicacion();
            } else {
                solicitarPermisoLlamada();
            }
            Log.d(TAG, "Solicitando permiso de llamada");
        }
    }

    private void mostrarDialogoExplicacion() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso necesario")
                .setMessage("La app necesita permiso de llamadas para contactar a la inmobiliaria al agitar el teléfono.")
                .setPositiveButton("Dar permiso", (dialog, which) -> solicitarPermisoLlamada())
                .setNegativeButton("Cancelar", (dialog, which) ->
                        Toast.makeText(this, "La app no podrá llamar sin el permiso", Toast.LENGTH_LONG).show())
                .setCancelable(false)
                .show();
    }

    private void solicitarPermisoLlamada() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CALL_PHONE},
                CODIGO_PERMISO_LLAMADA
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PERMISO_LLAMADA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de llamada concedido", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Permiso de llamada concedido");
            } else {
                Toast.makeText(this, "La app necesita el permiso para llamar", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Permiso de llamada denegado");
            }
        }
    }
}
