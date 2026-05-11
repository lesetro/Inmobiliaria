package com.trabajopractico.inmobiliaria.ui.inquilinos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.trabajopractico.inmobiliaria.databinding.FragmentInquilinosBinding;

public class InquilinosFragment extends Fragment {

    private FragmentInquilinosBinding binding;
    private InquilinosViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInquilinosBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(InquilinosViewModel.class);

        vm.getTexto().observe(getViewLifecycleOwner(), texto -> binding.tvInquilinos.setText(texto));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
