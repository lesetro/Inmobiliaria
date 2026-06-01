package com.trabajopractico.inmobiliaria;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.trabajopractico.inmobiliaria.modelo.Contrato;
import com.trabajopractico.inmobiliaria.modelo.Inmueble;
import com.trabajopractico.inmobiliaria.modelo.Pago;
import com.trabajopractico.inmobiliaria.modelo.Propietario;
import com.trabajopractico.inmobiliaria.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    private MutableLiveData<Integer> conteoInicial;
    private MutableLiveData<Integer> pagosNuevos;
    private MutableLiveData<Propietario> propietarioMutable;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Integer> getConteoInicial() {
        if (conteoInicial == null) conteoInicial = new MutableLiveData<>();
        return conteoInicial;
    }

    public LiveData<Integer> getPagosNuevos() {
        if (pagosNuevos == null) pagosNuevos = new MutableLiveData<>();
        return pagosNuevos;
    }

    public LiveData<Propietario> getPropietario() {
        if (propietarioMutable == null) propietarioMutable = new MutableLiveData<>();
        return propietarioMutable;
    }

    public void cargarPerfil() {
        String token = ApiClient.tokenBearer(getApplication());
        if (token == null) return;

        ApiClient.getServicio().obtenerPerfil(token)
                .enqueue(new Callback<Propietario>() {
                    @Override
                    public void onResponse(Call<Propietario> call, Response<Propietario> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            propietarioMutable.postValue(response.body());
                        }
                    }
                    @Override
                    public void onFailure(Call<Propietario> call, Throwable t) {
                        Log.e(TAG, "Error al cargar perfil: " + t.getMessage());
                    }
                });
    }

    public void verificarNuevosPagos() {
        String token = ApiClient.tokenBearer(getApplication());
        if (token == null) return;

        ApiClient.getServicio().obtenerInmueblesAlquilados(token)
                .enqueue(new Callback<List<Inmueble>>() {
                    @Override
                    public void onResponse(Call<List<Inmueble>> call, Response<List<Inmueble>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            int idInmueble = response.body().get(0).getIdInmueble();
                            obtenerContrato(token, idInmueble);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Inmueble>> call, Throwable t) {
                        Log.e(TAG, "Error al obtener inmuebles: " + t.getMessage());
                    }
                });
    }

    private void obtenerContrato(String token, int idInmueble) {
        ApiClient.getServicio().obtenerContratoPorInmueble(token, idInmueble)
                .enqueue(new Callback<Contrato>() {
                    @Override
                    public void onResponse(Call<Contrato> call, Response<Contrato> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int idContrato = response.body().getIdContrato();
                            obtenerPagos(token, idContrato);
                        }
                    }
                    @Override
                    public void onFailure(Call<Contrato> call, Throwable t) {
                        Log.e(TAG, "Error al obtener contrato: " + t.getMessage());
                    }
                });
    }

    private void obtenerPagos(String token, int idContrato) {
        ApiClient.getServicio().obtenerPagosPorContrato(token, idContrato)
                .enqueue(new Callback<List<Pago>>() {
                    @Override
                    public void onResponse(Call<List<Pago>> call, Response<List<Pago>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int totalActual = response.body().size();
                            int almacenado = ApiClient.recuperarConteoPagos(getApplication());

                            if (almacenado == -1) {
                                ApiClient.guardarConteoPagos(getApplication(), totalActual);
                                if (totalActual > 0) {
                                    conteoInicial.postValue(totalActual);
                                }
                            } else if (totalActual > almacenado) {
                                int nuevos = totalActual - almacenado;
                                ApiClient.guardarConteoPagos(getApplication(), totalActual);
                                pagosNuevos.postValue(nuevos);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Pago>> call, Throwable t) {
                        Log.e(TAG, "Error al obtener pagos: " + t.getMessage());
                    }
                });
    }
}
