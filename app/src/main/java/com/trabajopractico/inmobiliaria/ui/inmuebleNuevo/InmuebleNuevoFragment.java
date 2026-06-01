package com.trabajopractico.inmobiliaria.ui.inmuebleNuevo;

import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.trabajopractico.inmobiliaria.databinding.FragmentInmuebleNuevoBinding;

public class InmuebleNuevoFragment extends Fragment {

    private FragmentInmuebleNuevoBinding binding;
    private InmuebleNuevoViewModel vm;

    // Atributos para el manejo de la galeria.
    // Van como atributos del Fragment (no del VM) porque son cosas de UI
    // que necesitan persistir mientras el fragment vive.
    private Intent intent;
    private ActivityResultLauncher<Intent> selector;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInmuebleNuevoBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(InmuebleNuevoViewModel.class);

        // Observador del Uri: cuando se elige una imagen, mostrarla en el ImageView
        vm.getmUri().observe(getViewLifecycleOwner(), new Observer<android.net.Uri>() {
            @Override
            public void onChanged(android.net.Uri uri) {
                binding.ivFoto.setImageURI(uri);
            }
        });

        // Observador: cuando el inmueble se crea exitosamente, volver al listado
        vm.getInmuebleCreado().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean creado) {
                if (creado != null && creado) {
                    Navigation.findNavController(requireView()).popBackStack();
                }
            }
        });

        vm.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        });

        // Boton CARGAR IMAGEN: abre la galeria del celular
        binding.btCargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selector.launch(intent);
            }
        });

        // Boton GUARDAR: manda el inmueble + imagen al backend
        binding.btnGuardarInmueble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.util.Log.d("INMOBILE", "BOTON GUARDAR TOCADO");
                vm.cargarInmueble(
                        binding.etDireccion.getText().toString(),
                        binding.spUso.getSelectedItem().toString(),
                        binding.spTipo.getSelectedItem().toString(),
                        binding.etAmbientes.getText().toString(),
                        binding.etSuperficie.getText().toString(),
                        binding.etValor.getText().toString()
                );
            }
        });

        // Registrar el launcher de la galeria
        abrirGaleria();

        return binding.getRoot();
    }

    // Configura el intent y el launcher para abrir la galeria.
    // No abre la galeria todavia, solo deja todo registrado para cuando el usuario
    // toque el boton CARGAR IMAGEN.
    private void abrirGaleria() {
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selector = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult resultado) {
                        vm.recibirFoto(resultado);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}