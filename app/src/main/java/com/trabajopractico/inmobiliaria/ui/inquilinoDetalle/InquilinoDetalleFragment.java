package com.trabajopractico.inmobiliaria.ui.inquilinoDetalle;

import android.os.Bundle;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.trabajopractico.inmobiliaria.databinding.FragmentInquilinoDetalleBinding;
import com.trabajopractico.inmobiliaria.modelo.Inquilino;

public class InquilinoDetalleFragment extends Fragment {

    private FragmentInquilinoDetalleBinding binding;
    private InquilinoDetalleViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInquilinoDetalleBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(InquilinoDetalleViewModel.class);

        vm.getInquilinoMutable().observe(getViewLifecycleOwner(), new Observer<Inquilino>() {
            @Override
            public void onChanged(Inquilino inquilino) {
                if (inquilino != null) {
                    binding.tvCodigoInquilino.setText(String.valueOf(inquilino.getIdInquilino()));
                    binding.tvNombreInquilino.setText(inquilino.getNombre());
                    binding.tvApellidoInquilino.setText(inquilino.getApellido());
                    binding.tvDniInquilino.setText(inquilino.getDni());
                    binding.tvEmailInquilino.setText(inquilino.getEmail());
                    binding.tvTelefonoInquilino.setText(inquilino.getTelefono());
                }
            }
        });

        vm.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        });

        vm.cargarInquilino(getArguments());
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}