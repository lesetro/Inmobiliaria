package com.trabajopractico.inmobiliaria.ui.contratoDetalle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.trabajopractico.inmobiliaria.R;
import com.trabajopractico.inmobiliaria.databinding.FragmentContratoDetalleBinding;
import com.trabajopractico.inmobiliaria.modelo.Contrato;
import com.trabajopractico.inmobiliaria.modelo.Inmueble;

import java.text.NumberFormat;
import java.util.Locale;

public class ContratoDetalleFragment extends Fragment {

    private FragmentContratoDetalleBinding binding;
    private ContratoDetalleViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentContratoDetalleBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(ContratoDetalleViewModel.class);

        // Observer de la direccion
        vm.getDireccionInmuebleMutable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String direccion) {
                binding.tvInmuebleDireccion.setText(direccion);
            }
        });

        // Observer del contrato
        vm.getContratoMutable().observe(getViewLifecycleOwner(), new Observer<Contrato>() {
            @Override
            public void onChanged(Contrato contrato) {
                if (contrato != null) {
                    binding.tvCodigoContrato.setText(String.valueOf(contrato.getIdContrato()));
                    binding.tvFechaInicio.setText(contrato.getFechaInicio());
                    binding.tvFechaFin.setText(contrato.getFechaFinalizacion());
                }
            }
        });

        // Observer del monto formateado
        vm.getMontoAlquilerMutable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String monto) {
                binding.tvMontoAlquiler.setText(monto);
            }
        });

        // Observer del nombre del inquilino
        vm.getNombreInquilinoMutable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String nombre) {
                binding.tvInquilinoNombre.setText(nombre);
            }
        });

        // Observer del estado del boton PAGOS
        vm.getBotonPagosHabilitado().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean habilitado) {
                binding.btnPagos.setEnabled(habilitado != null && habilitado);
            }
        });

        // Observer de navegacion: cuando el VM emite un id, navega a Pagos
        vm.getIdContratoParaNavegar().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer idContrato) {
                if (idContrato == null) return;
                Bundle bundle = new Bundle();
                bundle.putInt("idContrato", idContrato);
                Navigation.findNavController(requireView()).navigate(
                        R.id.action_contratoDetalleFragment_to_pagosFragment, bundle);
                vm.resetIdContratoNavegacion();
            }
        });

        // Boton PAGOS: solo avisa al VM
        binding.btnPagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.solicitarNavegacionAPagos();
            }
        });

        vm.cargarContrato(getArguments());
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}