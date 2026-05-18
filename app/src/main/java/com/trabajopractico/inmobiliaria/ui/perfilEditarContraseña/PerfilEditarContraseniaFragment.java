package com.trabajopractico.inmobiliaria.ui.perfilEditarContraseña;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.trabajopractico.inmobiliaria.databinding.FragmentPerfilEditarContraseniaBinding;

public class PerfilEditarContraseniaFragment extends Fragment {

    private PerfilEditarContraseniaViewModel vm;
    private FragmentPerfilEditarContraseniaBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(PerfilEditarContraseniaViewModel.class);
        binding = FragmentPerfilEditarContraseniaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observar el resultado: si fue exitoso, volver al perfil
        vm.getCambioExitoso().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean exito) {
                if (exito != null && exito) {
                    // Volver al fragment anterior (Perfil)
                    Navigation.findNavController(requireView()).popBackStack();
                }
            }
        });

        // Boton Guardar: dispara el cambio de contrasenia
        binding.btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.cambiarContrasenia(
                        binding.etActual.getText().toString(),
                        binding.etNueva.getText().toString()
                );
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

