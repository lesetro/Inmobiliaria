package com.trabajopractico.inmobiliaria.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.trabajopractico.inmobiliaria.databinding.FragmentPerfilBinding;
import com.trabajopractico.inmobiliaria.modelo.Propietario;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar ViewModel
        vm = new ViewModelProvider(this).get(PerfilViewModel.class);
        // Inflar el binding
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observar cambios en el Propietario
        vm.getPropietarioMutable().observe(getViewLifecycleOwner(), new Observer<Propietario>() {
            @Override
            public void onChanged(Propietario propietario) {
                if (propietario != null) {
                    binding.etCodigo.setText(String.valueOf(propietario.getIdPropietario()));
                    binding.etDni.setText(propietario.getDni());
                    binding.etNombre.setText(propietario.getNombre());
                    binding.etApellido.setText(propietario.getApellido());
                    binding.etEmail.setText(propietario.getEmail());
                    binding.etTelefono.setText(propietario.getTelefono());
                }
            }
        });

        // Cargar el perfil
        vm.cargarPerfil();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}