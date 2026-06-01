package com.trabajopractico.inmobiliaria.ui.inquilinoDetalle;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.trabajopractico.inmobiliaria.modelo.Contrato;
import com.trabajopractico.inmobiliaria.modelo.Inmueble;
import com.trabajopractico.inmobiliaria.modelo.Inquilino;
import com.trabajopractico.inmobiliaria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InquilinoDetalleViewModel extends AndroidViewModel {

    private MutableLiveData<Inquilino> inquilinoMutable;
    private MutableLiveData<String> mensajeMutable;

    public InquilinoDetalleViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getMensaje() {
        if (mensajeMutable == null) mensajeMutable = new MutableLiveData<>();
        return mensajeMutable;
    }

    public LiveData<Inquilino> getInquilinoMutable() {
        if (inquilinoMutable == null) {
            inquilinoMutable = new MutableLiveData<>();
        }
        return inquilinoMutable;
    }

    // Lee el inmueble del Bundle, busca su contrato y de ahi extrae el inquilino
    public void cargarInquilino(Bundle bundle) {
        if (bundle == null) return;
        Inmueble inmueble = (Inmueble) bundle.getSerializable("inmueble");
        if (inmueble == null) return;

        String token = ApiClient.tokenBearer(getApplication());
        ApiClient.MiServicioInmobiliaria api = ApiClient.getServicio();
        Call<Contrato> call = api.obtenerContratoPorInmueble(token, inmueble.getIdInmueble());

        call.enqueue(new Callback<Contrato>() {
            @Override
            public void onResponse(Call<Contrato> call, Response<Contrato> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Contrato contrato = response.body();
                    if (contrato.getInquilino() != null) {
                        inquilinoMutable.postValue(contrato.getInquilino());
                    } else {
                        mensajeMutable.postValue("El contrato no tiene inquilino asociado");
                    }
                } else {
                    mensajeMutable.postValue("No se obtuvo el contrato");
                    Log.d("ERROR", "codigo: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Contrato> call, Throwable t) {
                mensajeMutable.postValue("Error de conexión");
            }
        });
    }
}