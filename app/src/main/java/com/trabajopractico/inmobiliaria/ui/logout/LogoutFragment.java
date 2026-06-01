package com.trabajopractico.inmobiliaria.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.trabajopractico.inmobiliaria.databinding.FragmentLogoutBinding;
import com.trabajopractico.inmobiliaria.ui.login.LoginActivity;

public class LogoutFragment extends Fragment {

    private FragmentLogoutBinding binding;
    private LogoutViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(LogoutViewModel.class);
        binding = FragmentLogoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observar el resultado del logout: si es true, navegar al Login
        vm.getCerrarSesion().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean cerrar) {
                if (cerrar != null && cerrar) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        // Mostrar el dialogo de confirmacion al abrir el fragment
        mostrarDialogo();

        return root;
    }

    private void mostrarDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cierre de sesión");
        builder.setMessage("¿Está seguro de que desea cerrar la sesión?");
        builder.setPositiveButton("ACEPTAR", (dialog, which) -> vm.cerrarSesion());
        builder.setNegativeButton("CANCELAR", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}