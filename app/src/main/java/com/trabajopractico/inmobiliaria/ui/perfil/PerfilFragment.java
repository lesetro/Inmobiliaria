package com.trabajopractico.inmobiliaria.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.trabajopractico.inmobiliaria.R;
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

        // El boton arranca deshabilitado, se habilita cuando lleguen los datos
        binding.btnEditar.setEnabled(false);

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
                    binding.btnEditar.setEnabled(true);
                }
            }
        });
        // Observar cambios en el modo edicion: habilita/deshabilita campos y cambia texto del boton
        vm.getModoEdicionMutable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean modoEdicion) {
                if (modoEdicion == null) return;
                boolean habilitar = modoEdicion;
                binding.etDni.setEnabled(habilitar);
                binding.etNombre.setEnabled(habilitar);
                binding.etApellido.setEnabled(habilitar);
                binding.etEmail.setEnabled(habilitar);
                binding.etTelefono.setEnabled(habilitar);
                binding.btnEditar.setText(habilitar ? "GUARDAR" : "EDITAR");
            }
        });

        // Boton EDITAR / GUARDAR: el VM decide que hacer segun el estado actual
        binding.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.alternarModoEdicion(
                        binding.etNombre.getText().toString(),
                        binding.etApellido.getText().toString(),
                        binding.etDni.getText().toString(),
                        binding.etTelefono.getText().toString(),
                        binding.etEmail.getText().toString()
                );
            }
        });

        // Boton CAMBIAR CONTRASEÑA: navega al fragment de cambiar contraseña
        binding.btnCambiarContrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.perfilEditarContraseniaFragment);
            }
        });

        vm.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        });

        vm.cargarPerfil();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}