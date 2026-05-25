package com.trabajopractico.inmobiliaria.ui.perfil;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

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
    private MutableLiveData<Boolean> modoEdicionMutable;

    public PerfilViewModel(@NonNull Application application) {

        super(application);
    }

    public LiveData<Propietario> getPropietarioMutable() {
        if (propietarioMutable == null) {
            propietarioMutable = new MutableLiveData<>();
        }
        return propietarioMutable;
    }

    public LiveData<Boolean> getModoEdicionMutable() {
        if (modoEdicionMutable == null) {
            modoEdicionMutable = new MutableLiveData<>();
            modoEdicionMutable.setValue(false);
        }
        return modoEdicionMutable;
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

    // Alterna entre modo lectura y modo edicion.
    // Si esta en lectura -> pasa a edicion (no hace nada con los strings).
    // Si esta en edicion -> guarda los cambios (manda PUT) y vuelve a lectura.
    public void alternarModoEdicion(String nombre, String apellido, String dni,
                                    String telefono, String email) {
        Boolean modoActual = getModoEdicionMutable().getValue();
        if (modoActual == null) modoActual = false;

        if (!modoActual) {
            // Pasar a modo edicion
            modoEdicionMutable.setValue(true);
        } else {
            // Guardar y volver a modo lectura
            actualizarPropietario(nombre, apellido, dni, telefono, email);
            modoEdicionMutable.setValue(false);
        }
    }

    public void actualizarPropietario(String nombre, String apellido, String dni,
                                      String telefono, String email) {
        Propietario p = propietarioMutable.getValue();
        if (p == null) return;

        p.setNombre(nombre);
        p.setApellido(apellido);
        p.setDni(dni);
        p.setTelefono(telefono);
        p.setEmail(email);
        p.setClave(null);

        String token = ApiClient.tokenBearer(getApplication());
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<Propietario> call = servicio.actualizarPerfil(token, p);

        call.enqueue(new Callback<Propietario>() {
            @Override
            public void onResponse(Call<Propietario> call, Response<Propietario> response) {
                if (response.isSuccessful()) {
                    Propietario nuevo = response.body();
                    if (nuevo != null) {
                        propietarioMutable.postValue(nuevo);
                        Toast.makeText(getApplication(), "Actualizacion exitosa",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplication(), "Error", Toast.LENGTH_LONG).show();
                    Log.d("ERROR", "codigo: " + response.code());
                    Log.d("ERROR", "mensaje: " + response.message());
                    Log.d("ERROR", "body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Propietario> call, Throwable t) {
                Toast.makeText(getApplication(), "on failure", Toast.LENGTH_LONG).show();
            }
        });
    }


}