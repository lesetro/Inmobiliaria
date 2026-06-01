package com.trabajopractico.inmobiliaria.ui.inmuebles;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.trabajopractico.inmobiliaria.modelo.Inmueble;
import com.trabajopractico.inmobiliaria.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InmueblesViewModel extends AndroidViewModel {

    private MutableLiveData<Inmueble> mInmueble;
    private MutableLiveData<List<Inmueble>> listaInmuebles = new MutableLiveData<>();
    private MutableLiveData<String> mensajeMutable = new MutableLiveData<>();

    public InmueblesViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Inmueble> getMInmueble() {
        if (mInmueble == null) {
            mInmueble = new MutableLiveData<>();
        }
        return mInmueble;
    }

    public LiveData<String> getMensaje() { return mensajeMutable; }

    public LiveData<List<Inmueble>> getListaInmuebles() {
        return listaInmuebles;
    }

    public void obtenerListaInmuebles() {
        String token = ApiClient.tokenBearer(getApplication());
        ApiClient.MiServicioInmobiliaria api = ApiClient.getServicio();
        Call<List<Inmueble>> call = api.obtenerInmuebles(token);

        call.enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(Call<List<Inmueble>> call, Response<List<Inmueble>> response) {
                if (response.isSuccessful()) {
                    listaInmuebles.postValue(response.body());
                } else {
                    mensajeMutable.postValue("No se obtuvieron inmuebles");
                }
            }

            @Override
            public void onFailure(Call<List<Inmueble>> call, Throwable throwable) {
                Log.d("ErrorInmueble", "Error al obtener inmuebles", throwable);
                mensajeMutable.postValue("Error de conexión");
            }
        });
    }
}