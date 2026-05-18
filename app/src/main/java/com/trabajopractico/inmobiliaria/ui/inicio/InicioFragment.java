package com.trabajopractico.inmobiliaria.ui.inicio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.SupportMapFragment;
import com.trabajopractico.inmobiliaria.R;
import com.trabajopractico.inmobiliaria.databinding.FragmentInicioBinding;

public class InicioFragment extends Fragment {

    private FragmentInicioBinding binding;
    private InicioViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(InicioViewModel.class);

        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm.getMapaActual().observe(getViewLifecycleOwner(), new Observer<InicioViewModel.MapaActual>() {
            @Override
            public void onChanged(InicioViewModel.MapaActual mapaActual) {
                SupportMapFragment smf = (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.mapaInicio);
                if (smf != null) {
                    smf.getMapAsync(mapaActual);
                }
            }
        });

        vm.cargarMapa();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
