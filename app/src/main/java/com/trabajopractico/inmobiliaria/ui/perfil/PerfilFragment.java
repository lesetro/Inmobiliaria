package com.trabajopractico.inmobiliaria.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private boolean modoEdicion = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar ViewModel
        vm = new ViewModelProvider(this).get(PerfilViewModel.class);
        // Inflar el binding
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // El boton arranca deshabilitado
        // Se habilita cuando lleguen los datos del perfil
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
        // Boton EDITAR / GUARDAR
        binding.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!modoEdicion) {
                    // Pasar a modo edicion: habilitar campos (menos etCodigo)
                    binding.etDni.setEnabled(true);
                    binding.etNombre.setEnabled(true);
                    binding.etApellido.setEnabled(true);
                    binding.etEmail.setEnabled(true);
                    binding.etTelefono.setEnabled(true);
                    binding.btnEditar.setText("GUARDAR");
                    modoEdicion = true;
                } else {
                    // Guardar: llamar al VM con los 5 strings del binding
                    vm.actualizarPropietario(
                            binding.etNombre.getText().toString(),
                            binding.etApellido.getText().toString(),
                            binding.etDni.getText().toString(),
                            binding.etTelefono.getText().toString(),
                            binding.etEmail.getText().toString()
                    );
                    // Volver a modo lectura
                    binding.etDni.setEnabled(false);
                    binding.etNombre.setEnabled(false);
                    binding.etApellido.setEnabled(false);
                    binding.etEmail.setEnabled(false);
                    binding.etTelefono.setEnabled(false);
                    binding.btnEditar.setText("EDITAR");
                    modoEdicion = false;
                }
            }
        });
        // Boton CAMBIAR CONTRASEÑA: navega al fragment de cambiar contrasenia
        binding.btnCambiarContrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.perfilEditarContraseniaFragment);
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