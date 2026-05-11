package com.trabajopractico.inmobiliaria.ui.perfil;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.trabajopractico.inmobiliaria.modelo.Propietario;
import com.trabajopractico.inmobiliaria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {

    private static final String TAG = "Perfil";
    private MutableLiveData<Propietario> propietarioMutable;

    public PerfilViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Propietario> getPropietarioMutable() {
        if (propietarioMutable == null) {
            propietarioMutable = new MutableLiveData<>();
        }
        return propietarioMutable;
    }

    public void cargarPerfil() {
        try {
            String token = ApiClient.tokenBearer(getApplication());
            ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
            Call<Propietario> call = servicio.obtenerPerfil(token);

            call.enqueue(new Callback<Propietario>() {
                @Override
                public void onResponse(Call<Propietario> call, Response<Propietario> response) {
                    if (response.isSuccessful()) {
                        Propietario p = response.body();
                        if (p != null) {
                            propietarioMutable.postValue(p);
                            Log.d(TAG, "Perfil cargado correctamente");
                        }
                    } else {
                        Log.e(TAG, "Error en respuesta: " + response.message());
                        propietarioMutable.postValue(null);
                    }
                }

                @Override
                public void onFailure(Call<Propietario> call, Throwable t) {
                    Log.e(TAG, "Error en la llamada: " + t.getMessage());
                    propietarioMutable.postValue(null);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Excepción al cargar perfil: " + e.getMessage());
            propietarioMutable.postValue(null);
        }
    }

}