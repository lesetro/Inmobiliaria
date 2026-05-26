package com.trabajopractico.inmobiliaria.ui.inmuebleDetalle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.trabajopractico.inmobiliaria.R;
import com.trabajopractico.inmobiliaria.databinding.FragmentInmuebleDetalleBinding;
import com.trabajopractico.inmobiliaria.modelo.Inmueble;
import com.trabajopractico.inmobiliaria.request.ApiClient;

import java.text.NumberFormat;
import java.util.Locale;

public class InmuebleDetalleFragment extends Fragment {

    private InmuebleDetalleViewModel mViewModel;
    private FragmentInmuebleDetalleBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInmuebleDetalleBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(InmuebleDetalleViewModel.class);

        // Observador del inmueble: llena todos los campos cuando hay datos
        mViewModel.getInmuebleMutable().observe(getViewLifecycleOwner(), new Observer<Inmueble>() {
            @Override
            public void onChanged(Inmueble inmueble) {
                if (inmueble != null) {
                    binding.tvDireccionDetalle.setText(inmueble.getDireccion());

                    NumberFormat nf = NumberFormat.getInstance(new Locale("es", "AR"));
                    binding.tvPrecioDetalle.setText("$ " + nf.format(inmueble.getValor()));

                    binding.tvCodigoDetalle.setText("INM-" + String.format("%03d", inmueble.getIdInmueble()));
                    binding.tvAmbientesDetalle.setText(inmueble.getAmbientes() + " ambientes");
                    binding.tvUsoDetalle.setText(inmueble.getUso());
                    binding.tvTipoDetalle.setText(inmueble.getTipo());
                    binding.tvSuperficieDetalle.setText(inmueble.getSuperficie() + " m²");
                    binding.tvLatitudDetalle.setText(String.valueOf(inmueble.getLatitud()));
                    binding.tvLongitudDetalle.setText(String.valueOf(inmueble.getLongitud()));

                    binding.chDisponible.setChecked(inmueble.isDisponible());

                    Glide.with(getContext())
                            .load(ApiClient.BASE_URL + inmueble.getImagen())
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.house)
                            .into(binding.ivImagenDetalle);
                }
            }
        });

        // Observers de campos formateados (el VM arma el texto)
        mViewModel.getPrecioMutable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String precio) {
                binding.tvPrecioDetalle.setText(precio);
            }
        });

        mViewModel.getCodigoMutable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String codigo) {
                binding.tvCodigoDetalle.setText(codigo);
            }
        });

        mViewModel.getAmbientesMutable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String ambientes) {
                binding.tvAmbientesDetalle.setText(ambientes);
            }
        });

        mViewModel.getSuperficieMutable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String superficie) {
                binding.tvSuperficieDetalle.setText(superficie);
            }
        });

        // Observer del texto de disponibilidad (el VM decide el texto)
        mViewModel.getTextoDisponibilidad().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String mensaje) {
                binding.tvInmuebleDisponibilidad.setText(mensaje);
            }
        });

        mViewModel.cargarDetalleInmueble(getArguments());

        // Listener del CheckBox: avisa al VM cuando cambia
        binding.chDisponible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.cambiarDisponibilidad(binding.chDisponible.isChecked());
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}