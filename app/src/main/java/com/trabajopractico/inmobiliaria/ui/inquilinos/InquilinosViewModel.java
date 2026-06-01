package com.trabajopractico.inmobiliaria.ui.inquilinos;

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

public class InquilinosViewModel extends AndroidViewModel {

    private MutableLiveData<List<Inmueble>> listaAlquilados = new MutableLiveData<>();
    private MutableLiveData<String> mensajeMutable = new MutableLiveData<>();

    public InquilinosViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Inmueble>> getListaAlquilados() { return listaAlquilados; }

    public LiveData<String> getMensaje() { return mensajeMutable; }

    public void obtenerInmueblesAlquilados() {
        String token = ApiClient.tokenBearer(getApplication());
        ApiClient.MiServicioInmobiliaria api = ApiClient.getServicio();
        Call<List<Inmueble>> call = api.obtenerInmueblesAlquilados(token);

        call.enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(Call<List<Inmueble>> call, Response<List<Inmueble>> response) {
                if (response.isSuccessful()) {
                    listaAlquilados.postValue(response.body());
                } else {
                    mensajeMutable.postValue("No se obtuvieron inmuebles alquilados");
                    Log.d("ERROR", "codigo: " + response.code());
                    Log.d("ERROR", "mensaje: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Inmueble>> call, Throwable t) {
                mensajeMutable.postValue("Error de conexión");
            }
        });
    }
}