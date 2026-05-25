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

        // Observer del inmueble: muestra la direccion en el detalle
        vm.getInmuebleMutable().observe(getViewLifecycleOwner(), new Observer<Inmueble>() {
            @Override
            public void onChanged(Inmueble inmueble) {
                if (inmueble != null) {
                    binding.tvInmuebleDireccion.setText("Inmueble en " + inmueble.getDireccion());
                }
            }
        });

        // Observer del contrato: llena todos los campos
        vm.getContratoMutable().observe(getViewLifecycleOwner(), new Observer<Contrato>() {
            @Override
            public void onChanged(Contrato contrato) {
                if (contrato != null) {
                    binding.tvCodigoContrato.setText(String.valueOf(contrato.getIdContrato()));
                    binding.tvFechaInicio.setText(contrato.getFechaInicio());
                    binding.tvFechaFin.setText(contrato.getFechaFinalizacion());

                    NumberFormat nf = NumberFormat.getInstance(new Locale("es", "AR"));
                    binding.tvMontoAlquiler.setText("$ " + nf.format(contrato.getMontoAlquiler()));

                    if (contrato.getInquilino() != null) {
                        String nombreCompleto = contrato.getInquilino().getNombre() + " " +
                                contrato.getInquilino().getApellido();
                        binding.tvInquilinoNombre.setText(nombreCompleto);
                    }

                    binding.btnPagos.setEnabled(true);
                }
            }
        });

        // Boton PAGOS: navega al fragment de pagos pasando el idContrato
        binding.btnPagos.setEnabled(false);
        binding.btnPagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contrato contrato = vm.getContratoMutable().getValue();
                if (contrato == null) return;

                Bundle bundle = new Bundle();
                bundle.putInt("idContrato", contrato.getIdContrato());
                Navigation.findNavController(v).navigate(
                        R.id.action_contratoDetalleFragment_to_pagosFragment, bundle);
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