package com.trabajopractico.inmobiliaria.ui.pagos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.trabajopractico.inmobiliaria.databinding.FragmentPagosBinding;
import com.trabajopractico.inmobiliaria.modelo.Pago;

import java.util.List;

public class PagosFragment extends Fragment {

    private FragmentPagosBinding binding;
    private PagosViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPagosBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(PagosViewModel.class);

        vm.getListaPagos().observe(getViewLifecycleOwner(), new Observer<List<Pago>>() {
            @Override
            public void onChanged(List<Pago> pagos) {
                PagoAdapter adapter = new PagoAdapter(pagos, getLayoutInflater());
                binding.rvPagos.setAdapter(adapter);
                binding.rvPagos.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });


        // El VM maneja la extraccion y validacion del Bundle
        vm.cargarPagos(getArguments());

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}