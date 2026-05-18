package com.trabajopractico.inmobiliaria.ui.login;

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

public class LoginViewModel extends AndroidViewModel {

    private MutableLiveData<String> mensaje;
    private MutableLiveData<String> tokenMutable;
    private MutableLiveData<Boolean> isLoadingMutable;
    private MutableLiveData<Boolean> resetExitoso;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mensaje = new MutableLiveData<>();
        tokenMutable = new MutableLiveData<>();
        isLoadingMutable = new MutableLiveData<>();
        resetExitoso = new MutableLiveData<>();
    }

    public LiveData<String> getMensaje() {
        if (mensaje == null) {
            mensaje = new MutableLiveData<>();
        }
        return mensaje;
    }

    public LiveData<String> getToken() {
        return tokenMutable;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingMutable;
    }
    public LiveData<Boolean> getResetExitoso() {return resetExitoso;}

    public void login(String usuario, String clave) {
        // Validar campos vacios
        if (usuario.isEmpty() || clave.isEmpty()) {
            mensaje.setValue("Por favor, complete todos los campos");
            return;
        }
        isLoadingMutable.setValue(true);

        // Implementar la interfaz
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<String> call = servicio.login(usuario, clave);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                isLoadingMutable.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body();
                    // Guardar el token usando el metodo del ApiClient
                    ApiClient.guardarToken(getApplication(), token);
                    tokenMutable.setValue(token);
                    mensaje.setValue("Login exitoso");
                } else {
                    mensaje.setValue("Usuario o contraseña incorrectos");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                isLoadingMutable.setValue(false);
                mensaje.setValue("Error de conexión: " + t.getMessage());
            }
        });

    }
    // Resetear contrasenia: PUT api/propietarios/fix-id3.
    // a la contrasenia por defecto: DEEKQW
    public void resetearContrasenia() {
        ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
        Call<ApiClient.ResetResponse> call = servicio.resetearPassword();

        call.enqueue(new Callback<ApiClient.ResetResponse>() {
            @Override
            public void onResponse(Call<ApiClient.ResetResponse> call,
                                   Response<ApiClient.ResetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiClient.ResetResponse body = response.body();
                    Toast.makeText(getApplication(),
                            "Contraseña restablecida. Ingresar con la clave por defecto.",
                            Toast.LENGTH_LONG).show();
                    Log.d("RESET", "message: " + body.message);
                    Log.d("RESET", "email reseteado: " + body.email);
                    resetExitoso.postValue(true);
                } else {
                    Toast.makeText(getApplication(),
                            "Error al resetear contraseña",
                            Toast.LENGTH_LONG).show();
                    Log.d("ERROR", "codigo: " + response.code());
                    Log.d("ERROR", "mensaje: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiClient.ResetResponse> call, Throwable t) {
                Toast.makeText(getApplication(), "on failure", Toast.LENGTH_LONG).show();
            }
        });
    }

}