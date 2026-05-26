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

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InmuebleDetalleViewModel extends AndroidViewModel {

    private MutableLiveData<Inmueble> inmuebleMutable;
    private MutableLiveData<String> textoDisponibilidad;
    private MutableLiveData<String> precioMutable;
    private MutableLiveData<String> codigoMutable;
    private MutableLiveData<String> ambientesMutable;
    private MutableLiveData<String> superficieMutable;

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

    public LiveData<String> getPrecioMutable() {
        if (precioMutable == null) precioMutable = new MutableLiveData<>();
        return precioMutable;
    }

    public LiveData<String> getCodigoMutable() {
        if (codigoMutable == null) codigoMutable = new MutableLiveData<>();
        return codigoMutable;
    }

    public LiveData<String> getAmbientesMutable() {
        if (ambientesMutable == null) ambientesMutable = new MutableLiveData<>();
        return ambientesMutable;
    }

    public LiveData<String> getSuperficieMutable() {
        if (superficieMutable == null) superficieMutable = new MutableLiveData<>();
        return superficieMutable;
    }

    // Carga el detalle del inmueble desde el Bundle recibido del fragment anterior
    public void cargarDetalleInmueble(Bundle bundle) {
        if (bundle == null) return;
        Inmueble inmueble = (Inmueble) bundle.getSerializable("inmueble");
        if (inmueble == null) return;

        inmuebleMutable.setValue(inmueble);
        publicarCamposFormateados(inmueble);

        if (inmueble.isDisponible()) {
            textoDisponibilidad.setValue("Disponible para alquilar");
        } else {
            textoDisponibilidad.setValue("No disponible");
        }
    }

    // Arma los textos formateados y los publica en sus LiveData
    private void publicarCamposFormateados(Inmueble inmueble) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("es", "AR"));
        precioMutable.setValue("$ " + nf.format(inmueble.getValor()));
        codigoMutable.setValue("INM-" + String.format("%03d", inmueble.getIdInmueble()));
        ambientesMutable.setValue(inmueble.getAmbientes() + " ambientes");
        superficieMutable.setValue(inmueble.getSuperficie() + " m²");
    }

    // Cambia la disponibilidad del inmueble y manda el PUT al backend
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