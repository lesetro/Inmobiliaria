package com.trabajopractico.inmobiliaria.ui.inmuebles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.trabajopractico.inmobiliaria.databinding.FragmentInmueblesBinding;
import com.trabajopractico.inmobiliaria.modelo.Inmueble;

import java.util.List;

public class InmueblesFragment extends Fragment {

    private InmueblesViewModel mViewModel;
    private FragmentInmueblesBinding binding;

    public static InmueblesFragment newInstance() {
        return new InmueblesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInmueblesBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(InmueblesViewModel.class);

        mViewModel.getListaInmuebles().observe(getViewLifecycleOwner(), new Observer<List<Inmueble>>() {
            @Override
            public void onChanged(List<Inmueble> inmuebles) {
                InmuebleAdapter adapter = new InmuebleAdapter(inmuebles, getLayoutInflater());
                binding.rvInmuebles.setAdapter(adapter);
                GridLayoutManager glm = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
                binding.rvInmuebles.setLayoutManager(glm);
            }
        });

        mViewModel.obtenerListaInmuebles();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}