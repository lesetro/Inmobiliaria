package com.trabajopractico.inmobiliaria;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.View;
import android.widget.TextView;

import com.trabajopractico.inmobiliaria.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String CANAL_ID = "pagos_canal";
    private static final int NOTIF_ID = 1;
    private static final int CODIGO_PERMISO_NOTIFICACION = 101;

    private ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private MainViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio,
                R.id.nav_contratos,
                R.id.nav_inmuebles,
                R.id.nav_logout,
                R.id.nav_inquilinos,
                R.id.nav_perfil
        )
                .setOpenableLayout(binding.drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        vm = new ViewModelProvider(this).get(MainViewModel.class);

        // Observar primera apertura: muestra pagos existentes
        vm.getConteoInicial().observe(this, total -> mostrarNotificacionPrimera(total));

        // Observar pagos nuevos desde la última vez
        vm.getPagosNuevos().observe(this, nuevos -> mostrarNotificacion(nuevos));

        // Observar perfil y actualizar el header del drawer
        View header = binding.navView.getHeaderView(0);
        TextView tvNombre = header.findViewById(R.id.tvNombreHeader);
        TextView tvEmail = header.findViewById(R.id.tvEmailHeader);

        vm.getPropietario().observe(this, propietario -> {
            if (propietario != null) {
                tvNombre.setText(propietario.getNombre() + " " + propietario.getApellido());
                tvEmail.setText(propietario.getEmail());
            }
        });

        vm.cargarPerfil();

        crearCanalNotificacion();
        verificarPermisoNotificacion();
    }

    //  CANAL DE NOTIFICACIONES -

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CANAL_ID,
                    "Notificaciones de Pagos",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            canal.setDescription("Notifica cuando se registran nuevos pagos de alquiler");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(canal);
        }
    }

    //  PERMISO POST_NOTIFICATIONS

    private boolean tienePermisoNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void verificarPermisoNotificacion() {
        if (!tienePermisoNotificacion()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                mostrarDialogoNotificaciones();
            } else {
                solicitarPermisoNotificacion();
            }
        } else {
            vm.verificarNuevosPagos();
        }
    }

    private void mostrarDialogoNotificaciones() {
        new AlertDialog.Builder(this)
                .setTitle("Notificaciones de pagos")
                .setMessage("¿Desea recibir notificaciones cuando se registren nuevos pagos en sus contratos?")
                .setPositiveButton("Sí, recibir", (dialog, which) -> solicitarPermisoNotificacion())
                .setNegativeButton("No gracias", null)
                .setCancelable(false)
                .show();
    }

    private void solicitarPermisoNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    CODIGO_PERMISO_NOTIFICACION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PERMISO_NOTIFICACION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permiso de notificaciones concedido");
                vm.verificarNuevosPagos();
            } else {
                Log.w(TAG, "Permiso de notificaciones denegado");
            }
        }
    }

    //  MOSTRAR NOTIFICACIONES

    private void mostrarNotificacionPrimera(int total) {
        String mensaje = total == 1
                ? "Tiene 1 pago registrado en su contrato"
                : "Tiene " + total + " pagos registrados en sus contratos";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CANAL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Pagos de alquiler")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIF_ID, builder.build());
        Log.d(TAG, "Notificación inicial: " + mensaje);
    }

    private void mostrarNotificacion(int nuevos) {
        String mensaje = nuevos == 1
                ? "Se registró 1 nuevo pago en su contrato"
                : "Se registraron " + nuevos + " nuevos pagos en sus contratos";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CANAL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Nuevo pago de alquiler")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIF_ID, builder.build());
        Log.d(TAG, "Notificación: " + mensaje);
    }

    // --- NAVEGACIÓN ---

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
