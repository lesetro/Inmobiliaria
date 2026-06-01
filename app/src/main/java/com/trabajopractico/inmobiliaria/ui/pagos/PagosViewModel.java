package com.trabajopractico.inmobiliaria.ui.pagos;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.trabajopractico.inmobiliaria.modelo.Pago;
import com.trabajopractico.inmobiliaria.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagosViewModel extends AndroidViewModel {

    private MutableLiveData<List<Pago>> listaPagos = new MutableLiveData<>();
    private MutableLiveData<String> mensajeMutable = new MutableLiveData<>();

    public PagosViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Pago>> getListaPagos() { return listaPagos; }

    public LiveData<String> getMensaje() { return mensajeMutable; }

    // El Fragment pasa el Bundle completo, el VM extrae y valida el id
    public void cargarPagos(Bundle bundle) {
        if (bundle == null) return;
        int idContrato = bundle.getInt("idContrato", 0);
        if (idContrato <= 0) return;
        obtenerPagos(idContrato);
    }

    public void obtenerPagos(int idContrato) {
        String token = ApiClient.tokenBearer(getApplication());
        ApiClient.MiServicioInmobiliaria api = ApiClient.getServicio();
        Call<List<Pago>> call = api.obtenerPagosPorContrato(token, idContrato);

        call.enqueue(new Callback<List<Pago>>() {
            @Override
            public void onResponse(Call<List<Pago>> call, Response<List<Pago>> response) {
                if (response.isSuccessful()) {
                    listaPagos.postValue(response.body());
                } else {
                    mensajeMutable.postValue("No se obtuvieron pagos");
                    Log.d("ERROR", "codigo: " + response.code());
                    Log.d("ERROR", "mensaje: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Pago>> call, Throwable t) {
                mensajeMutable.postValue("Error de conexión");
            }
        });
    }
}