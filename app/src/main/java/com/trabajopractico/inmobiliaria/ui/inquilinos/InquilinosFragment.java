package com.trabajopractico.inmobiliaria.ui.inquilinos;

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
import androidx.recyclerview.widget.GridLayoutManager;

import com.trabajopractico.inmobiliaria.databinding.FragmentInquilinosBinding;
import com.trabajopractico.inmobiliaria.modelo.Inmueble;
import com.trabajopractico.inmobiliaria.ui.inmuebles.InmuebleAdapter;

import java.util.List;

public class InquilinosFragment extends Fragment {

    private InquilinosViewModel mViewModel;
    private FragmentInquilinosBinding binding;

    public static InquilinosFragment newInstance() {
        return new InquilinosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInquilinosBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(InquilinosViewModel.class);

        mViewModel.getListaAlquilados().observe(getViewLifecycleOwner(),
                new Observer<List<Inmueble>>() {
                    @Override
                    public void onChanged(List<Inmueble> inmuebles) {
                        InmuebleAdapter adapter = new InmuebleAdapter(
                                inmuebles,
                                getLayoutInflater(),
                                InmuebleAdapter.Destino.INQUILINO);
                        binding.rvInquilinos.setAdapter(adapter);
                        GridLayoutManager glm = new GridLayoutManager(getContext(), 2,
                                GridLayoutManager.VERTICAL, false);
                        binding.rvInquilinos.setLayoutManager(glm);
                    }
                });

        mViewModel.getMensaje().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        });

        mViewModel.obtenerInmueblesAlquilados();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}