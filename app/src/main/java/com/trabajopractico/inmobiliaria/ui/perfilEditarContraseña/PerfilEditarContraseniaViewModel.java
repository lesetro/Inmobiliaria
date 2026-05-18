package com.trabajopractico.inmobiliaria.ui.perfilEditarContraseña;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.trabajopractico.inmobiliaria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilEditarContraseniaViewModel extends AndroidViewModel {

    private static final String TAG = "PerfilEditarContrasenia";

    // LiveData que avisa al fragment cuando el cambio fue exitoso
    // (para que vuelva al perfil)
    private MutableLiveData<Boolean> cambioExitoso;

    public PerfilEditarContraseniaViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getCambioExitoso() {
        if (cambioExitoso == null) {
            cambioExitoso = new MutableLiveData<>();
        }
        return cambioExitoso;
    }

    // Cambiar contrasenia
    // PUT api/Propietarios/changePassword con currentPassword + newPassword.
    // Devuelve Void (el back no devuelve cuerpo, solo codigo de exito).
    public void cambiarContrasenia(String actual, String nueva) {
        if (actual == null || actual.isEmpty() || nueva == null || nueva.isEmpty()) {
            Toast.makeText(getApplication(), "Complete ambos campos", Toast.LENGTH_LONG).show();
            return;
        }

        String token = ApiClient.tokenBearer(getApplication());
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<Void> call = servicio.cambiarPassword(token, actual, nueva);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplication(), "Contraseña cambiada exitosamente",
                            Toast.LENGTH_LONG).show();
                    cambioExitoso.postValue(true);
                } else {
                    Toast.makeText(getApplication(), "Error al cambiar contraseña",
                            Toast.LENGTH_LONG).show();
                    Log.d("ERROR", "codigo: " + response.code());
                    Log.d("ERROR", "mensaje: " + response.message());
                    Log.d("ERROR", "body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplication(), "on failure", Toast.LENGTH_LONG).show();
            }
        });
    }
}
