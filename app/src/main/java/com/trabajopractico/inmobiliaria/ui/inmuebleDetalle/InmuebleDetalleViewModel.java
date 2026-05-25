package com.trabajopractico.inmobiliaria.ui.inmuebleDetalle;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.trabajopractico.inmobiliaria.modelo.Inmueble;
import com.trabajopractico.inmobiliaria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InmuebleDetalleViewModel extends AndroidViewModel {

    private MutableLiveData<Inmueble> inmuebleMutable;
    private MutableLiveData<String> textoDisponibilidad;

    public InmuebleDetalleViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Inmueble> getInmuebleMutable() {
        if (inmuebleMutable == null) {
            inmuebleMutable = new MutableLiveData<>();
        }
        return inmuebleMutable;
    }

    public LiveData<String> getTextoDisponibilidad() {
        if (textoDisponibilidad == null) {
            textoDisponibilidad = new MutableLiveData<>();
        }
        return textoDisponibilidad;
    }

    // Carga el detalle del inmueble desde el Bundle recibido del fragment anterior
    public void cargarDetalleInmueble(Bundle bundle) {
        if (bundle != null) {
            Inmueble inmueble = (Inmueble) bundle.getSerializable("inmueble");
            if (inmueble != null) {
                inmuebleMutable.setValue(inmueble);
                if (inmueble.isDisponible()) {
                    textoDisponibilidad.setValue("Disponible para alquilar");
                } else {
                    textoDisponibilidad.setValue("No disponible");
                }
            }
        }
    }

    // Cambia la disponibilidad del inmueble.
    // Trabaja sobre el Inmueble que ya esta en el LiveData,
    // le cambia el campo disponible y manda el PUT al backend.
    public void cambiarDisponibilidad(boolean disponible) {
        Inmueble inmueble = inmuebleMutable.getValue();
        if (inmueble == null) return;

        inmueble.setDisponible(disponible);

        String token = ApiClient.tokenBearer(getApplication());
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<Inmueble> call = servicio.actualizarInmueble(token, inmueble);

        call.enqueue(new Callback<Inmueble>() {
            @Override
            public void onResponse(Call<Inmueble> call, Response<Inmueble> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Inmueble actualizado = response.body();
                    inmuebleMutable.postValue(actualizado);
                    if (actualizado.isDisponible()) {
                        textoDisponibilidad.postValue("Disponible para alquilar");
                    } else {
                        textoDisponibilidad.postValue("No disponible");
                    }
                    Log.d("INMUEBLE_DETALLE", "Disponibilidad cambiada");
                } else {
                    Toast.makeText(getApplication(), "Error", Toast.LENGTH_LONG).show();
                    Log.d("ERROR", "codigo: " + response.code());
                    Log.d("ERROR", "mensaje: " + response.message());
                    Log.d("ERROR", "body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Inmueble> call, Throwable t) {
                Toast.makeText(getApplication(), "on failure", Toast.LENGTH_LONG).show();
            }
        });
    }
}